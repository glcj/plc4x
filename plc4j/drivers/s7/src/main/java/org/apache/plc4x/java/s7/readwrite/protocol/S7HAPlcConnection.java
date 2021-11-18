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

import io.netty.channel.Channel;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import org.apache.plc4x.java.api.EventPlcConnection;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.api.exceptions.PlcInvalidFieldException;
import org.apache.plc4x.java.api.listener.EventListener;
import org.apache.plc4x.java.api.messages.PlcBrowseRequest;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;
import org.apache.plc4x.java.api.messages.PlcSubscriptionEvent;
import org.apache.plc4x.java.api.messages.PlcSubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcSubscriptionResponse;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionRequest;
import org.apache.plc4x.java.api.messages.PlcUnsubscriptionResponse;
import org.apache.plc4x.java.api.messages.PlcWriteRequest;
import org.apache.plc4x.java.api.messages.PlcWriteResponse;
import org.apache.plc4x.java.api.metadata.PlcConnectionMetadata;
import org.apache.plc4x.java.api.model.PlcConsumerRegistration;
import org.apache.plc4x.java.api.model.PlcField;
import org.apache.plc4x.java.api.model.PlcSubscriptionHandle;
import org.apache.plc4x.java.api.value.PlcValueHandler;
import org.apache.plc4x.java.s7.readwrite.S7Driver;
import org.apache.plc4x.java.spi.Plc4xProtocolBase;
import org.apache.plc4x.java.spi.connection.AbstractPlcConnection;
import org.apache.plc4x.java.spi.connection.ChannelExposingConnection;
import org.apache.plc4x.java.spi.connection.PlcFieldHandler;

/**
 *
 * @author cgarcia
 */
public class S7HAPlcConnection  extends AbstractPlcConnection implements ChannelExposingConnection, EventPlcConnection  {
    
    private S7Driver master = new S7Driver();
    private PlcConnection master_connection = null;

    public S7HAPlcConnection(String connectionString) {
    }
    
       
    @Override
    public void connect() throws PlcConnectionException {
        master_connection.connect();
    }

    @Override
    public boolean isConnected() {
        return (master_connection.isConnected());
    }

    @Override
    public void close() throws Exception {
        master_connection.close();
    }

    @Override
    public Channel getChannel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addEventListener(EventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeEventListener(EventListener listener) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void unregister(PlcConsumerRegistration registration) {
        super.unregister(registration); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcConsumerRegistration register(Consumer<PlcSubscriptionEvent> consumer, Collection<PlcSubscriptionHandle> handles) {
        return super.register(consumer, handles); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CompletableFuture<PlcUnsubscriptionResponse> unsubscribe(PlcUnsubscriptionRequest unsubscriptionRequest) {
        return super.unsubscribe(unsubscriptionRequest); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CompletableFuture<PlcSubscriptionResponse> subscribe(PlcSubscriptionRequest subscriptionRequest) {
        return super.subscribe(subscriptionRequest); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CompletableFuture<PlcWriteResponse> write(PlcWriteRequest writeRequest) {
        return super.write(writeRequest); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public CompletableFuture<PlcReadResponse> read(PlcReadRequest readRequest) {
        return super.read(readRequest); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcUnsubscriptionRequest.Builder unsubscriptionRequestBuilder() {
        return super.unsubscriptionRequestBuilder(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcSubscriptionRequest.Builder subscriptionRequestBuilder() {
        return super.subscriptionRequestBuilder(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcWriteRequest.Builder writeRequestBuilder() {
        return super.writeRequestBuilder(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcReadRequest.Builder readRequestBuilder() {
        return master_connection.readRequestBuilder();
    }

    @Override
    public PlcValueHandler getPlcValueHandler() {
        return super.getPlcValueHandler(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcFieldHandler getPlcFieldHandler() {
        return super.getPlcFieldHandler(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canSubscribe() {
        return super.canSubscribe(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canWrite() {
        return true;
    }

    @Override
    public boolean canRead() {
        return true;
    }

    @Override
    public CompletableFuture<Void> ping() {
        return super.ping(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcConnectionMetadata getMetadata() {
        return super.getMetadata(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProtocol(Plc4xProtocolBase<?> protocol) {
        super.setProtocol(protocol); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcBrowseRequest.Builder browseRequestBuilder() {
        return super.browseRequestBuilder(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlcField prepareField(String fieldQuery) throws PlcInvalidFieldException {
        return super.prepareField(fieldQuery); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean canBrowse() {
        return super.canBrowse(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        return super.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int hashCode() {
        return super.hashCode(); //To change body of generated methods, choose Tools | Templates.
    }





    
}
