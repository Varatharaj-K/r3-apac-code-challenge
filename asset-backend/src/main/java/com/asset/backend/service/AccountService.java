package com.asset.backend.service;

import com.asset.accounts.CreateNewAccount;
import com.asset.backend.models.request.AccountCreationRequest;
import com.asset.flows.ShipAssetFlow;
import com.asset.states.AssetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    NodeRpcConnection nodeRpcConnection;

    /**
     * Processing Account Creation Request
     * Invoking Account Create CORDA Flow
     * */
    public String createCordaAccount(AccountCreationRequest accountCreationRequest) throws ExecutionException, InterruptedException {
        logger.info("Invoking Account create Flow");
        String  accountUniqueId = nodeRpcConnection.getProxy().startFlowDynamic(
                CreateNewAccount.class,accountCreationRequest.getAccountName()

        ).getReturnValue().get();
        logger.info("Account create invoked");
        return accountUniqueId;
    }
}
