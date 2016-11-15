/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.netact.alarm.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.netact.alarm.domain.NetactAlarm;
import java.net.InetSocketAddress;
import java.util.Date;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arindra
 */
public class NetactAlarmForwardingHandler extends IoHandlerAdapter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    
    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getServiceAddress();

        String str = message.toString();
        logger.info("Message Recieved [ " + session.getRemoteAddress().toString() + " ]");
        logger.debug("[" + session.getRemoteAddress().toString() + " ] ===START MESSAGE=== " + str + " ===END MESSAGE===");
        try {
            NetactAlarm alarmObject = new NetactAlarm(inetSocketAddress, str);
            alarmObject.parseAlarm();
            NetactAlarmConnector.enterpriseFramework.entranceQueue.add(alarmObject);
             ObjectMapper mapper = new ObjectMapper();
             logger.info("Message sent to Enterprise Bus");
             logger.debug(mapper.writeValueAsString(alarmObject));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        logger.debug("IDLE " + session.getIdleCount(status));
    }
}
