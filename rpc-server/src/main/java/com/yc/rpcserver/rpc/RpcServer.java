package com.yc.rpcserver.rpc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Yanchen
 * @ClassName RpcServer
 * @Date 2019/6/10 10:13
 */
public class RpcServer implements ApplicationContextAware,InitializingBean {
    ExecutorService executorService= Executors.newCachedThreadPool();

    private Map<String,Object> handlerMap=new ConcurrentHashMap<String, Object>();

    private int port;

    public RpcServer(int port) {
        this.port = port;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ServerSocket serverSocket=null;
        try {
            serverSocket=new ServerSocket(port);
            while(true){
                Socket socket=serverSocket.accept();
                executorService.execute(new ProcessorHandler(socket,handlerMap));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(serverSocket!=null){
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String,Object> serviceBeanMap=applicationContext.getBeansWithAnnotation(RpcService.class);
        if(!serviceBeanMap.isEmpty()){
            for(Object servcieBean:serviceBeanMap.values()){
                RpcService rpcService=servcieBean.getClass().getAnnotation((RpcService.class));
                String serviceName=rpcService.value().getName();
                String version=rpcService.version();
                if(!StringUtils.isEmpty(version)){
                    serviceName+="-"+version;
                }
                handlerMap.put(serviceName,servcieBean);
            }
        }
    }
}
