/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.InetSocketAddress;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class NetactAlarm {

    private final InetSocketAddress remoteAddress;
    private Long alarmNumber;
    private String severity;
    private String maintenanceRegion;
    private String siteName;
    private String dn;
    private String alarmText;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private Date startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private Date clearedTime;
    private Long intID;
    private Long notifID;
    private String pcText;
    private String supplInfo;
    private String diagInfo;
    private String userInfo;
    private String objectName;
    private String alarmType;
    private final String message;
    private String ticketTitle;
    
    
    protected NetactAlarm() throws Exception {
        this(null,null);
    }
    
    public String[] CSV(){
        String[] csv = new String[17];
        csv[0] = String.valueOf(alarmNumber);
        csv[1] = severity;
        csv[2] = maintenanceRegion;
        csv[3] = siteName;
        csv[4] = dn;
        csv[5] = alarmText;
        csv[6] = startTime.toString();
        csv[7] = clearedTime.toString();
        csv[8] = String.valueOf(intID);
        csv[9] = String.valueOf(notifID);
        csv[10] = pcText;
        csv[11] = supplInfo;
        csv[12] = diagInfo;
        csv[13] = userInfo;
        csv[14] = objectName;
        csv[15] = alarmType;
        csv[16] = message;
        return csv;
    }

    /*
    
    #S#1049690 C MR-BSC_TBD 
FBSC_TESTBED 
PLMN-PLMN/BSC-400876/FUN-VTP-32 
WORKING STATE CHANGE 
Started - Cancelled 2016-10-26 21:56:59 
IntId 0 NotifId 69474 
PC TEXT Indeterminate 
Suppl Info BL-SY WO-BU 0000 0098 5993 00000000 00000000 00000000 
Diag Info 
User info 
Object FBSC_TESTBED EQUIPMENT 
#E#

#S#alarmNumber severity maintenanceRegion
siteName 
dn 
alarmText 
Started alarmTime -  condition new alarm
Started – cancelOrUpdateTag cancelTime  condition update alarm status
IntId valueofIntId NotifId notificationId 
PC TEXT pcText 	
Suppl Info additionalInfo
Diag Info diagInfo
User info userAddInfo
Object objectName alarmType 
#E#


    
     */
    public void parseAlarm() throws IOException, ParseException {
        BufferedReader bufReader = new BufferedReader(new StringReader(message.replaceAll("(\\\\r)?\\\\n", System.getProperty("line.separator"))));
        String line = null;
        int lineNumber = 100;
        while ((line = bufReader.readLine()) != null) {
            if (line.contains("#S#")) {
                lineNumber = 1;
            } else {
                if (line.trim().isEmpty()) continue;
                lineNumber = lineNumber + 1;
            }

            if (lineNumber == 1) {
                //#S#1049690 C MR-BSC_TBD
                line = line.replaceAll("#S#", "");
                String[] textSplit = line.trim().split(" ");
                alarmNumber = Long.parseLong(textSplit[0]);
                severity = textSplit[1];
                maintenanceRegion = textSplit[2];
            }

            if (lineNumber == 2) {
                siteName = line.trim();
            }
            if (lineNumber == 3) {
                dn = line.trim();
            }
            if (lineNumber == 4) {
                alarmText = line.trim();
            }

            if (lineNumber == 5) {
                /*
            Started alarmTime -  condition new alarm
            Started 2016-10-26 21:56:59 -  
            
            Started – cancelOrUpdateTag cancelTime  condition update alarm status
            Started - Cancelled 2016-10-26 21:56:59 
                 */
                String[] textSplit = line.trim().split(" ");
                if (line.toLowerCase().contains("cancelled")) {
                    clearedTime = parseDateTime(textSplit[3] + " " + textSplit[4]);
                } else {
                    startTime = parseDateTime(textSplit[1] + " " + textSplit[2]);
                }
            }

            if (lineNumber == 6) {
                //IntId 0 NotifId 69474 
                //IntId valueofIntId NotifId notificationId
                String[] textSplit = line.trim().split(" ");
                intID = Long.parseLong(textSplit[1]);
                notifID = Long.parseLong(textSplit[3]);
            }
            
            if (lineNumber == 7){
                //PC TEXT Indeterminate
                //PC TEXT pcText
                pcText = line.toLowerCase().replaceAll("pc text", "").trim();
            }
            
            if (lineNumber == 8){
                //Suppl Info BL-SY WO-BU 0000 0098 5993 00000000 00000000 00000000
                //Suppl Info additionalInfo
                supplInfo = line.toLowerCase().replaceAll("suppl info", "").trim();
            }
            
            if (lineNumber == 9){
                //Diag Info diagInfo
                diagInfo = line.toLowerCase().replaceAll("diag info", "").trim();
            }
            
            if (lineNumber == 10){
                //User info userAddInfo
                userInfo = line.toLowerCase().replaceAll("user info", "").trim();
            }
            if (lineNumber == 11){
                //Object FBSC_TESTBED EQUIPMENT 
                //Object objectName alarmType 
                String[] textSplit = line.trim().split(" ");
                objectName = textSplit[1];
                alarmType = textSplit[2];
            }
        }
        this.ticketTitle = siteName + "|" + alarmNumber;
    }

    private Date parseDateTime(String date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(date);
    }
    
    public EventCDR getEventCDR(){
        return new EventCDR("alarm",remoteAddress.getAddress().getHostAddress(),remoteAddress.getPort(),startTime,ticketTitle);
    }
    
    public String ticketStatus(){
        if (startTime != null){
            return "open";
        }else{
            return "close";
        }
    }

}
