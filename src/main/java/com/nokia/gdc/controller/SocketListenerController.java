/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.controller;

import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.domain.NetactSocketServer;
import com.nokia.gdc.services.TicketServiceBroker;
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
    TicketServiceBroker ticketServiceBroker;
    
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
    
    @RequestMapping("/start-queue")
    public void createQueueListener() {
        //ticketServiceBroker.setIsStart(true);
        taskExecutor.execute(ticketServiceBroker);
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
