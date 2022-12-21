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

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.UUID;
import org.apache.plc4x.app.services.api.DeviceDBRecord;
import org.apache.plc4x.app.services.api.DriverDBRecord;
import org.apache.plc4x.java.api.PlcDriver;


public class DriverDBRecordImpl implements DriverDBRecord {
       
    private String protocolCode;
    private String protocolName;
    private UUID uuid;
    
    private PlcDriver plcdriver = null;

    private Boolean enable = false;
    
    private int transmits = 0;
    private int receives = 0;
    private int errors = 0;
    
    private LocalDateTime startDateTime;
    private LocalDateTime currentDateTime;
    private LocalDateTime lastUpdateDateTime;
    
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
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getNumberOfTags() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public LocalDateTime getStartDateTime() {
        return startDateTime;
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        return currentDateTime;
    }

    @Override
    public LocalDateTime getLastUpdateDateTime() {
        return lastUpdateDateTime;
    }

    
}
