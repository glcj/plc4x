/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.apache.plc4x.app.services.impl;

import java.time.LocalDateTime;
import java.util.UUID;
import org.apache.plc4x.app.services.api.TagDBRecord;

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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getReceives() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getErrors() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public int getNumberOfTags() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LocalDateTime getLastReadDateTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LocalDateTime getLastWriteDateTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public LocalDateTime getLastErrorDateTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
