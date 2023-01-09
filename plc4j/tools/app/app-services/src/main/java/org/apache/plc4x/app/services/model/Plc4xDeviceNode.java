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
package org.apache.plc4x.app.services.model;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.event.ChangeListener;
import org.apache.plc4x.app.api.DeviceDBRecord;
import org.apache.plc4x.app.services.core.Plc4xAddTagGroupAction;
import org.apache.plc4x.app.services.core.Plc4xDelTagGroupAction;
import org.apache.plc4x.app.services.core.Plc4xDeviceChildFactory;
import org.apache.plc4x.app.services.core.Plc4xPropertiesNotifier;
import org.openide.actions.DeleteAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;

/**
 *
 * @author cgarcia
 */
public class Plc4xDeviceNode  extends BeanNode implements PropertyChangeListener {

    private final DeviceDBRecord bean;      
    private String key;     
    private ChangeListener listener;    

    @Messages("HINT_Plc4xDeviceNode=Represents one Plc4x driver.")    
    public Plc4xDeviceNode(DeviceDBRecord bean) throws IntrospectionException {
        super(bean, Children.create(new Plc4xDeviceChildFactory(bean), false));       
        this.bean = bean;   
        setIconBaseWithExtension("org/apache/plc4x/app/services/Device_16x16.png"); 
        super.setName(this.bean.getDeviceName());  
        setShortDescription(Bundle.HINT_Plc4xDeviceNode()); 
        final BeanInfo info = Introspector.getBeanInfo(DeviceDBRecord.class);
        System.out.println("Propiedades: " + this.getPropertySets().length);
        this.setValue("BEAN", bean);
    }
    
    @Override     
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            SystemAction.get(OpenLocalExplorerAction.class),
            new Plc4xAddTagGroupAction(this),
            new Plc4xDelTagGroupAction(this),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(DeleteAction.class),
            SystemAction.get(PropertiesAction.class),
        };         
        return result;     
    } 
     
    
    
    @Override     
    public Action getPreferredAction() {
        return SystemAction.get(PropertiesAction.class);
    } 
    
    @Override     
    public Node cloneNode() {         
        try {     
            return new Plc4xDeviceNode(bean);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Node.EMPTY;
    }

    @Override     
    protected void finalize() throws Throwable {
        super.finalize();
        if (listener != null) {
            Plc4xPropertiesNotifier.removeChangeListener(listener);
        }
    } 
    
    @Override     
    public boolean canRename() {
        return true;     
    }    
    
    @Override     
    public void setName(String nue) {              
        Plc4xPropertiesNotifier.changed();     
    }   
    
    @Override    
    public boolean canDestroy() {
        return true;     
    }
    
    @Override     
    public void destroy() throws IOException {
        Plc4xPropertiesNotifier.changed();     
    }    

    @Override
    public void propertyChange(PropertyChangeEvent pce) {
        System.out.println("Cambio: " + pce.getPropertyName());
        System.out.println("Value: " + pce.getNewValue());
    }
    
}
