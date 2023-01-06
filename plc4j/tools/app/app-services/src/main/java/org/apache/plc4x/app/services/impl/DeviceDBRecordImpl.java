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
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.plc4x.app.services.api.DeviceDBRecord;
import org.apache.plc4x.app.services.api.TagGroupDBRecord;

@JsonPropertyOrder({ "deviceName",
    "deviceDesc",
    "protocolCode",
    "uuid",
    "treenode",
    "enable",
    "properties",
    "tagg"})
public class DeviceDBRecordImpl implements DeviceDBRecord {
    

    private String deviceName;
    private String deviceDesc;
    private UUID protocolCode;    
    private UUID uuid; 
    private UUID treenode;        
    
    private Boolean enable = false;   
    
    private int transmits = 0;
    private int receives = 0;
    private int errors = 0;    
    
    private Instant startInstant;
    private Instant currentInstant;
    private Instant lastUpdateInstant;    
    
    private Map<String, String> properties = new HashMap<String, String>();
    
    @JsonIgnore
    private final HashMap<UUID, TagGroupDBRecord> tagg = new HashMap();      



    @Override
    public void setDeviceName(String name) {
        this.deviceName = name;
    }

    @Override
    public String getDeviceName() {
        return deviceName;
    }
    
    @Override
    public void setDeviceDescription(String desc) {
        this.deviceDesc = desc;
    }

    @Override
    public String getDeviceDescription() {
        return deviceDesc;
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
    public void setProtocolCode(UUID protocol) {
        this.protocolCode = protocol;
    }

    @Override
    public UUID getProtocolCode() {
        return protocolCode;
    }  
    
    @Override    
    public void setTreeLocation(UUID treenode) {
        this.treenode = treenode;
    }   
    
    @Override    
    public UUID getTreeLocation() {
        return treenode;
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
    public void setPropertie(String id, String str) {
        properties.put(id, str);
    }

    @Override
    public String getPropertie(String id) {
        return properties.get(id);
    }

    @Override
    public Map<String, String> getProperties() {
        return properties;
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
    public int getNumberOfTagGroups() {
        return tagg.size();
    }

    @Override
    public int getNumberOfTags() {
        int[] ntags = new int[1];
        tagg.entrySet().stream()
            .forEach(item -> ntags[0] += item.getValue().getNumberOfTags());
        return ntags[0];
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
    public Instant getLastUpdateDateTime() {
        return lastUpdateInstant;
    }
    
}
