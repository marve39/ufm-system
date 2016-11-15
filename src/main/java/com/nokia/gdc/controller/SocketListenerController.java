/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.controller;

import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.alarm.services.AlarmLoggerService;
import com.nokia.gdc.common.services.MessageRouting;
import com.nokia.gdc.netact.alarm.socket.NetactSocketServer;
import com.nokia.gdc.ticket.services.AlarmTroubleTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Arindra
 */
@RestController
@RequestMapping("/smanager/listener")
public class SocketListenerController {
    
    @Autowired
    AlarmTroubleTicketService alarmTroubleTicketService;
    
    @Autowired
    MessageRouting messageRouting;
    
     @Autowired
     AlarmLoggerService alarmLoggerService;
    
    @Autowired
    ThreadPoolTaskExecutor taskExecutor;
    
    @RequestMapping("/start")
    public NetactSocketServer create() {
        if (NetactAlarmConnector.socketServer == null) {
            NetactAlarmConnector.socketServer = new NetactSocketServer(7566);
        }
        NetactAlarmConnector.socketServer.socketBind();
        return NetactAlarmConnector.socketServer;
    }
    
    @RequestMapping("/start-ef")
    public void createQueueListener() {
        //ticketServiceBroker.setIsStart(true);
        taskExecutor.execute(alarmTroubleTicketService);
        taskExecutor.execute(messageRouting);
        taskExecutor.execute(alarmLoggerService);
        
    }
    
    @RequestMapping("/check-queue")
    public int queueSize() {
        return NetactAlarmConnector.alarmObjectQueue.size();
    }

    @RequestMapping("/stop")
    public NetactSocketServer stop() {
        if (NetactAlarmConnector.socketServer != null) {
            NetactAlarmConnector.socketServer.socketUnBind();
            return NetactAlarmConnector.socketServer;
        }
         
        return null;
    }

    @RequestMapping("/status")
    public NetactSocketServer status() {
       return NetactAlarmConnector.socketServer;
    }

}
