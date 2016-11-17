/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.services;

import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import java.math.BigInteger;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class MantisConnectService {
    private final String user;
    private final String password;
    private final String mantisEndpoint;
    
    protected MantisConnectService(){
        this(null,null,null);
    }
    
    public void setHTTPProxy(String host, String port){
         if(!host.isEmpty() && !port.isEmpty()){
             AxisProperties.setProperty("http.proxyHost",host);
             AxisProperties.setProperty("http.proxyPort",port);
         }
    }
    
    public void setHTTPSProxy(String host, String port){
        if(!host.isEmpty() && !port.isEmpty()){
             AxisProperties.setProperty("https.proxyHost",host);
             AxisProperties.setProperty("https.proxyPort",port);
         }
    }
   
    /*
    public void createTicket(){
        Service service = new Service();
        try {
            MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
            return mc.mc_issue_add(username, password, issue);
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }*/
}
