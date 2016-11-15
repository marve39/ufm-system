/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.sql.Blob;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

/**
 *
 * @author Arindra
 */
@Converter
public class JpaBlobConverterJson implements AttributeConverter<Object, Blob> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Blob convertToDatabaseColumn(Object meta) {
        try {
            String strJson = objectMapper.writeValueAsString(meta);
            byte[] buff = strJson.getBytes();
            return new SerialBlob(buff);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public Object convertToEntityAttribute(Blob dbData) {
        try {
            byte[] bdata = dbData.getBytes(1, (int) dbData.length());
            return objectMapper.readValue(new String(bdata), Object.class);
        } catch (Exception ex) {
            // logger.error("Unexpected IOEx decoding json from database: " + dbData);
            ex.printStackTrace();
        }
        return null;
    }
}
