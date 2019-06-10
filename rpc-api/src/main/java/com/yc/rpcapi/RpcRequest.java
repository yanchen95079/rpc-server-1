package com.yc.rpcapi;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Yanchen
 * @ClassName RpcRequest
 * @Date 2019/6/10 10:09
 */
@Setter
@Getter
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 191979866596113421L;
    private String className;
    private String methodName;
    private Object[] parameters;
    private String version;
}
