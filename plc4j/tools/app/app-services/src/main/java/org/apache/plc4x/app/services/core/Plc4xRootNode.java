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

import java.awt.Image;
import java.beans.IntrospectionException;
import java.io.IOException;
import javax.swing.Action;
import javax.swing.ImageIcon;
import org.apache.plc4x.app.services.api.MasterDB;
import org.netbeans.api.core.ide.ServicesTabNodeRegistration;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.actions.NewAction;
import org.openide.actions.OpenLocalExplorerAction;
import org.openide.actions.PropertiesAction;
import org.openide.actions.ToolsAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;

@ServicesTabNodeRegistration(name = "Plc4xRootNode",
    displayName = "#LBL_Plc4xRootNode",
    shortDescription = "#HINT_Plc4xRootNode",
    iconResource = "org/apache/plc4x/app/services/toddy_s7_16x16.png",
    position = 2021) 
@Messages({"LBL_Plc4xRootNode=Plc4x Drivers",
    "HINT_Plc4xRootNode=Shows all currently set system properties." })
public class Plc4xRootNode extends AbstractNode {
    
    public final Image closeicon = ImageUtilities.loadImage("org/apache/plc4x/app/services/toddy_s7_16x16.png");
    public final Image openicon = ImageUtilities.loadImage("org/apache/plc4x/app/services/toddy_s7_16x16.png");

    private final MasterDB db = Lookup.getDefault().lookup(MasterDB.class);

    public Plc4xRootNode() throws IntrospectionException {
        super(Children.create(new Plc4xRootChildFactory(), false));        
        setDisplayName(Bundle.LBL_Plc4xRootNode());      
        setShortDescription(Bundle.HINT_Plc4xRootNode());        
    }


    @Override
    public javax.swing.Action[] getActions(boolean context) {
      Action[] result = new Action[]{
          null,
          null,
          SystemAction.get(OpenLocalExplorerAction.class),
          null,
          SystemAction.get(NewAction.class),
          null,
          SystemAction.get(ToolsAction.class),
          SystemAction.get(PropertiesAction.class),};
      return result;
    }

    @Override
    public Image getIcon(int type) {
        return closeicon;
    }
        
    @Override
    public Image getOpenedIcon(int type) {
        return openicon;
    }


    
    
}
