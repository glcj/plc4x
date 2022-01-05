/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.plc4x.hop.transformer.plc4xinput;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.hop.core.CheckResult;
import org.apache.hop.core.Const;
import org.apache.hop.core.ICheckResult;
import org.apache.hop.core.RowMetaAndData;
import org.apache.hop.core.exception.HopException;
import org.apache.hop.core.exception.HopPluginException;
import org.apache.hop.core.exception.HopValueException;
import org.apache.hop.core.logging.LogLevel;
import org.apache.hop.core.row.IRowMeta;
import org.apache.hop.core.row.IValueMeta;
import org.apache.hop.core.row.RowDataUtil;
import org.apache.hop.core.row.RowMeta;
import org.apache.hop.core.row.value.ValueMetaDate;
import org.apache.hop.core.row.value.ValueMetaFactory;
import org.apache.hop.core.util.StringUtil;
import org.apache.hop.core.util.Utils;
import org.apache.hop.i18n.BaseMessages;
import org.apache.hop.metadata.api.IHopMetadataProvider;
import org.apache.hop.pipeline.Pipeline;
import org.apache.hop.pipeline.PipelineMeta;
import org.apache.hop.pipeline.engine.IPipelineEngine;
import org.apache.hop.pipeline.transform.BaseTransform;
import org.apache.hop.pipeline.transform.ITransform;
import org.apache.hop.pipeline.transform.TransformMeta;
import org.apache.plc4x.hop.metadata.Plc4xConnection;
import org.apache.plc4x.hop.transformer.util.GeneratorField;
import org.apache.plc4x.hop.transformer.util.Plc4xWrapperConnection;
import org.apache.plc4x.java.PlcDriverManager;
import org.apache.plc4x.java.api.PlcConnection;
import org.apache.plc4x.java.api.messages.PlcReadRequest;
import org.apache.plc4x.java.api.messages.PlcReadResponse;

/**
 * Transform That contains the basic skeleton needed to create your own plugin
 *
 */
public class Plc4xRead extends BaseTransform<Plc4xReadMeta, Plc4xReadData> implements ITransform<Plc4xReadMeta, Plc4xReadData> {

  private static final Class<?> PKG = Plc4xRead.class; // Needed by Translator
  
  private Plc4xConnection connmeta = null;
  private Plc4xWrapperConnection connwrapper = null;
  private PlcReadRequest readRequest = null;
  private static final ReentrantLock lock = new ReentrantLock();
  
  private static final String dummy = "dummy";

  public Plc4xRead(TransformMeta transformMeta, Plc4xReadMeta meta, Plc4xReadData data, int copyNr, PipelineMeta pipelineMeta,
                Pipeline pipeline ) {
    super( transformMeta, meta, data, copyNr, pipelineMeta, pipeline );
  }

