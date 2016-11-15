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
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arindra
 */
@Service
public class AlarmTroubleTicketService implements Runnable {

    @Autowired
    TroubleTicketRepository ttRepo;

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
                ttRepo.save(tt);

                try {
                    jsonMessage = mapper.writeValueAsString(tt);
                } catch (JsonProcessingException ex) {
                    jsonMessage = "Message Cannot translated";
                }
                logger.info("Ticket closed  (" + jsonMessage + ")");

            }
        }

    }
    /*
    private void createTicket(String title, NetactAlarm alarmObject){
        List<TroubleTicket> ttList = ttRepo.findTT("pre-open", title);
        if (ttList != null){
            if (ttList.size() > 1){
                Logger.getLogger(TicketServiceBroker.class.getName()).log(Level.SEVERE, "Double Ticket detected");
            }else{
                TroubleTicket tt = ttList.get(0);
                long diff = alarmObject.getStartTime().getTime() - tt.getFirstOpenAlarmTime().getTime();
                long diffMinutes = diff / (60 * 1000);
                if (diffMinutes > 5){
                    tt.increaseStatus();
                    tt.addCDR(alarmObject.getEventCDR());
                    ttRepo.save(tt);
                }
            }
        }else{
            
        }
        
    }
     */
}
