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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.plc4x.app.services.api.DriverDBRecord;
import org.apache.plc4x.app.services.api.MasterDB;
import org.apache.plc4x.java.api.PlcDriver;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

@JsonPropertyOrder({"db"})
@ServiceProvider(service=MasterDB.class)
public class MasterDBImpl implements MasterDB {

    private final HashMap<UUID, DriverDBRecord> db = new HashMap();
    private UUID tempUuid;

    public MasterDBImpl() {
        //1. Jackson must first try to rebuild the structures from a file
        
        //2. We take the drivers registered in the CLASSPATH
        for (PlcDriver driver:Lookup.getDefault().lookupAll(PlcDriver.class)) {
            tempUuid = UUID.randomUUID();
            db.put(tempUuid, new DriverDBRecordImpl(tempUuid, driver));           
        }        
    }
    
    public HashMap<UUID, DriverDBRecord> getDB(){
        return db;
    } 
    
    @JsonIgnore    
    @Override
    public List<String> getDriverCodes() {
        return db.entrySet().stream()
                .filter(e -> true)
                .map(name -> name.getValue().getPlcDriver().getProtocolCode())
                .collect(Collectors.toList());              
    }

    @JsonIgnore    
    @Override
    public List<String> getDriverNames() {
        return db.entrySet().stream()
                .filter(e -> true)
                .map(name -> name.getValue().getPlcDriver().getProtocolName())
                .collect(Collectors.toList());  
    }

    @JsonIgnore
    @Override
    public DriverDBRecord getDriverByCode(String code) {
        return db.entrySet().stream()
                .filter(e -> e.getValue().getPlcDriver().getProtocolCode().equals(code))
                .findFirst().get().getValue();
    }

    @JsonIgnore
    @Override
    public DriverDBRecord getDriverByName(String name) {
        return db.entrySet().stream()
                .filter(e -> e.getValue().getPlcDriver().getProtocolName().equals(name))
                .findFirst().get().getValue();
    }    
    
    @JsonIgnore
    @Override
    public int getNumberOfDrivers() {
        return db.size();
    }
    
    @JsonIgnore
    @Override
    public int getNumberOfDevice() {
        int[] n = new int[1];
        n[0] = 0;
        db.entrySet().stream()
                .forEach(drv -> n[0] += drv.getValue().getNumberOfDevice());
        return n[0];
    }

    @JsonIgnore
    @Override
    public int getNumberOfTagGroups() {
        int[] n = new int[1];
        n[0] = 0;
        db.entrySet().stream()
                .forEach(drv -> n[0] += drv.getValue().getNumberOfTagGroups());
        return n[0];
    }
    
    @JsonIgnore
    @Override
    public int getNumberOfTags() {
        int[] n = new int[1];
        n[0] = 0;
        db.entrySet().stream()
                .forEach(drv -> n[0] += drv.getValue().getNumberOfTags());
        return n[0];
    }

    @JsonIgnore
    @Override
    public int getTransmits() {
        return 0;
    }

    @JsonIgnore
    @Override
    public int getReceives() {
        return 0;
    }

    @JsonIgnore
    @Override
    public int getErrors() {
        return 0;
    }


    
}
