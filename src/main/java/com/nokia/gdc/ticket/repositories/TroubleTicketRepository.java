/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.ticket.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;
import com.nokia.gdc.ticket.domain.TroubleTicket;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Arindra
 */
@Transactional
public interface TroubleTicketRepository extends CrudRepository<TroubleTicket, Long> {
    @Query("SELECT tt FROM TroubleTicket tt WHERE LOWER(tt.status) = LOWER(:status) and LOWER(tt.title) = LOWER(:title)")
    public List<TroubleTicket> findTT(@Param("status") String status, @Param("title") String title);
    
    @Query("SELECT tt FROM TroubleTicket tt order by tt.timeCreation desc")
    public List<TroubleTicket> findAllTT();
    
    @Query("SELECT tt FROM TroubleTicket tt where tt.externalTicketID = 'null' order by tt.timeCreation desc")
    public List<TroubleTicket> findNullAtWTT();
}
