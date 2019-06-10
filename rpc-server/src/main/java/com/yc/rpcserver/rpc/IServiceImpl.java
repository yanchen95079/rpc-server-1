package com.yc.rpcserver.rpc;


import com.yc.rpcapi.IService;

/**
 * @author Yanchen
 * @ClassName ISetviceImpl
 * @Date 2019/6/10 10:20
 */
@RpcService(value = IService.class,version = "v1.0")
public class IServiceImpl implements IService {

    @Override
    public String work(String content) {
        System.out.println("1.0:"+content);
        return "1.0"+content;
    }
}
