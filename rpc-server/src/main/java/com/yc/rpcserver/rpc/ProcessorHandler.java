package com.yc.rpcserver.rpc;

import com.yc.rpcapi.RpcRequest;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;

/**
 * @author Yanchen
 * @ClassName ProcessorHandler
 * @Date 2019/6/10 10:13
 */
public class ProcessorHandler implements Runnable{

    private Socket socket;
    private Map<String,Object> handlerMap;


    public ProcessorHandler(Socket socket, Map<String,Object> handlerMap) {
        this.socket = socket;
        this.handlerMap = handlerMap;
    }

    @Override
    public void run() {
        ObjectInputStream objectInputStream=null;
        ObjectOutputStream objectOutputStream=null;

        try {
            objectInputStream=new ObjectInputStream(socket.getInputStream());
            RpcRequest rpcRequest=(RpcRequest)objectInputStream.readObject();
            Object result=invoke(rpcRequest);
            objectOutputStream=new ObjectOutputStream(socket.getOutputStream());
            objectOutputStream.writeObject(result);
            objectOutputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }finally {
            if(objectInputStream!=null){
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(objectOutputStream!=null){
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private Object invoke(RpcRequest request) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        String serviceName=request.getClassName();
        String version=request.getVersion();
        if(!StringUtils.isEmpty(version)){
            serviceName+="-"+version;
        }
        Object service=handlerMap.get(serviceName);
        if(service==null){
            throw new RuntimeException("service not found:"+serviceName);
        }
        Object[] args=request.getParameters();
        Method method=null;
        if(args!=null) {
            Class<?>[] types = new Class[args.length];
            for (int i = 0; i < args.length; i++) {
                types[i] = args[i].getClass();
            }
            Class clazz=Class.forName(request.getClassName());
            method=clazz.getMethod(request.getMethodName(),types);
        }else{
            Class clazz=Class.forName(request.getClassName());
            method=clazz.getMethod(request.getMethodName());
        }
        Object result=method.invoke(service,args);
        return result;
    }
}
