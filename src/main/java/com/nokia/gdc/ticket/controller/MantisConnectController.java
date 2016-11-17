/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.controller;

import biz.futureware.mantis.rpc.soap.client.IssueData;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import biz.futureware.mantis.rpc.soap.client.MantisConnectBindingStub;
import biz.futureware.mantis.rpc.soap.client.ProjectData;
import java.math.BigInteger;
import org.apache.axis.AxisProperties;
import org.apache.axis.client.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * @author Arindra
 */
@RestController
public class MantisConnectController {

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

    private void setProxyOnAxis() {
        if(!httpProxyHost.isEmpty()) AxisProperties.setProperty("http.proxyHost",httpProxyHost);
        if(!httpProxyPort.isEmpty()) AxisProperties.setProperty("http.proxyPort",httpProxyPort);
        if(!httpsProxyHost.isEmpty()) AxisProperties.setProperty("https.proxyHost",httpsProxyHost);
        if(!httpsProxyPort.isEmpty()) AxisProperties.setProperty("https.proxyPort",httpsProxyPort);
    }

    @RequestMapping("/mantisconnect/mc_version")
    public String getMCVersion() {//@PathVariable("port") Integer port){
        Service service = new Service();
        try {
            setProxyOnAxis();
            MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
            return mc.mc_version();
        } catch (Exception ex) {
            ex.printStackTrace();
            return "";
        }
    }
    
    @RequestMapping("/mantisconnect/mc_projects_get_user_accessible/{username}/{password}")
    public ProjectData[] getMCProjectsGetUserAccessible(@PathVariable("username") String username, @PathVariable("password") String password){
        Service service = new Service();
        try {
            setProxyOnAxis();
            MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
            return mc.mc_projects_get_user_accessible(username, password);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
     @RequestMapping("/mantisconnect/mc_issue_get/{username}/{password}/{projectID}")
    public IssueData getMCProjectsGetUserAccessible(@PathVariable("username") String username, @PathVariable("password") String password, @PathVariable("projectID") BigInteger projectID){
        Service service = new Service();
        try {
            setProxyOnAxis();
            MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
            return mc.mc_issue_get(username, password, projectID);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/mantisconnect/mc_issue_add/{username}/{password}")
    public IssueData getMCProjectsGetUserAccessible(@RequestBody IssueData issueData, @PathVariable("username") String username, @PathVariable("password") String password){
        Service service = new Service();
        
     //   System.out.println("Project" + issueData.getProject().getId() + " " + issueData.getProject().getName());
     //   System.out.println("Description" + issueData.getDescription());
        
        try {
            setProxyOnAxis();
            MantisConnectBindingStub mc = new MantisConnectBindingStub(new java.net.URL(mantisEndpoint), service);
      //      issueData = mc.mc_issue_get(username, password, BigInteger.valueOf(Long.parseLong("60")));
            BigInteger issueID = mc.mc_issue_add(username, password, issueData);
            return mc.mc_issue_get(username, password, issueID);
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
