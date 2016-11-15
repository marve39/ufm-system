/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.common;

import com.nokia.gdc.common.domain.EnterpriseBus;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class EnterpriseFramework {
    public LinkedBlockingQueue<Object> alarmTicketQueue = new LinkedBlockingQueue<Object>();
    public LinkedBlockingQueue<Object> alarmLoggerQueue = new LinkedBlockingQueue<Object>();
    public LinkedBlockingQueue<EnterpriseBus> entranceQueue = new LinkedBlockingQueue<EnterpriseBus>();
}
