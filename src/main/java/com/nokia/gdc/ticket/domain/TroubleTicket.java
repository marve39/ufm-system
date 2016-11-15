/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.domain;

import com.nokia.gdc.common.domain.EventCDR;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.gdc.utils.JpaBlobConverterJson;
import com.nokia.gdc.utils.JpaConverterJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Transient;
import org.hibernate.annotations.Type;

/**
 *
 * @author Arindra
 */
@Entity
@Data
@RequiredArgsConstructor
public class TroubleTicket {

    private @Id String internalTicketId;
    private String status;
    private final String title;
    private final String severity;

    @Convert(converter = JpaBlobConverterJson.class)
    //@Lob
    private List<EventCDR> cdr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date timeCreation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private Date timeClosed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date firstOpenAlarmTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date firstCloseAlarmTime;
    private String lastTriggerBy;
    private String externalTicketID;

    @JsonIgnore
    @Transient
   // private String[] STATUS = {"pre-open", "open", "pre-closed", "closed"};
    private String[] STATUS = {"pre-open", "open", "pre-closed", "closed"};

    protected TroubleTicket() {
        this(null, null, null, null, null);
    }

    public void increaseStatus() {
        int i = Arrays.asList(STATUS).indexOf(this.status.toLowerCase());
        this.status = STATUS[i++];
    }
    
    public void putExternalTicketID(String externalTicketId){
        this.externalTicketID = externalTicketId;
    }
    
    public void generateID(){
        this.internalTicketId = UUID.randomUUID().toString();
    }

    public void addCDR(EventCDR newCDR) {
        ArrayList<EventCDR> cdrList = this.cdr != null?new ArrayList<EventCDR>(this.cdr):new ArrayList<EventCDR>();
        cdrList.add(newCDR);
        this.cdr = cdrList;

    }
    
    public void closeTicket(Date timeClose){
        this.status = "close";
        this.timeClosed = timeClose;
    }

}
