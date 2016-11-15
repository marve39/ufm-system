/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nokia.gdc.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nokia.gdc.socket.NetactAlarmForwardingHandler;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Calendar;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.apache.mina.core.service.IoAcceptor;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

/**
 *
 * @author Arindra
 */
@Data
@RequiredArgsConstructor
public class NetactSocketServer {

    private final Integer port;
    private Boolean isActive = false;
    private Boolean isDisposing = false;
    private Boolean isDisposed = false;
    private Integer incomingCounter = 0;
    private Integer processingCounter = 0;
    private String message;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "MM/dd/yyyy HH:mm:ss")
    private Calendar createdTime;
    private Long lifeTime;

    @JsonIgnore
    private IoAcceptor acceptor;
    @JsonIgnore
    private String SUCCESS = "[SUCCESS] - Operation Success";
    @JsonIgnore
    private String ALREADY_BOUND = "[FAILED] - Socket Already Bound";
    @JsonIgnore
    private Boolean isAcceptorPrepared = false;

    protected NetactSocketServer() {
        this(null);
    }

    //   public void setNewAcceptor(IoAcceptor acceptor) {
    //       this.acceptor = acceptor;
    //   }
    private void prepareAcceptor() {

        /*ClassLoader classLoader = this.getClass().getClassLoader();
        Class loadedMyClass = classLoader.loadClass("com.nokia.gdc.socket."+ classHandlerName);
        Constructor constructor = loadedMyClass.getConstructor();
         */
        LineDelimiter line = new LineDelimiter("#E#");
        TextLineCodecFactory txtFac = new TextLineCodecFactory(Charset.forName("UTF-8"), line, line);
        txtFac.setDecoderMaxLineLength(10240);

        acceptor = new NioSocketAcceptor();
        acceptor.getFilterChain().addLast("logger", new LoggingFilter("com.nokia.gdc.NetactAlarmForwarding"));
        acceptor.getFilterChain().addLast("codec", new ProtocolCodecFilter(txtFac));

        acceptor.setHandler(new NetactAlarmForwardingHandler());
        acceptor.getSessionConfig().setReadBufferSize(2048);
        acceptor.getSessionConfig().setIdleTime(IdleStatus.BOTH_IDLE, 10);

        incomingCounter = 0;
        processingCounter = 0;
        lifeTime = new Long(0);
    }

    public boolean socketBind() {

        if (!isAcceptorPrepared){
            prepareAcceptor();
            isAcceptorPrepared = false;
        }
        if (!acceptor.isActive()) {
            try {
                acceptor.bind(new InetSocketAddress(this.port));
            } catch (IOException ex) {
                this.message = "[FAILED] - " + ex.getMessage();
                return false;
            }
            createdTime = Calendar.getInstance();
            this.message = SUCCESS;
            return true;
        }
        this.message = ALREADY_BOUND;
        return false;
    }

    public boolean socketUnBind() {

        if (acceptor.isActive()) {
            acceptor.unbind();
            acceptor.dispose();
            this.message = SUCCESS;
            return true;
        }
        return false;
    }

    public void refreshInfo() {
        if (createdTime != null && isActive) {
            lifeTime = (Calendar.getInstance().getTimeInMillis() - createdTime.getTimeInMillis()) / 1000;
        }
        if (acceptor != null) {
            isActive = acceptor.isActive();
            isDisposing = acceptor.isDisposing();
            isDisposed = acceptor.isDisposed();
        }
    }

    public void addIncomingCounter(Integer counter) {
        incomingCounter = incomingCounter + counter;
    }

    public void addProcessingCounter(Integer counter) {
        processingCounter = processingCounter + counter;
    }
}
