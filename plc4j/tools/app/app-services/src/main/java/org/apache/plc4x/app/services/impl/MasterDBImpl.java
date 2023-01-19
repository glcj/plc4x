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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.plc4x.app.api.MasterDB;
import org.apache.plc4x.java.api.PlcDriver;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;
import org.apache.plc4x.app.api.DeviceRecord;
import org.apache.plc4x.app.api.DriverRecord;
import org.apache.plc4x.app.api.TagRecord;
import org.apache.plc4x.app.api.TagGroupRecord;

@JsonPropertyOrder({"db"})
@ServiceProvider(service=MasterDB.class)
public class MasterDBImpl implements MasterDB, Lookup.Provider, LookupListener {
    
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final HashMap<UUID, DriverRecord> db = new HashMap();    
    private final HashMap<UUID, TagGroupRecord> taggs = new HashMap();
    private final HashMap<UUID, TagRecord> tags = new HashMap();
     
    private final InstanceContent ic;
    private final Lookup lk;
    private final Lookup.Result<PlcDriver> plc4xresult;
    private final Lookup.Result<DriverRecord> dbresult;
    
    private final Lookup.Template drivertemplate = new Lookup.Template(PlcDriver.class);
    private Lookup.Template dbtemplate = new Lookup.Template(DriverRecord.class);    
    
    private UUID tempUuid;

    public MasterDBImpl() {
        ic = new InstanceContent ();
        lk = new AbstractLookup (ic);
        
        //1. Jackson must first try to rebuild the structures from a file
        
        //2. We take the drivers registered in the CLASSPATH
        plc4xresult = Lookup.getDefault().lookup(drivertemplate);
        plc4xresult.addLookupListener(this);
        
        Collection<PlcDriver> drivers = (Collection<PlcDriver>) plc4xresult.allInstances();
        
        drivers.stream().
                forEach(p -> ic.add(new DriverRecordImpl(UUID.randomUUID(), p)));
        
        dbresult = lk.lookup(dbtemplate);
        
        /*
        for (PlcDriver driver:Lookup.getDefault().lookupAll(PlcDriver.class)) {
            tempUuid = UUID.randomUUID();
            addDriver(new DriverRecordImpl(tempUuid, driver));           
        }  
        */
    }
    
    public HashMap<UUID, DriverRecord> getDB(){
        return db;
    } 

    @Override
    public void addDriver(DriverRecord driver) {
        final DriverRecord  tempdrv =  getDriverByCode(driver.getProtocolCode());
        if (null == tempdrv) {
            if(null == driver.getUUID()){
                tempUuid = UUID.randomUUID();
            } else {
                tempUuid = driver.getUUID();
            }
            db.put(tempUuid, driver);
        }

    }

    @Override
    public Optional<DriverRecord> getDriver(UUID uuid) {         
        return  (Optional<DriverRecord>) dbresult.allInstances().stream().
                filter(r -> r.getUUID().equals(uuid)).
                findFirst();
    }

    @Override
    public Optional<DriverRecord> getDriver(String drvname) {
        return  (Optional<DriverRecord>) dbresult.allInstances().stream().
                filter(r -> r.getProtocolCode().equals(drvname)).
                findFirst();
    }

    @Override
    public Collection<DriverRecord> getDrivers() {
        return (Collection<DriverRecord>) dbresult.allInstances();

    }
    
    @Override
    public void removeDriver(UUID uuid) {
        Optional<DriverRecord> oprecord = getDriver(uuid);
        if (oprecord.isPresent()) ic.remove(oprecord.get());        
    }

    @Override
    public void addDevice(UUID driver, DeviceRecord device) {
        Optional<DriverRecord> opdriver = getDriver(driver);
        if (opdriver.isPresent()) {
            opdriver.get().addDevice(device);
        }
    }

    @Override
    public Optional<DeviceRecord> getDevice(UUID uuid) {       
        Optional<DriverRecord> opdriver = 
                (Optional<DriverRecord>) dbresult.allInstances().stream().
                filter(drv -> ((Optional<DeviceRecord>) drv.getDevice(uuid)).isPresent()).
                findFirst();
        
        return opdriver.isPresent() ? opdriver.get().getDevice(uuid):  Optional.empty();
        
    }

    @Override
    public Optional<DeviceRecord> getDevice(String devicename) {
         Optional<DriverRecord> opdriver = 
                (Optional<DriverRecord>) dbresult.allInstances().stream().
                filter(drv -> ((Optional<DeviceRecord>) drv.getDevice(devicename)).isPresent()).
                findFirst();
        
        return opdriver.isPresent() ? opdriver.get().getDevice(devicename):  Optional.empty();
    }
   
     
    @Override
    public void removeDevice(DeviceRecord device) {
        removeDevice(device.getUUID());
    }

    @Override
    public void removeDevice(UUID uuid) {
        Optional<DriverRecord> opdriver = (Optional<DriverRecord>) dbresult.allInstances().stream().
                filter(drv -> drv.getDevice(uuid).equals(uuid)).
                findFirst();              
        if (opdriver.isPresent()) {
            Optional<DeviceRecord> opdev = opdriver.get().getDevice(uuid);
            if (opdev.isPresent()) 
                if (!opdev.get().getEnable()){
                    opdev.get().getTagGroups().stream().
                            forEach(tg -> {
                                taggs.remove(tg.getUUID());
                                removeTagGroup(tg.getUUID());
                                });
                    if (opdev.get().getTagGroups().isEmpty()) 
                        opdriver.get().removeDevice(opdev.get());
                    opdev = null;
            };
        }         
    }

