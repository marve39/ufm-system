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
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author Arindra
 */
@RestController
@RequestMapping("/ticket/alarm")
public class AlarmTroubleTicketController {
    
    @Autowired
    TroubleTicketRepository ttRepo;
    
   @RequestMapping("/list/json/{limit}")
    public List<TroubleTicket> listAll(@PathVariable("limit") Integer limit) {
        List<TroubleTicket> listTT =  ttRepo.findAllTT(new PageRequest(0,limit));
       // System.out.println("SIZE === " + listTT.size());
        return listTT;
    }
    
    @RequestMapping("/list/row/{limit}")
    public String listTTCSV(@PathVariable("limit") Integer limit){
        String ret = "";
        for(TroubleTicket tt: ttRepo.findAllTT(new PageRequest(0,limit))){
            try {
                ret = ret + tt.unmarshallObject() + "\n";
             //   System.out.println(ret);
            } catch (JsonProcessingException ex) {
                Logger.getLogger(AlarmTroubleTicketController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return ret;
    }
}
