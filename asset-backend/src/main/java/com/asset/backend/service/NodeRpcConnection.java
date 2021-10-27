/*
 * Author Varatharaj K
 * varatharaj@digiledge.com
 * Copyright © Immergro Technologies PVT LTD
 */

package com.asset.backend.service;

import net.corda.client.rpc.CordaRPCClient;
import net.corda.client.rpc.CordaRPCConnection;
import net.corda.core.messaging.CordaRPCOps;
import net.corda.core.utilities.NetworkHostAndPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;


@Component
public class NodeRpcConnection {



    private CordaRPCConnection rpcConnection;

    public CordaRPCOps getProxy() {
        return proxy;
    }

    public void setProxy(CordaRPCOps proxy) {
        this.proxy = proxy;
    }

    private CordaRPCOps proxy;

    private static Logger logger = LoggerFactory.getLogger(NodeRpcConnection.class);


    @Value("${config.rpc.host}") private String hostName;
    @Value("${config.rpc.username}") private String userName;
    @Value("${config.rpc.password}") private String password;
    @Value("${config.rpc.port}") private int port;
    @Value("${withBlockChain}") private Boolean withBlockChain;
    @PostConstruct
    public void init() {

        if (!withBlockChain) {
            return;
        }

        NetworkHostAndPort rpcAddress = new NetworkHostAndPort(hostName, port);
        CordaRPCClient rpcClient = new CordaRPCClient(rpcAddress);
        CordaRPCConnection rpcConnection = rpcClient.start(userName, password);
        proxy = rpcConnection.getProxy();
        logger.info("******************Node time is"+proxy.currentNodeTime().toString());

    }

    @PreDestroy
    public void destroy(){
        if (!withBlockChain) {
            return;
        }
        rpcConnection.notifyServerAndClose();
    }

}
