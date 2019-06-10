package com.yc.rpcserver.rpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Yanchen
 * @ClassName SpringConfig
 * @Date 2019/6/10 10:12
 */
@Configuration
@ComponentScan(basePackages = "com.yc.rpc.rpc")
public class SpringConfig {

    @Bean(name="rpcServer")
    public RpcServer rpcServer(){
        return new RpcServer(8080);
    }
}
