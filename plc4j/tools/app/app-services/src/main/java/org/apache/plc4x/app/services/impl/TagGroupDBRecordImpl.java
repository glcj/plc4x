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

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;
import org.apache.plc4x.app.services.api.TagDBRecord;
import org.apache.plc4x.app.services.api.TagGroupDBRecord;


public class TagGroupDBRecordImpl implements TagGroupDBRecord {

    private String groupName;
    private String groupDesc;
    private UUID uuid;   
    
    private Boolean enable = false;  
    
    private int scanTime = 100;
    
    private Instant startInstant;
    private Instant currentInstant;
    private Instant lastUpdateInstant;    
    
    private final HashMap<UUID, TagDBRecord> tags = new HashMap();      
    
    @Override
    public void setTagGroupName(String name) {
        this.groupName = name;
    }

    @Override
    public String getTagGroupName() {
        return groupName;
    }

    @Override
    public void setTagGroupDesc(String desc) {
        this.groupDesc = desc;
    }

    @Override
    public String getTagGroupDesc() {
        return groupDesc;
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
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    @Override
    public Boolean getEnable() {
        return enable;
    }

    @Override
    public void setScanTime(int ms) {
        if (ms < 100) scanTime=100;
        else  scanTime = ms;
    }

    @Override
    public int getScanTime() {
        return scanTime;
    }

    @Override
    public int getJitter() {
        return 0;
    }

    @Override
    public int getTransmits() {
        return 0;
    }

    @Override
    public int getReceives() {
        return 0;
    }

    @Override
    public int getErrors() {
        return 0;
    }

    @Override
    public int getNumberOfTags() {
        return tags.size();
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
