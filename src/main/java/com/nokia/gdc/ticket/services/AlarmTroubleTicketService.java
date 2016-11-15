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
                alarmObject = NetactAlarmConnector.alarmObjectQueue.poll(1, TimeUnit.MINUTES);
                if (alarmObject != null) {
                    String jsonMessage;
                    try {
                        jsonMessage = mapper.writeValueAsString(alarmObject);
                    } catch (JsonProcessingException ex) {
                        jsonMessage = "Message Cannot translated";
                    }
                    logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Object Recieved (" + jsonMessage + ") ]");

                    //    ttRepo.save(this);
                    try {
                        File file = new File("test-alarm.csv");

                        //if file doesnt exists, then create it
                        if (!file.exists()) {
                            file.createNewFile();
                        }

                        //true = append file
                        FileWriter fileWritter = new FileWriter(file.getName(), true);
                        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                        //bufferWritter.write(Arrays.toString(alarmObject.CSV()));
                        bufferWritter.write("\n");
                        bufferWritter.close();
                    } catch (Exception e) {
                        // do something
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException ex) {
                logger.error("[ThreadID:" + threadId + ",errorcode:1301,message:" + ex.getMessage());
            }
        }
    }

    private void doTicketOperation(NetactAlarm alarmObject) {
        String title = alarmObject.getTicketTitle();
        List<TroubleTicket> ttList = ttRepo.findTT("open", title);
        if (ttList.size() == 0) {
            TroubleTicket tt = new TroubleTicket(title, alarmObject.getSeverity(), alarmObject.getStartTime(), null, alarmObject.getStartTime(), null, this.getClass().toString());
            tt.setStatus(alarmObject.ticketStatus());
            tt.addCDR(alarmObject.getEventCDR());
            ttRepo.save(tt);
        } else {
            //alarmObject.
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
