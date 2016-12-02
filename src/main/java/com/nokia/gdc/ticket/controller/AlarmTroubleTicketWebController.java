/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.controller;

import com.nokia.gdc.ticket.repositories.TroubleTicketRepository;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import com.nokia.gdc.ticket.domain.TroubleTicket;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.GetMapping;

/**
 *
 * @author Arindra
 */
@Controller
//@RequestMapping("/t/web")
public class AlarmTroubleTicketWebController {

    @Autowired
    TroubleTicketRepository ttRepo;

    @GetMapping("/t/web/list")
   // @RequestMapping("/list")//
    public String getListTicket(Map<String, Object> model) {
        List<TroubleTicket> listTT = ttRepo.findAllTT(new PageRequest(0,100));
        model.put("listTT", listTT);
        System.out.println("MASUSSSSSSSSSSSSSSSSSSSSSSSSSK");
        return "listTicket";
    }

}
