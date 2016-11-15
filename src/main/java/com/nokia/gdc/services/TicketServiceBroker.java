/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.domain.NetactAlarm;
import com.nokia.gdc.repositories.TroubleTicketRepository;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import com.nokia.gdc.domain.TroubleTicket;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arindra
 */
@Service
public class TicketServiceBroker implements Runnable {

    @Autowired
    TroubleTicketRepository ttRepo;

    @Override
    public void run() {
        while (true) {
            NetactAlarm alarmObject;
            try {
                alarmObject = NetactAlarmConnector.alarmObjectQueue.poll(1, TimeUnit.MINUTES);
                if (alarmObject != null) {

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
                        bufferWritter.write(Arrays.toString(alarmObject.CSV()));
                        bufferWritter.close();
                    } catch (Exception e) {
                        // do something
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(TicketServiceBroker.class.getName()).log(Level.SEVERE, null, ex);
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
