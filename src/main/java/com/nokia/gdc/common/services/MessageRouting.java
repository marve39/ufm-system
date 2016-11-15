/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.common.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.common.domain.EnterpriseBus;
import com.nokia.gdc.common.domain.MessageBus;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author Arindra
 */
@Service
public class MessageRouting implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void run() {
        long threadId = Thread.currentThread().getId();
        logger.info("[ThreadID:" + threadId + ",errorcode:0,message:Message Route Start... ]");
        EnterpriseBus eb;
        ObjectMapper mapper = new ObjectMapper();
        while (true) {
            try {
                eb = NetactAlarmConnector.enterpriseFramework.entranceQueue.poll(1, TimeUnit.MINUTES);
                if (eb != null) {
                    List<MessageBus> listMessage = eb.pullMessage();
                    System.out.println("===Message Route Received => " + listMessage.size());
                    logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Received message with size (" + listMessage.size() + ") ]");
                    for (MessageBus message : listMessage) {
                        String jsonMessage;
                        try {
                            jsonMessage = mapper.writeValueAsString(message.getMessageObject());
                        } catch (JsonProcessingException ex) {
                            jsonMessage = "Message Cannot translated";
                        }
                        switch (message.getClassName()) {
                            case "com.nokia.gdc.AlarmTroubleTicketService":
                                NetactAlarmConnector.enterpriseFramework.alarmTicketQueue.add(message.getMessageObject());
                                logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Message sent to com.nokia.gdc.TicketBroker (" + jsonMessage + ") ]");
                                break;
                            case "com.nokia.gdc.AlarmLoggerService":
                                NetactAlarmConnector.enterpriseFramework.alarmLoggerQueue.add(message.getMessageObject());
                                logger.debug("[ThreadID:" + threadId + ",errorcode:0,message:Message sent to com.nokia.gdc.AlarmLoggerService (" + jsonMessage + ") ]");
                                break;
                        }
                    }
                }
            } catch (InterruptedException ex) {
                 logger.info("[ThreadID:" + threadId + ",errorcode:0,message:Thread Closing ..");
            }

        }
    }

}