  /*
  * 
  * @param meta Meta data from user dialog
  * @param remarks Error registers
  * @param origin transform instance name
  */
  public static final RowMetaAndData buildRow(
      Plc4xReadMeta meta, List<ICheckResult> remarks, String origin) throws HopPluginException {
    IRowMeta rowMeta = new RowMeta();
    Object[] rowData = RowDataUtil.allocateRowData(meta.getFields().size() + 2);
    int index = 0;

    if (meta.isNeverEnding()) {
      if (!Utils.isEmpty(meta.getRowTimeField())) {
        rowMeta.addValueMeta(new ValueMetaDate(meta.getRowTimeField()));
        rowData[index++] = null;
      }

      if (!Utils.isEmpty(meta.getLastTimeField())) {
        rowMeta.addValueMeta(new ValueMetaDate(meta.getLastTimeField()));
        rowData[index++] = null;
      }
    }

    for (GeneratorField field : meta.getFields()) {
      int typeString = ValueMetaFactory.getIdForValueMeta(field.getType());
      if (StringUtils.isNotEmpty(field.getType())) {
        IValueMeta valueMeta =
            ValueMetaFactory.createValueMeta(field.getName(), typeString); // build a
        // value!
        valueMeta.setLength(field.getLength());
        valueMeta.setPrecision(field.getPrecision());
        valueMeta.setConversionMask(field.getFormat());
        valueMeta.setCurrencySymbol(field.getCurrency());
        valueMeta.setGroupingSymbol(field.getGroup());
        valueMeta.setDecimalSymbol(field.getDecimal());
        valueMeta.setOrigin(origin);

        IValueMeta stringMeta = ValueMetaFactory.cloneValueMeta(valueMeta, IValueMeta.TYPE_STRING);

        if (field.isSetEmptyString()) {
          // Set empty string
          rowData[index] = StringUtil.EMPTY_STRING;
        } else {
          String stringValue = field.getValue();

          // If the value is empty: consider it to be NULL.
          if (Utils.isEmpty(stringValue)) {
            rowData[index] = null;

            if (valueMeta.getType() == IValueMeta.TYPE_NONE) {
              String message =
                  BaseMessages.getString(
                      PKG,
                      "Plc4x.Read.Meta.CheckResult.SpecifyTypeError",
                      valueMeta.getName(),
                      stringValue);
              remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
            }
          } else {
            // Convert the data from String to the specified type ...
            //
            try {
              rowData[index] = valueMeta.convertData(stringMeta, stringValue);
            } catch (HopValueException e) {
              switch (valueMeta.getType()) {
                case IValueMeta.TYPE_NUMBER:
                  String message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.BuildRow.Error.Parsing.Number",
                          valueMeta.getName(),
                          stringValue,
                          e.toString());
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;

                case IValueMeta.TYPE_DATE:
                  message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.BuildRow.Error.Parsing.Date",
                          valueMeta.getName(),
                          stringValue,
                          e.toString());
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;

                case IValueMeta.TYPE_INTEGER:
                  message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.BuildRow.Error.Parsing.Integer",
                          valueMeta.getName(),
                          stringValue,
                          e.toString());
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;

                case IValueMeta.TYPE_BIGNUMBER:
                  message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.BuildRow.Error.Parsing.BigNumber",
                          valueMeta.getName(),
                          stringValue,
                          e.toString());
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;

                case IValueMeta.TYPE_TIMESTAMP:
                  message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.BuildRow.Error.Parsing.Timestamp",
                          valueMeta.getName(),
                          stringValue,
                          e.toString());
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;

                default:
                  // Boolean and binary don't throw errors normally, so it's probably an unspecified
                  // error problem...
                  message =
                      BaseMessages.getString(
                          PKG,
                          "Plc4x.Read.Meta.CheckResult.SpecifyTypeError",
                          valueMeta.getName(),
                          stringValue);
                  remarks.add(new CheckResult(ICheckResult.TYPE_RESULT_ERROR, message, null));
                  break;
              }
            }
          }
        }

        // Now add value to the row!
        // This is in fact a copy from the fields row, but now with data.
        rowMeta.addValueMeta(valueMeta);
        index++;
      }
    }

    return new RowMetaAndData(rowMeta, rowData);
  }
  
  /* 
  * 
  *
  */
  @Override
  public  boolean processRow() throws HopException {
    Object[] r = getRow(); // Get row from input rowset & set row busy!
    setLogLevel(LogLevel.DEBUG);
    
    if ((!meta.isNeverEnding() && data.rowsWritten >= data.rowLimit) && !isStopped()) {
      logBasic("Finalizando la ejecucion...");        
      setOutputDone(); // signal end to receiver(s)
      return false;        
    }    
    
    lock.lock();
    try {
        IHopMetadataProvider metaprovider = getMetadataProvider();
        connmeta = metaprovider.getSerializer(Plc4xConnection.class).load(meta.getConnection());
        connwrapper = (Plc4xWrapperConnection) getPipeline().getExtensionDataMap().get(meta.getConnection());

        if (connmeta == null){    
            logError(
                BaseMessages.getString(
                    PKG,
                    "Plc4x.Read.Meta.Log.SetMetadata",
                    meta.getConnection()));         
        }

        if ((connmeta != null) && (connwrapper == null)){
            readRequest = null;
            try{
                PlcConnection conn =  new PlcDriverManager().getConnection(connmeta.getUrl());
                if (conn.isConnected()) {
                    logBasic("Crea wrapper para la nueva conexion.");  
                    logBasic("Objeto conneccion: " + conn.hashCode());
                    connwrapper = new Plc4xWrapperConnection(conn);            
                    getPipeline().getExtensionDataMap().put(meta.getConnection(), connwrapper); 
                    Thread.sleep(100);
                }
            } catch (Exception ex){
                throw new HopException ("Unable to create connection to PLC");
            }
        }
    } finally {
        lock.unlock();
    }

    
    if ((connmeta != null) && (connwrapper != null)){
        if (connwrapper.getConnection().isConnected()){
            if (readRequest == null){

                PlcReadRequest.Builder builder = connwrapper.getConnection().readRequestBuilder(); 
                for (GeneratorField field: meta.getFields()){
                    builder.addItem(field.getName(), field.getItem());
                }                
                readRequest = builder.build();   
                logBasic("Created read request");                
            }
            
            try {
                logBasic("Executing read request.");                
                PlcReadResponse readResponse = readRequest.execute().orTimeout(5, TimeUnit.SECONDS).get();
                for (GeneratorField field: meta.getFields()){
                    field.setValue(readResponse.getString(field.getName()));
                    System.out.println("Asigna valor: " + readResponse.getString(field.getName()));
                }  
                data.rowDate = new Date();
            } catch (Exception ex) {
                throw new HopException ("Unable read from PLC. " + ex.getMessage());
            }
            
        } else {
            getPipeline().getExtensionDataMap().put(meta.getConnection(), null);  
        }
        
    } else {
      setOutputDone();
      return false;        
    }
    
    /*
    if ((meta.isNeverEnding() || data.rowsWritten < data.rowLimit) && !isStopped()) {
      r = data.outputRowMeta.cloneRow(data.outputRowData);
        System.out.println("Tamano : " + r.length);        
    } else {
      logBasic("Finalizando la ejecucion...");        
      setOutputDone(); // signal end to receiver(s)
      return false;
    }    
    */
    r = data.outputRowMeta.cloneRow(data.outputRowData);    
    putRow(data.outputRowMeta, r ); // return your data
    data.rowsWritten++;
    return true;
  }


  @Override
  public boolean init() {
    try {
        if(super.init()){     
            // Determine the number of rows to generate...
            data.rowLimit = Const.toLong(resolve(meta.getRowLimit()), -1L);
            data.rowsWritten = 0L;
            data.delay = Const.toLong(resolve(meta.getIntervalInMs()), -1L);

            if (data.rowLimit < 0L) { // Unable to parse
              logError(BaseMessages.getString(PKG, "Plc4x.Read.Meta.Wrong.RowLimit.Number"));
              return false; // fail
            }            
            
            // Create a row (constants) with all the values in it...
            List<ICheckResult> remarks = new ArrayList<>(); // stores the errors...
            RowMetaAndData outputRow = buildRow(meta, remarks, getTransformName());
            if (!remarks.isEmpty()) {
              for (int i = 0; i < remarks.size(); i++) {
                CheckResult cr = (CheckResult) remarks.get(i);
                logError(cr.getText());
              }
              return false;
            }

            data.outputRowData = outputRow.getData();
            data.outputRowMeta = outputRow.getRowMeta();            

          return true;
        }
    return false;
    } catch (Exception ex){
        setErrors(1L);
        logError("Error initializing transform", ex);
        return false;
    }
  }

  /*
  * Here, must perform the cleaning of any resource, main of the connection to 
  * the associated PLC.
  */
    @Override
    public void cleanup() {
        super.cleanup();
        logBasic("Cleanup. Release connection.");
        if (connwrapper != null)
        connwrapper.release();     
    }


    /*
     * Here, must perform the cleaning of any resource, main of the connection to 
     * the associated PLC.
    */    
    @Override
    public void dispose() {
        super.dispose();
        logBasic("Dispose. Release connection.");
        if (connwrapper != null)
        connwrapper.release();   
        logBasic("Ref Count : " + connwrapper.refCnt());
        if (( connwrapper.refCnt() == 0) && connwrapper.getConnection().isConnected()){
            try {
                System.out.println("Solicita nuevamente el cierre");
            connwrapper.getConnection().close();
            } catch (Exception ex){
                logBasic(ex.getMessage());
            }
        }
        if (!connwrapper.getConnection().isConnected()){
            logBasic("**** Conexion cerrada");
            connwrapper = null;
            readRequest = null;
            //getPipeline().getExtensionDataMap().put(meta.getConnection(), null);             
            getPipeline().getExtensionDataMap().remove(meta.getConnection());
        }
    }
 
  
  
  
}
