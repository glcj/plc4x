/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.plc4x.app.services.impl;

import java.time.LocalDateTime;
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
    
    private LocalDateTime startDateTime;
    private LocalDateTime currentDateTime;
    private LocalDateTime lastUpdateDateTime;    
    
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
    public LocalDateTime getStartDateTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public LocalDateTime getCurrentDateTime() {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public LocalDateTime getLastUpdateDateTime() {
        throw new UnsupportedOperationException("Not supported yet.");
    }



    
}
