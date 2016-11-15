/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.socket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nokia.gdc.NetactAlarmConnector;
import com.nokia.gdc.domain.NetactAlarm;
import java.net.InetSocketAddress;
import java.util.Date;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;

/**
 *
 * @author Arindra
 */
public class NetactAlarmForwardingHandler extends IoHandlerAdapter {

    @Override
    public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
        cause.printStackTrace();
    }

    @Override
    public void messageReceived(IoSession session, Object message) throws Exception {
        InetSocketAddress inetSocketAddress = (InetSocketAddress) session.getServiceAddress();

        String str = message.toString();
        System.out.println("===== Message Recieved [ " + session.getRemoteAddress().toString() + " ]======");
        System.out.println(str);
        System.out.println("=======================================================================");
        try {
            NetactAlarm alarmObject = new NetactAlarm(inetSocketAddress, str);
            alarmObject.parseAlarm();
            NetactAlarmConnector.alarmObjectQueue.add(alarmObject);
             ObjectMapper mapper = new ObjectMapper();
             System.out.println("-- SEND ---");
             System.out.println(mapper.writeValueAsString(alarmObject));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
        System.out.println("IDLE " + session.getIdleCount(status));
    }
}
