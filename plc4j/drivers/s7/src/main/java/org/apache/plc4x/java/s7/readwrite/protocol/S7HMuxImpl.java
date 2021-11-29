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
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.AttributeKey;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.apache.plc4x.java.spi.events.ConnectEvent;
import org.apache.plc4x.java.spi.events.ConnectedEvent;
import org.apache.plc4x.java.spi.events.DisconnectedEvent;
import org.apache.plc4x.java.spi.events.DiscoveredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author cgarcia
 */
@Sharable
public class S7HMuxImpl extends MessageToMessageCodec<ByteBuf, ByteBuf> implements S7HMux {

    private static final Logger logger = LoggerFactory.getLogger(S7HMuxImpl.class);  
    final static AttributeKey<Boolean> IS_CONNECTED = AttributeKey.valueOf("IS_CONNECTED");
    final static AttributeKey<Boolean> IS_PRIMARY = AttributeKey.valueOf("IS_PRIMARY");
    
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture scf = null;
    ChannelHandlerContext embed_ctx = null;
    protected Channel tcp_channel = null;
    protected Channel primary_channel = null;     
    protected Channel secondary_channel = null;    

    /*
    * From S7ProcolLogic
    */
    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf outbb, List<Object> list) throws Exception {
        logger.info(">>>> encode: " + ctx.toString());    
        //logger.info("\r\n" + ByteBufUtil.prettyHexDump(outbb));
        if ((embed_ctx == null) && (ctx.channel() instanceof EmbeddedChannel)) embed_ctx = ctx;
        if ((tcp_channel != null)  && (embed_ctx == ctx)){  
            logger.info(">>>>>>>>>>>>>>>>>>>>>>>>>>");
            tcp_channel.writeAndFlush(outbb.copy());
        } else {
            logger.info("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
            list.add(outbb.copy());                        
        }
    }

    /*
    * To S7ProtocolLogic
    */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf inbb, List<Object> list) throws Exception {
        logger.info(">>>> decode: " + ctx.toString());  
        logger.info("+++++++++++++++++++++++++++++++");
        logger.info("\r\n" + ByteBufUtil.prettyHexDump(inbb));        
        embed_ctx.fireChannelRead(inbb.copy());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        logger.info(">>>> channelRegistered(): " + ctx.toString());                
    }

    public void setPrimaryChannel(Channel primary_channel) {
        logger.info(">>>> setPrimaryChannel(): " + primary_channel.toString());         
        if ((this.primary_channel == null)){
            this.primary_channel = primary_channel;
            tcp_channel = primary_channel;              
        } else if ((!this.primary_channel.isActive()) && (tcp_channel == secondary_channel)){
            this.primary_channel = primary_channel;
        }  else if ((!this.primary_channel.isActive()) && (tcp_channel == this.primary_channel)){
            synchronized(tcp_channel) {
                tcp_channel.close();
                this.primary_channel = primary_channel;
                tcp_channel =  primary_channel;
                embed_ctx.channel().attr(IS_PRIMARY).set(true);                
                System.out.println("Asignado primario: " + tcp_channel.isActive());

                if (tcp_channel.isActive()) {
                    embed_ctx.fireUserEventTriggered(new ConnectEvent());                               
                }

            }
        }
        
    }

    @Override
    public void setSecondaryChannel(Channel secondary_channel) {
        logger.info(">>>> setSecondaryChannel(): " + secondary_channel.toString());      
        if ((this.secondary_channel == null) || (tcp_channel == primary_channel)){
            this.secondary_channel = secondary_channel;
        } else if ((!this.secondary_channel.isActive()) && (tcp_channel == primary_channel)){
            this.secondary_channel = secondary_channel;
        }  else if ((!this.secondary_channel.isActive()) && (tcp_channel == this.secondary_channel)){
            synchronized(tcp_channel) {
                this.secondary_channel = secondary_channel;
                tcp_channel =  secondary_channel;
                embed_ctx.channel().attr(IS_PRIMARY).set(false);                 
            }
        }
    }
             

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //super.exceptionCaught(ctx, cause);
        logger.info(">>>> exceptionCaught: " + ctx.toString());
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        super.channelWritabilityChanged(ctx); 
        logger.info(">>>> channelWritabilityChanged: " + ctx.toString());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        super.userEventTriggered(ctx, evt); 
        logger.info(">>>> userEventTriggered: " + ctx.toString() + " : " + evt.getClass());

        if (evt instanceof ConnectedEvent) {
            try {
                tcp_channel.pipeline().remove("watchdog");                 
            } catch (Exception ex){
                logger.info(ex.toString());
            }
            try {
                tcp_channel.pipeline().addFirst("watchdog", new ReadTimeoutHandler(30)); 
                if (tcp_channel.isActive()) {
                    logger.info("Pasando a conectado.");
                    embed_ctx.channel().attr(IS_CONNECTED).set(true);
                 } else {
                    embed_ctx.channel().attr(IS_CONNECTED).set(false);
                }               
            } catch (Exception ex){
                logger.info(ex.toString());                
            }            
        }
        if (evt instanceof DisconnectedEvent) {
            primary_channel.close();
            embed_ctx.close();
        }        
        
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx); 
        logger.info(">>>> channelReadComplete: " + ctx.toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx); 
        logger.info(">>>> channelInactive: " + ctx.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx); 
        logger.info(">>>> channelActive: " + ctx.toString());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx); 
        logger.info(">>>> channelUnregistered: " + ctx.toString()); 
        logger.info("Channel primary   : " + primary_channel.isActive());     

        //TODO: If embedded channel is closed, we need close all channels
        if (ctx == embed_ctx) return;        
        
        if (tcp_channel == ctx.channel())
        embed_ctx.channel().attr(IS_CONNECTED).set(false);
        
                
        if ((tcp_channel == primary_channel) &&
            (primary_channel == ctx.channel()))
        if ((!primary_channel.isActive()) &&
            (secondary_channel != null))
        if (secondary_channel.isActive()){ 
            synchronized(tcp_channel) {
                tcp_channel =  secondary_channel;
                embed_ctx.channel().attr(IS_PRIMARY).set(false);
                embed_ctx.channel().pipeline().fireUserEventTriggered(new ConnectEvent());   
            }          
        } ;


        if ((tcp_channel == secondary_channel) &&
            (secondary_channel == ctx.channel()))
        if ((!secondary_channel.isActive() &&
            (primary_channel.isActive()))) {
            synchronized(tcp_channel) {            
                tcp_channel = primary_channel;
                embed_ctx.channel().attr(IS_PRIMARY).set(true);                
                embed_ctx.channel().pipeline().fireUserEventTriggered(new ConnectEvent());             
            }
        } 
    }
    
}
