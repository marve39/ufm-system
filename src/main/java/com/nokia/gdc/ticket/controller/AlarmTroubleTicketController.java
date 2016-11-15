/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.controller;

import com.nokia.gdc.ticket.domain.TroubleTicket;
import com.nokia.gdc.ticket.repositories.TroubleTicketRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Arindra
 */
@RestController
@RequestMapping("/ticket/alarm")
public class AlarmTroubleTicketController {
    
    @Autowired
    TroubleTicketRepository ttRepo;
    
    @RequestMapping("/list")
    public List<TroubleTicket> create() {
        return ttRepo.findAllTT();
    }
}
