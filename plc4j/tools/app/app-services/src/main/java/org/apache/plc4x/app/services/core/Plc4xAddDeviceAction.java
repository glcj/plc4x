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

import org.apache.plc4x.app.services.view.Plc4xAddDeviceDialog;
import org.apache.plc4x.app.services.model.Plc4xDriverNode;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentListener;
import java.beans.IntrospectionException;
import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import org.apache.plc4x.app.api.Plc4xDialog;
import org.apache.plc4x.app.services.impl.DeviceRecordImpl;
import org.apache.plc4x.app.services.model.Plc4xDeviceNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;
import org.apache.plc4x.app.api.DeviceRecord;


public class Plc4xAddDeviceAction extends AbstractAction{
    
    private final Plc4xDriverNode node;

    public Plc4xAddDeviceAction(final Plc4xDriverNode node) {
        this.node = node;
        this.putValue(AbstractAction.NAME, "Add Device...");
    }

    /*
    * TODO: Apply regex to device name. Only letter and number, including "_".
    */
    @Override
    public void actionPerformed(ActionEvent ae) {
        Lookup lk = Lookups.forPath("Plc4xDriver/" + node.getDriverRecord().getProtocolCode());
        if ( lk != null) {
            DeviceRecord devicerecord = new DeviceRecordImpl();
            node.setValue("DEVICE", devicerecord);
            final Plc4xDialog dialog = lk.lookup(Plc4xDialog.class);
            dialog.setNode(node);                       
            ((JDialog) dialog).setVisible(true);
            /*
            if (devicerecord.getDeviceName() !=null) {
                String name = devicerecord.getDeviceName();
                Boolean isBlank =  name.isEmpty();
                if (!isBlank) {
                    Node[] nodes = new Plc4xDeviceNode[1];
                    try {
                        //1. Add to MasterDB.
                        
                        //2. Register to Plc4xDriver Service.
                        //   Any problem disable the device record.
                        
                        //3. Add the node representation.
                        nodes[0] = new Plc4xDeviceNode(devicerecord);
                        node.getChildren().add(nodes); 
                    } catch (IntrospectionException ex) {
                        Exceptions.printStackTrace(ex);
                    }                 
                }
            }*/
        }     
        /*
        Plc4xAddDeviceDialog dialog = Lookup.getDefault().lookup(Plc4xAddDeviceDialog.class);
        dialog.setNode(node);
        dialog.setVisible(true);
        */
        // Node[] nodes = new Plc4xDeviceNode[1];
        // nodes[0] = new Plc4xDeviceNode("NOmbre");
        // node.getChildren().add(nodes);
    }
    
}
