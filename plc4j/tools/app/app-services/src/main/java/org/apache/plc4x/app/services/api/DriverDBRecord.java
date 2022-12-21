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
package org.apache.plc4x.app.services.api;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.plc4x.java.api.PlcDriver;


public interface DriverDBRecord {
    
    // Configuration section
    
    public void setProtocolCode(String protocol);   
    public String getProtocolCode();
    
    public void setProtocolName(String name);  
    String getProtocolName();
    
    public void setUUID(UUID uuid);  
    public UUID getUUID();
    
    public PlcDriver getPlcDriver();

    public void setEnable(Boolean enable);
    public Boolean getEnable();    
    
    public int getTransmits();
    public int getReceives();
    public int getErrors();

    public int getNumberOfDevice();
    public int getNumberOfTagGroups();   
    public int getNumberOfTags();
    
    public LocalDateTime getStartDateTime();
    public LocalDateTime getCurrentDateTime();
    public LocalDateTime getLastUpdateDateTime();
   
    
}
