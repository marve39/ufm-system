/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.domain;

import com.nokia.gdc.common.domain.EventCDR;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.gdc.utils.JpaConverterJson;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import javax.persistence.Id;
import javax.persistence.Transient;

/**
 *
 * @author Arindra
 */
@Entity
@Data
@RequiredArgsConstructor
public class TroubleTicket {

    private @Id
    @GeneratedValue
    Long internalTicketId;
    private String status;
    private final String title;
    private final String severity;

    @Convert(converter = JpaConverterJson.class)
    private List<EventCDR> cdr;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date timeCreation;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date timeClosed;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date firstOpenAlarmTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date firstCloseAlarmTime;

    private final String lastTriggerBy;

    @JsonIgnore
    @Transient
   // private String[] STATUS = {"pre-open", "open", "pre-closed", "closed"};
    private String[] STATUS = {"pre-open", "open", "pre-closed", "closed"};

    protected TroubleTicket() {
        this(null, null, null, null, null, null, null);
    }

    public void increaseStatus() {
        int i = Arrays.asList(STATUS).indexOf(this.status.toLowerCase());
        this.status = STATUS[i++];
    }

    public void addCDR(EventCDR newCDR) {
        ArrayList<EventCDR> cdrList = new ArrayList<EventCDR>(this.cdr);
        cdrList.add(newCDR);
        this.cdr = cdrList;

    }

}
