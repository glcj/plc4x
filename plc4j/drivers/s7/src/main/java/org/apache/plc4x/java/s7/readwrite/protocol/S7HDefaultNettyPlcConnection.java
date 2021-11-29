/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.plc4x.java.s7.readwrite.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcIoException;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.value.PlcValueHandler;
import org.apache.plc4x.java.spi.configuration.Configuration;
import org.apache.plc4x.java.spi.configuration.ConfigurationFactory;
import org.apache.plc4x.java.spi.connection.ChannelFactory;
import org.apache.plc4x.java.spi.connection.DefaultNettyPlcConnection;
import org.apache.plc4x.java.spi.connection.PlcFieldHandler;
import org.apache.plc4x.java.spi.connection.ProtocolStackConfigurer;
import org.apache.plc4x.java.spi.events.ConnectedEvent;
import org.apache.plc4x.java.spi.optimizer.BaseOptimizer;
import org.apache.plc4x.java.transport.tcp.TcpChannelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
public class S7HDefaultNettyPlcConnection extends DefaultNettyPlcConnection implements Runnable{
    
    private static final Logger logger = LoggerFactory.getLogger(S7HDefaultNettyPlcConnection.class);    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scf = null;
    
    protected final ChannelFactory secondaryChannelFactory;    
    protected Channel primary_channel = null;    
    protected Channel secondary_channel = null;    
    protected final MessageToMessageCodec<ByteBuf, ByteBuf> s7hmux;
    
    public S7HDefaultNettyPlcConnection(boolean canRead, 
            boolean canWrite, 
            boolean canSubscribe, 
            PlcFieldHandler fieldHandler, 
            PlcValueHandler valueHandler, 
            Configuration configuration, 
            ChannelFactory channelFactory, 
            ChannelFactory secondaryChannelFactory,            
            boolean awaitSessionSetupComplete, 
            boolean awaitSessionDisconnectComplete, 
            boolean awaitSessionDiscoverComplete, 
            ProtocolStackConfigurer stackConfigurer, 
            BaseOptimizer optimizer) {
        super(canRead, 
                canWrite, 
                canSubscribe, 
                fieldHandler, 
                valueHandler, 
                configuration, 
                channelFactory, 
                awaitSessionSetupComplete, 
                awaitSessionDisconnectComplete, 
                awaitSessionDiscoverComplete, 
                stackConfigurer, 
                optimizer);
        this.secondaryChannelFactory = secondaryChannelFactory;
        this.s7hmux = new S7HMuxImpl();
    }

    @Override
    public void connect() throws PlcConnectionException {
        try {
            // As we don't just want to wait till the connection is established,
            // define a future we can use to signal back that the s7 session is
            // finished initializing.
            CompletableFuture<Void> sessionSetupCompleteFuture = new CompletableFuture<>();
            CompletableFuture<Configuration> sessionDiscoveredCompleteFuture = new CompletableFuture<>();

            if(channelFactory == null) {
                throw new PlcConnectionException("No primary channel factory provided");
            }

            // Inject the configuration
            ConfigurationFactory.configure(configuration, channelFactory);
            
            if (secondaryChannelFactory != null )
            ConfigurationFactory.configure(configuration, secondaryChannelFactory);    
            
            channel = new EmbeddedChannel(getChannelHandler(sessionSetupCompleteFuture, sessionDisconnectCompleteFuture, sessionDiscoveredCompleteFuture));
            channel.pipeline().addFirst(s7hmux);            
            //channel.pipeline().addFirst((new LoggingHandler(LogLevel.INFO)));            
            channel.closeFuture().addListener(future -> {
                if (!sessionSetupCompleteFuture.isDone()) {
                    sessionSetupCompleteFuture.completeExceptionally(
                        new PlcIoException("Connection terminated by remote"));
                }
            });

            doPrimaryTcpConnections();
            
            if (secondaryChannelFactory != null )
            doSecondaryTcpConnections();

            scf = scheduler.scheduleAtFixedRate(this, 4, 4, TimeUnit.SECONDS);
            
            primary_channel.closeFuture().addListener(future -> {
                if (!sessionDiscoveredCompleteFuture.isDone()) {
                    //Do Nothing
                    try {
                        sessionDiscoveredCompleteFuture.complete(null);
                    } catch (Exception e) {
                        //Do Nothing
                    }

                }
            });            
            
            
            
            // Send an event to the pipeline telling the Protocol filters what's going on.
            sendChannelCreatedEvent();            
            
            // Wait till the connection is established.
            if (awaitSessionSetupComplete) {
                sessionSetupCompleteFuture.get();
            }            
            //channel.pipeline().write(new ConnectedEvent());
            // Set the connection to "connected"
            connected = true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PlcConnectionException(e);
        } catch (ExecutionException e) {
            throw new PlcConnectionException(e);
        }            
    }
    
    public void doPrimaryTcpConnections(){
        try {
            primary_channel = channelFactory.createChannel(new LoggingHandler(LogLevel.INFO) );
            primary_channel.pipeline().addFirst(s7hmux);            
           //primary_channel.pipeline().addFirst("watchdog", new ReadTimeoutHandler(4));            
        } catch (Exception ex){
            logger.info(ex.toString());
        }
        ((S7HMux) s7hmux).setPrimaryChannel(primary_channel);        
    }
    
    public void doSecondaryTcpConnections(){
        try {
            secondary_channel = secondaryChannelFactory.createChannel(new LoggingHandler(LogLevel.INFO) );
            secondary_channel.pipeline().addFirst(s7hmux);            
           //primary_channel.pipeline().addFirst("watchdog", new ReadTimeoutHandler(4));            
        } catch (Exception ex){
            logger.info(ex.toString());
        }
        ((S7HMux) s7hmux).setSecondaryChannel(secondary_channel);        
    }    

    @Override
    public void run() {
        if (primary_channel != null){
            if (!primary_channel.isActive()){
                logger.info("**** CREANDO CONEXION PRIMARY ***");
                doPrimaryTcpConnections();                    
            }
        }
        
        if (secondary_channel != null){
            if (!secondary_channel.isActive()){
                logger.info("**** CREANDO CONEXION SECONDARY ***");
                doSecondaryTcpConnections();                 
            }
        }        
        ping();
    }

    @Override
    public CompletableFuture<Void> ping() { 
        logger.info ("PING");
        /*
        CompletableFuture<Void> cf =new CompletableFuture<Void>();
        PlcReadRequest.Builder builder = readRequestBuilder();
        builder.addItem("value", "%MX1.0:BOOL");            
        PlcReadRequest readRequest = builder.build();
        try 
        {
        PlcReadResponse readResponse = readRequest.execute().get(2, TimeUnit.SECONDS);
        } catch (Exception ex){
            logger.info("PING: " + ex.toString());
        }
        */
        return null;

    }
    
      
}
