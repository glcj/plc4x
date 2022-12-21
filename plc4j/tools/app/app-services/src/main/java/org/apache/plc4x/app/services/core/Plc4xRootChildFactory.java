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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.plc4x.app.services.api.MasterDB;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcDriver;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author cgarcia
 */
public class Plc4xRootChildFactory extends ChildFactory.Detachable<String> {

    private ChangeListener listener;
    
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
    protected Node createNodeForKey(String key) {         
        return new Plc4xDriverNode(key);     
    }    
    
    @Override
    protected boolean createKeys(List<String> toPopulate) {
        
        List<String> keys = new ArrayList<String>();
        
        MasterDB db = Lookup.getDefault().lookup(MasterDB.class);
        

        for (String name:db.getDriverNames()) {
            keys.add(name);
            
        }
        
        Collections.sort(keys);         
        toPopulate.addAll(keys); 

        return true;
    }
    
}
