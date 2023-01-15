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

import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.Properties;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.apache.plc4x.app.services.core.Plc4xPropertiesNotifier;
import org.openide.actions.DeleteAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.RenameAction;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.apache.plc4x.app.api.TagRecord;

/**
 *
 * @author cgarcia
 */
public class Plc4xTagNode  extends BeanNode {
    
    private final TagRecord bean;
    private String key;     
    private ChangeListener listener;    

    @Messages("HINT_Plc4xTagNode=Represents one Plc4x driver.")    
    public Plc4xTagNode(TagRecord bean)  throws IntrospectionException {
        super(bean, Children.LEAF);        
        this.bean = bean;   
        setIconBaseWithExtension("org/apache/plc4x/app/services/tag_amarilla_linea_16x16.png"); 
        super.setName(bean.getTagName());         
        setShortDescription(Bundle.HINT_Plc4xTagNode());        
    }
    
    @Override     
    public Action[] getActions(boolean context) {
        Action[] result = new Action[]{
            SystemAction.get(OpenLocalExplorerAction.class),
            null,
            null,
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
            return new Plc4xTagNode(bean);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return Node.EMPTY;
    }

    @Messages({"PROP_TagNode_value=Value",
        "HINT_TagNode_value=Value of this system property."})     
    @Override     
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set props = sheet.get(Sheet.PROPERTIES);
        if (props == null) {
            props = Sheet.createPropertiesSet();
            sheet.put(props);
        }         
        props.put(new PropertySupport.Name(this));
        
        class ValueProp extends PropertySupport.ReadWrite {
            public ValueProp() {
                super("value", String.class, Bundle.PROP_TagNode_value(), Bundle.HINT_TagNode_value());
            }             
            
            @Override             
            public Object getValue() {
                return System.getProperty(key);
            }             
            
            @Override             
            public void setValue(Object nue) {
                System.setProperty(key, (String) nue);
                Plc4xPropertiesNotifier.changed();
            }         
        }         
        
        props.put(new ValueProp());
        Plc4xPropertiesNotifier.addChangeListener(listener = new ChangeListener() {
            @Override             
            public void stateChanged(ChangeEvent ev) {
                firePropertyChange("value", null, null);
            }         
        });
        
        return sheet;
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
        Properties p = System.getProperties();
        String value = p.getProperty(key);
        p.remove(key);         
        
        if (value != null) {
            p.setProperty(nue, value);
        }         
        
        System.setProperties(p);         
        
        Plc4xPropertiesNotifier.changed();     
    }   
    
    @Override    
    public boolean canDestroy() {
        return true;     
    }
    
    @Override     
    public void destroy() throws IOException {
        Properties p = System.getProperties();
        p.remove(key);
        System.setProperties(p);
        Plc4xPropertiesNotifier.changed();     
    }    
    
}
