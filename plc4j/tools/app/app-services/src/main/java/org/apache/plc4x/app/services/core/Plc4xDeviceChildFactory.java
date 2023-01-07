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
package org.apache.plc4x.app.services.core;

import java.beans.IntrospectionException;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.plc4x.app.services.api.DeviceDBRecord;
import org.apache.plc4x.app.services.api.DriverDBRecord;
import org.apache.plc4x.app.services.api.TagGroupDBRecord;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;


public class Plc4xDeviceChildFactory extends ChildFactory.Detachable<TagGroupDBRecord> {
   
    private final DeviceDBRecord device;  
    private ChangeListener listener;

    public Plc4xDeviceChildFactory(DeviceDBRecord device) {
        this.device = device;
    }

    @Override     
    protected void addNotify() {
       Plc4xPropertiesNotifier.addChangeListener(listener = (ChangeEvent ev) -> {
           refresh(true);         
       });     
    }    
   
    @Override     
    protected void removeNotify() {
        if (listener != null) {
            Plc4xPropertiesNotifier.removeChangeListener(listener);
            listener = null;         
        }     
    }   
    
    @Override     
    protected Node createNodeForKey(TagGroupDBRecord key) {         
        try {     
            return new Plc4xTagGroupNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Node.EMPTY;
    }    
    
    @Override
    protected boolean createKeys(List<TagGroupDBRecord> toPopulate) {        
        device.getTagGroups().stream().forEach(b -> toPopulate.add(b));        
        return true;
    }
    
}
