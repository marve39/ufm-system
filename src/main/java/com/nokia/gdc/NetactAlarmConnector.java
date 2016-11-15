package com.nokia.gdc;

import com.nokia.gdc.domain.NetactSocketServer;
import com.nokia.gdc.domain.NetactAlarm;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@SpringBootApplication
@EnableAsync
public class NetactAlarmConnector {

  //  private static final HashMap<Integer, SocketInfo> socketListener = new HashMap<Integer, SocketInfo>();
    public static NetactSocketServer socketServer;
    public final static LinkedBlockingQueue<NetactAlarm> alarmObjectQueue = new LinkedBlockingQueue<NetactAlarm>();
    public final static LinkedBlockingQueue<NetactAlarm> ticketOperationQueue = new LinkedBlockingQueue<NetactAlarm>();
    private static boolean isProducerReady;
    
    @Bean
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);
        executor.setMaxPoolSize(1);
        executor.setQueueCapacity(100);
        return executor;
    }
    
    public static void main(String[] args) {
        SpringApplication.run(NetactAlarmConnector.class, args);
    }
    
    public static synchronized void setProducerIsReady(boolean isReady){
        isProducerReady = isReady;
    }
    
    public static synchronized boolean getProducerStatus(){
        return isProducerReady;
    }
}
