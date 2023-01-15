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
        if (tempdrv == null) {
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
    public void removeDevice(DeviceRecord device) {
        Optional<DriverRecord> opdriver = (Optional<DriverRecord>) dbresult.allInstances().
                stream().
                filter(drv -> drv.getDevice(device) != null).
                findFirst();              
        if (opdriver.isPresent()) opdriver.get().removeDevice(device);           
    }

    @Override
    public void addTagGroup(UUID device, TagGroupRecord taggroup) {
        Optional<DeviceRecord> opdevice = getDevice(device);
        if (opdevice.isPresent()){
            opdevice.get().addTagGroup(taggroup);            
        } else {
            System.out.println("Dispositivo no encontrado: " + device.toString());
        }
    }

    @Override
    public Optional<TagGroupRecord> getTagGroup(UUID uuid) {
        return null;
    }

    //TODO: Solo si no esta habilitado el driver/dispositivo/taggroup
    @Override
    public void removeTagGroup(TagGroupRecord taggroup) {
        dbresult.allInstances().stream().
                forEach(drv -> {
                    drv.getDevices().stream().
                            forEach(dev -> dev.removeTagGroup(taggroup));
                    ;
                });
    }

    @Override
    public void addTag(UUID taggroup, TagRecord tag) {
        Optional<TagGroup> optagg = dbresult.allInstances().stream().
                filter(drv -> drv.getDevices().stream().
                        anyMatch(dev -> dev.getTagGroup(taggroup).isPresent())).
                findFirst();
                
    }

    @Override
    public Optional<TagRecord> getTag(UUID uuid) {
        return null;
    }

    @Override
    public void removeTag(UUID uuid) {
        
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
        return new TagGroupRecordImpl();
    }

    @Override
    public TagRecord createTagDBRecord() {
        return new TagRecordImpl();
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

    @Override
    public Lookup getLookup() {
        return lk;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        System.out.println("Evento: " + ev.toString());
    }


    
}
