/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.netact.alarm.domain.NetactAlarm;
import com.nokia.gdc.ticket.repositories.TroubleTicketRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import com.nokia.gdc.ticket.domain.TroubleTicket;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arindra
 */
@Service
@EnableScheduling
public class AlarmTroubleTicketService implements Runnable {

    @Autowired
    TroubleTicketRepository ttRepo;
    
    @Autowired
    MantisConnectService mantisConnectService = new MantisConnectService();

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        long threadId = Thread.currentThread().getId();
        logger.info("[ThreadID:" + threadId + ",errorcode:0,message:Alarm Ticket Service started... ]");

        ObjectMapper mapper = new ObjectMapper();
        NetactAlarm alarmObject;
        while (true) {
            try {
                alarmObject = (NetactAlarm) NetactAlarmConnector.enterpriseFramework.alarmTicketQueue.poll(1, TimeUnit.MINUTES);
                if (alarmObject != null) {
                    String jsonMessage;
                    try {
                        jsonMessage = mapper.writeValueAsString(alarmObject);
                    } catch (JsonProcessingException ex) {
                        jsonMessage = "Message Cannot translated";
                    }
                    logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Object Received (" + jsonMessage + ") ]");

                    doTicketOperation(alarmObject);
                }
            } catch (InterruptedException ex) {
                logger.error("[ThreadID:" + threadId + ",errorcode:1301,message:" + ex.getMessage());
            } catch (Exception ex1){
                logger.error("[ThreadID:" + threadId + ",errorcode:9999,message:" + ex1.getMessage());
            }
        }
    }

    private void doTicketOperation(NetactAlarm alarmObject) {
        String title = alarmObject.getTicketTitle();
        List<TroubleTicket> ttList = ttRepo.findTT("open", title);
        ObjectMapper mapper = new ObjectMapper();
        String jsonMessage;
        if (ttList.isEmpty()) {
            TroubleTicket tt = new TroubleTicket(title, alarmObject.getSeverity(), alarmObject.getStartTime(), alarmObject.getStartTime(), null);
            tt.setStatus(alarmObject.ticketStatus());
            tt.addCDR(alarmObject.getEventCDR());
            tt.generateID();
            tt.setLastTriggerBy(this.getClass().toString());
            try {
                jsonMessage = mapper.writeValueAsString(tt);
            } catch (JsonProcessingException ex) {
                jsonMessage = "Message Cannot translated";
            }
            if (alarmObject.ticketStatus().contains("close")) {
                logger.warn("No open ticket match for this object = " + jsonMessage);
            } else {
                BigInteger id = mantisConnectService.createTicket(tt);
                if(id != null){ 
                    tt.setExternalTicketID(id.toString());
                    logger.info("WTT created  ("+id+"|" + tt.getTitle() + ")");
                }else{
                    logger.error("Error in WTT create  ("+id+"|" + tt.getTitle() + ")");
                }
                ttRepo.save(tt);
                logger.info("Ticket created  (" + jsonMessage + ")");
            }

        } else {
            if (alarmObject.ticketStatus().contains("open")) {
                TroubleTicket tt = ttList.get(0);
                tt.addCDR(alarmObject.getEventCDR());
                tt.setLastTriggerBy(this.getClass().toString());
                ttRepo.save(tt);
            } else if (alarmObject.ticketStatus().contains("close")) {
                TroubleTicket tt = ttList.get(0);
                tt.addCDR(alarmObject.getEventCDR());
                tt.closeTicket(alarmObject.getClearedTime());
                tt.setLastTriggerBy(this.getClass().toString());
                
                boolean isClosed = mantisConnectService.closeTicket(tt);
                if (isClosed){
                    logger.info("WTT closed  ("+tt.getExternalTicketID()+"|" + tt.getTitle() + ")");
                }else{
                    logger.error("Error in WTT close  ("+tt.getExternalTicketID()+"|" + tt.getTitle() + ")");
                }
                ttRepo.save(tt);

                try {
                    jsonMessage = mapper.writeValueAsString(tt);
                } catch (JsonProcessingException ex) {
                    jsonMessage = "Message Cannot translated";
                }
                
                logger.info("Ticket closed [" + isClosed + "] - (" + jsonMessage + ")");

            }
        }

    }
    
       
    @Scheduled(cron = "*/5 * * * * ?")
    public void syncTroubleTicketWithWTT(){
        
    }
}
