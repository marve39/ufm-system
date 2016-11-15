/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.common.domain;

import java.util.List;

/**
 *
 * @author Arindra
 */
public interface EnterpriseBus {
    List<MessageBus> pullMessage();
}
