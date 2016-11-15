/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.alarm.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.gdc.NetactAlarmConnector;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arindra
 */
@Service
public class AlarmLoggerService implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        long threadId = Thread.currentThread().getId();
        logger.info("[ThreadID:" + threadId + ",errorcode:0,message:Alarm Logger Service started... ]");

        ObjectMapper mapper = new ObjectMapper();
        while (true) {
            try {
                Object obj = NetactAlarmConnector.enterpriseFramework.alarmLoggerQueue.poll(1, TimeUnit.MINUTES);
                if (obj != null) {
                    try {
                        String jsonMessage;
                        try {
                            jsonMessage = mapper.writeValueAsString(obj);
                        } catch (JsonProcessingException ex) {
                            jsonMessage = "Message Cannot translated";
                        }
                        logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Object Recieved (" + jsonMessage + ") ]");
                        String message = obj.toString();
                        Boolean isNew = false;
                        File file = new File("AlarmLoggerService.csv");

                        //if file doesnt exists, then create it
                        if (!file.exists()) {
                            file.createNewFile();
                            isNew = true;
                        }

                        //true = append file
                        FileWriter fileWritter = new FileWriter(file.getName(), true);
                        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                        if (isNew) {
                            bufferWritter.write("alarmnumber,severity,maintenanceRegion,siteName,dn,alarmText,startTime,clearedTime,intID,notifID,pcText,supplInfo,diagInfo,userInfo,objectName,alarmType");
                            bufferWritter.newLine();
                        }
                        bufferWritter.write(message);
                        bufferWritter.newLine();
                        bufferWritter.close();
                        logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Success (" + file.getName() + "|" + isNew + "|" + message + ") ]");

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("[ThreadID:" + threadId + ",errorcode:1202,message:" + e.getMessage());
                    }
                }
            } catch (InterruptedException ex) {
                logger.info("[ThreadID:" + threadId + ",errorcode:0,message:Thread Closing ..");
                
            }
        }
    }
}
