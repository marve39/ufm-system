/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.services;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.ObjectRef;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import com.nokia.gdc.ticket.domain.TroubleTicket;
import java.math.BigInteger;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Arindra
 */
@org.springframework.stereotype.Service
public class MantisConnectService {
    
    @Value("${biz.futureware.mantis.rpc.soap.client.endpoint:}")
    private String mantisEndpoint;
    
    @Value("${biz.futureware.mantis.rpc.soap.client.user:}")
    private String mantisUser;
    
    @Value("${biz.futureware.mantis.rpc.soap.client.password:}")
    private String mantisPassword;

    @Value("${org.apache.axis.AxisProperties.http.proxy.host:}")
    private String httpProxyHost;

    @Value("${org.apache.axis.AxisProperties.http.proxy.port:}")
    private String httpProxyPort;

    @Value("${org.apache.axis.AxisProperties.https.proxy.host:}")
    private String httpsProxyHost;

    @Value("${org.apache.axis.AxisProperties.https.proxy.port:}")
    private String httpsProxyPort;
    
    @Value("${com.nokia.gdc.ticket.service.mantisconnect.project.id:}")
    private String mantisProjectId;
    
    @Value("${com.nokia.gdc.ticket.service.mantisconnect.status.open.id:}")
    private String mantisStatusOpenId;
    
    @Value("${com.nokia.gdc.ticket.service.mantisconnect.status.close.id:}")
    private String mantisStatusCloseId;
    
     private void setProxyOnAxis() {
        if(!httpProxyHost.isEmpty()) AxisProperties.setProperty("http.proxyHost",httpProxyHost);
        if(!httpProxyPort.isEmpty()) AxisProperties.setProperty("http.proxyPort",httpProxyPort);
        if(!httpsProxyHost.isEmpty()) AxisProperties.setProperty("https.proxyHost",httpsProxyHost);
        if(!httpsProxyPort.isEmpty()) AxisProperties.setProperty("https.proxyPort",httpsProxyPort);
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
    
   
     public BigInteger createTicket(TroubleTicket tt) {
        Service service = new Service();
        
        try {
            setProxyOnAxis();
      //      setHTTPProxy("10.144.1.10","8080");
       /*     mantisEndpoint = "http://gdcindonesia.com:8140/api/soap/mantisconnect.php";
            mantisUser = "submiter";
            mantisPassword = "12345";
       */     MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
      
            ObjectRef project = new ObjectRef();
            project.setId(new BigInteger(mantisProjectId));
            
            ObjectRef status = new ObjectRef();
            status.setId(new BigInteger(mantisStatusOpenId));
            
            IssueData issueData = new IssueData();
                    
            issueData.setProject(project);
            issueData.setStatus(status);
            issueData.setSummary(tt.getTitle());
            issueData.setDescription(tt.getTitle());
              
            BigInteger issueID = mc.mc_issue_add(mantisUser, mantisPassword, issueData);
            return issueID;
        //    System.out.println("Issue ID => " + issueID);
        //    return mc.mc_issue_get(username, password, issueID);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
     }
     
     public Boolean closeTicket(TroubleTicket tt){
        Service service = new Service();
        
        try {
            setProxyOnAxis();
        /*    setHTTPProxy("10.144.1.10","8080");
            mantisEndpoint = "http://gdcindonesia.com:8140/api/soap/mantisconnect.php";
            mantisUser = "submiter";
            mantisPassword = "12345";
         */   MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
      
            BigInteger issueID = new BigInteger(tt.getExternalTicketID());
            
            IssueData issueData = mc.mc_issue_get(mantisUser, mantisPassword, issueID);
            
            ObjectRef status = new ObjectRef();
            status.setId(new BigInteger(mantisStatusCloseId));
            
            issueData.setStatus(status);
              
            return mc.mc_issue_update(mantisUser, mantisPassword, issueID, issueData);
            //    System.out.println("Issue ID => " + issueID);
        //    return mc.mc_issue_get(username, password, issueID);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
     }
}
