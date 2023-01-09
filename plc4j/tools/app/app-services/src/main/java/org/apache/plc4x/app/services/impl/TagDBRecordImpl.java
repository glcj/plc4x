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
import java.util.UUID;
import org.apache.plc4x.app.api.TagDBRecord;


/**
 *
 * @author cgarcia
 */
public class TagDBRecordImpl implements TagDBRecord {

    private String name;
    private String desc;
    private String id;
    
    private UUID uuid;   

    private Boolean enable = false;    
    private Boolean disableOutput = true;   
    
    private int transmits = 0;
    private int receives = 0;
    private int errors = 0;  

    private Instant startInstant;
    private Instant currentInstant;
    private Instant lastUpdateInstant;      
    
    @Override
    public void setTagName(String name) {
        this.name = name;
    }

    @Override
    public String getTagName() {
        return name;
    }

    @Override
    public void setTagDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public String getTagDesc() {
        return desc;
    }

    @Override
    public void setTagID(String id) {
        this.id = id;
    }

    @Override
    public String getTagID() {
        return id;
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
    public void setDisableOutput(Boolean disableOutput) {
        this.disableOutput = disableOutput;
    }

    @Override
    public Boolean getDisableOutput() {
        return disableOutput;
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
    public Instant getLastReadInstant() {
        return startInstant;
    }

    @Override
    public Instant getLastWriteInstant() {
        return currentInstant; 
    }

    @Override
    public Instant getLastErrorInstant() {
        return lastUpdateInstant;
    }
    
}
