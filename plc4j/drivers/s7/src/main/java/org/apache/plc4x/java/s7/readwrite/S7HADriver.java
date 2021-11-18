/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.plc4x.java.s7.readwrite;

import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.exceptions.PlcConnectionException;
import org.apache.plc4x.java.s7.readwrite.configuration.S7Configuration;
import org.apache.plc4x.java.spi.configuration.Configuration;

/**
 *
 * @author cgarcia
 */
public class S7HADriver extends S7Driver {

    
    @Override
    public String getProtocolCode() {
        return "s7h";
    }    
    
    @Override
    public String getProtocolName() {
        return "Siemens S7 (High Availability)";
    }
    
    @Override
    public PlcConnection getConnection(String connectionString) throws PlcConnectionException {
        System.out.println("La conexion: " + connectionString);
        return super.getConnection(connectionString); //To change body of generated methods, choose Tools | Templates.
    }


    
}
