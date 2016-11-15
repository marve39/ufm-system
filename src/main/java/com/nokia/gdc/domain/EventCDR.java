/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class EventCDR {
    private final String eventType;
    private final String remoteIP;
    private final int remotePort;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private final Date timeReceived;
    private final String description;
    
     protected EventCDR(){
        this(null,null,0,null,null);
    }
}