    @Override
    public void addTagGroup(UUID device, TagGroupRecord taggroup) {
        Optional<DeviceRecord> opdevice = getDevice(device);
        if (opdevice.isPresent()){
            opdevice.get().addTagGroup(taggroup);
            taggs.put(taggroup.getUUID(), taggroup);
        } else {
            System.out.println("Dispositivo no encontrado: " + device.toString());
        }
    }

    @Override
    public Optional<TagGroupRecord> getTagGroup(UUID uuid) {
        return taggs.values().stream().
                filter(t -> t.getUUID().equals(uuid)).
                findFirst(); 
    }


    @Override
    public Optional<TagGroupRecord> getTagGroup(String taggname) {
        return taggs.values().stream().
                filter(t -> t.getTagGroupName().equals(taggname)).
                findFirst(); 
    }
    
    @Override
    public void removeTagGroup(TagGroupRecord taggroup) {
        removeTagGroup(taggroup.getUUID());
    }

    @Override
    public void removeTagGroup(UUID uuid) {
        Optional<TagGroupRecord> opTagGroup = getTagGroup(uuid);
        if (opTagGroup.isPresent())
            if (!opTagGroup.get().getEnable()) {
                Optional<TagRecord> optag = opTagGroup.get().getTags().stream().
                        filter(t -> t.getEnable()).
                        findFirst();
            if (!optag.isPresent()) {
                taggs.remove(opTagGroup.get());
                opTagGroup.get().getTags().stream().forEach(t -> tags.remove(t.getUUID()));
                Optional<DeviceRecord> opdevice = getDevice(opTagGroup.get().getDeviceRecord());
                if (opdevice.isPresent()) opdevice.get().removeTagGroup(opTagGroup.get());
                opTagGroup = null;
            }
        }
    }

    @Override
    public void addTag(UUID taggroup, TagRecord tag) {
        Optional<TagGroupRecord> optagg = getTagGroup(taggroup);
        if (optagg.isPresent()){
            optagg.get().addTag(tag);
            tags.put(tag.getUUID(), tag);
        }
        
    }

    @Override
    public Optional<TagRecord> getTag(UUID uuid) {
        return tags.values().stream().
                filter(t -> t.getUUID().equals(uuid)).
                findFirst();              
    }

    @Override
    public Optional<TagRecord> getTag(String tagname) {
        return tags.values().stream().
                filter(t -> t.getTagName().equals(tagname)).
                findFirst();           
    }

    @Override
    public void removeTag(TagRecord tag) {
        Optional<TagRecord> optag = getTag(tag.getUUID());
        if (optag.isPresent()) {
            tags.remove(tag.getUUID());
            Optional<TagGroupRecord> optagg = getTagGroup(tag.getTagGroup());
                if (optagg.isPresent()) {
                    optagg.get().removeTag(tag);
                }
            optag =null; //To GC?            
        }
    }
         
    @Override
    public void removeTag(UUID uuid) {
        Optional<TagRecord> optag = getTag(uuid);
        if (optag.isPresent()){
            if (!optag.get().getEnable()) {
                tags.remove(optag.get().getUUID());
                Optional<TagGroupRecord> optagg = getTagGroup(optag.get().getTagGroup());
                if (optagg.isPresent()) {
                    optagg.get().removeTag(optag.get());
                }
                optag = null; //To GC?
            }
        }      
    }
    
    @JsonIgnore    
    @Override
    public List<String> getDriverCodes() {
        return  dbresult.allInstances().stream()
                .filter(e -> true)
                .map(driver -> driver.getProtocolCode())
                .collect(Collectors.toList());            
    }

    @JsonIgnore    
    @Override
    public List<String> getDriverNames() {
        return  dbresult.allInstances().stream()
                .filter(e -> true)
                .map(driver -> driver.getProtocolName())
                .collect(Collectors.toList());  
    }

    @JsonIgnore
    @Override
    public DriverRecord getDriverByCode(String code) {
        Optional<DriverRecord> oprecord = (Optional<DriverRecord>) 
                dbresult.allInstances().stream()
                .filter(r ->  r.getPlcDriver().getProtocolCode().equals(code))
                .findFirst();

        return oprecord.isPresent()?oprecord.get():null;
    }

    @JsonIgnore
    @Override
    public DriverRecord getDriverByName(String name) {
        Optional<DriverRecord> oprecord = (Optional<DriverRecord>) 
                dbresult.allInstances().stream()
                .filter(r ->  r.getPlcDriver().getProtocolName().equals(name))
                .findFirst();
        if (oprecord.isPresent()) return oprecord.get();
        return null;
    }    

    @Override
    public DeviceRecord createDeviceDBRecord() {
        return new DeviceRecordImpl();
    }

    @Override
    public TagGroupRecord createTagGroupDBRecord() {
        UUID uuid = UUID.randomUUID();
        return new TagGroupRecordImpl(uuid);
    }

    @Override
    public TagRecord createTagDBRecord() {
        UUID uuid = UUID.randomUUID();        
        return new TagRecordImpl(uuid);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        this.pcs.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
         this.pcs.removePropertyChangeListener(listener);
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
        dbresult.allInstances().stream().
                 forEach(drv -> n[0] += drv.getNumberOfDevice());
        return n[0];
    }

    @JsonIgnore
    @Override
    public int getNumberOfTagGroups() {
        int[] n = new int[1];
        n[0] = 0;
        dbresult.allInstances().stream().
                 forEach(drv -> n[0] += drv.getNumberOfTagGroups());
        return n[0];
    }
    
    @JsonIgnore
    @Override
    public int getNumberOfTags() {
        int[] n = new int[1];
        n[0] = 0;
        dbresult.allInstances().stream().
                 forEach(drv -> n[0] += drv.getNumberOfTags());
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

    @JsonIgnore
    @Override
    public Lookup getLookup() {
        return lk;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        System.out.println("Evento: " + ev.toString());
    }


    
}
