/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import org.h2.jdbc.JdbcClob;
import org.hibernate.Hibernate;

/**
 *
 * @author Arindra
 */
@Converter
public class JpaClobConverterJson implements AttributeConverter<Object, Clob> {

    private final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Object convertToEntityAttribute(Clob clob) {

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            Reader reader = clob.getCharacterStream();

            int byteRead = reader.read();
            while (byteRead != -1) {
                os.write(byteRead);
                byteRead = reader.read();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return os.toString();
    }

    @Override
    public Clob convertToDatabaseColumn(Object meta) {
        try {
            String jsonObj = objectMapper.writeValueAsString(meta);

            if (jsonObj == null) {
                return null;
            }
            Clob clob = null;
           // try {
            //    clob = new JdbcClob(jsonObj);
        //    } catch (SQLException e) {
          //      e.printStackTrace();
            //}
            return clob;
        } catch (JsonProcessingException ex) {
            Logger.getLogger(JpaClobConverterJson.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }
}
