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

import org.apache.plc4x.app.services.model.Plc4xDriverNode;
import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.apache.plc4x.app.services.model.Plc4xDeviceNode;
import org.openide.nodes.AbstractNode;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.apache.plc4x.app.api.DeviceRecord;

/**
 *
 * @author cgarcia
 */
public class Plc4xDelDeviceAction extends AbstractAction  {
    
    private final Plc4xDeviceNode node;    

    public Plc4xDelDeviceAction(final Plc4xDeviceNode node) {
        this.node = node;
        this.putValue(AbstractAction.NAME, "Delete Device");        
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        //1. Get the node selected
        System.out.println("ActionEvent: " + ae.getSource().getClass().getName());
        System.out.println("Este: " + node.getDisplayName());    
        try {
            node.destroy();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        //2. Get the DeviceRecord associated to the noe
        
        //3. Delete from database.
        
         JOptionPane.showMessageDialog(null, "Borrar Device!");
    }
    
}
