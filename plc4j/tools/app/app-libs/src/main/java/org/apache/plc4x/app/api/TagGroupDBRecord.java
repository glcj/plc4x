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
package org.apache.plc4x.app.api;

import java.time.Instant;
import java.util.Collection;
import java.util.UUID;


public interface TagGroupDBRecord {
    
    
    public void setTagGroupName(String name);  
    public String getTagGroupName();
    
    public void setTagGroupDesc(String desc);   
    public String getTagGroupDesc();    
    
    public void setUUID(UUID uuid);  
    public UUID getUUID();

    public void setScanTime(int ms);
    public int getScanTime();    
    
    public void setEnable(Boolean enable);
    public Boolean getEnable(); 
    
    public Collection<TagDBRecord> getTags();     
    
    public int getJitter();
    
    public int getTransmits();
    public int getReceives();
    public int getErrors();
 
    public int getNumberOfTags();
    
    public Instant getStartInstant();
    public Instant getCurrentInstant();
    public Instant getLastUpdateDateTime();      
    
}
