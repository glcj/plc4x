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

import org.apache.plc4x.app.services.model.Plc4xDeviceNode;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.apache.plc4x.app.api.DeviceRecord;
import org.apache.plc4x.app.api.DriverRecord;


public class Plc4xDriverChildFactory extends ChildFactory.Detachable<DeviceRecord> implements LookupListener {
   
    private final DriverRecord driver;
    private final Lookup.Result<DeviceRecord> plc4xresult;
    private final Lookup.Template template = new Lookup.Template(DeviceRecord.class);     
    private PropertyChangeListener listener;

    public Plc4xDriverChildFactory(DriverRecord driver) {
        this.driver = driver;
        
        plc4xresult = driver.getLookup().lookup(template);
        plc4xresult.addLookupListener(this);        
    }

    @Override     
    protected void addNotify() {
        driver.addPropertyChangeListener(listener = (PropertyChangeEvent ev) -> {
           refresh(true);         
       });     
    }    
   
    @Override     
    protected void removeNotify() {
        if (listener != null) {
            driver.removePropertyChangeListener(listener);
            listener = null;         
        }         
    }   
    
    @Override     
    protected Node createNodeForKey(DeviceRecord key) {         
        try {     
            return new Plc4xDeviceNode(key);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Node.EMPTY;
    }    
    
    @Override
    protected boolean createKeys(List<DeviceRecord> toPopulate) {
        driver.getDevices().stream().
                forEach(b -> {if (b != null) toPopulate.add(b);});               
        return true;
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        this.refresh(true);
    }
    
}
