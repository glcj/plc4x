/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.plc4x.app.services.impl;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;
import org.apache.plc4x.app.services.api.DeviceDBRecord;
import org.apache.plc4x.app.services.api.DriverDBRecord;
import org.apache.plc4x.java.api.PlcDriver;

@JsonPropertyOrder({ "protocolCode",
    "protocolName",
    "uuid",
    "enable",
    "devices"})
@JsonIgnoreProperties(value = { "plcdriver",
    "transmits",
    "receives",
    "errors",
    "startInstant",
    "currentInstant",
    "lastUpdateInstant"})
public class DriverDBRecordImpl implements DriverDBRecord {
       
    private String protocolCode;
    private String protocolName;
    private UUID uuid;
    
    private Boolean enable = false;    
    
    private PlcDriver plcdriver = null;
    
    private int transmits = 0;
    private int receives = 0;
    private int errors = 0;
    
    private Instant startInstant;
    private Instant currentInstant;
    private Instant lastUpdateInstant;
    
    @JsonIgnore
    private final HashMap<UUID, DeviceDBRecord> devices = new HashMap();    
    
    
    
    public DriverDBRecordImpl() {
    }    

    public DriverDBRecordImpl(UUID uuid, PlcDriver plcdriver) {
        this.uuid = uuid;
        this.plcdriver = plcdriver;
    }

    @Override
    public void setProtocolCode(String protocol) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public String getProtocolCode() {
        return plcdriver.getProtocolCode();
    }

    @Override
    public void setProtocolName(String name) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public String getProtocolName() {
        return plcdriver.getProtocolName();
    }

    @Override
    public void setUUID(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }
    
    @JsonIgnore
    @Override
    public PlcDriver getPlcDriver() {
        return plcdriver;
    }    

    @Override
    public void setEnable(Boolean enable) {
        this.enable = enable; 
    }

    @Override
    public Boolean getEnable() {
        return enable;
    }

    @Override
    public int getTransmits() {
        return transmits;
    }

    @Override
    public int getReceives() {
        return receives;
    }

    @Override
    public int getErrors() {
        return errors;
    }

    @Override
    public int getNumberOfDevice() {
        return devices.size();
    }

    @Override
    public int getNumberOfTagGroups() {
        int[] n = new int[1];
        n[0] = 0;
        devices.entrySet().stream()
                .forEach(drv -> n[0] += drv.getValue().getNumberOfTagGroups());
        return n[0];
    }

    @Override
    public int getNumberOfTags() {
        int[] n = new int[1];
        n[0] = 0;
        devices.entrySet().stream()
                .forEach(drv -> n[0] += drv.getValue().getNumberOfTags());
        return n[0];
    }

    @Override
    public Instant getStartInstant() {
        return startInstant;
    }

    @Override
    public Instant getCurrentInstant() {
        return currentInstant;
    }

    @Override
    public Instant getLastUpdateInstant() {
        return lastUpdateInstant;
    }

    
}
