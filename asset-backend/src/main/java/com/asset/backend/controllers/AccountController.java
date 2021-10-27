package com.asset.backend.controllers;

import com.asset.backend.exceptions.ApplicationException;
import com.asset.backend.models.request.AccountCreationRequest;
import com.asset.backend.models.rest.Acknowledgement;
import com.asset.backend.service.AccountService;
import com.asset.backend.utils.StatusCode;
import com.asset.backend.validations.AssetValidations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/accounts")
public class AccountController {

    private static final Logger logger = LoggerFactory.getLogger(AccountController.class);

    @Autowired
    AccountService accountService;

    /**
     * Controller for Creating a new Corda account
     * @param accountCreationRequest - Account Creation request which contains account name
     * */
    @PostMapping
    public ResponseEntity<?> createAccount(@RequestBody AccountCreationRequest accountCreationRequest) {
        logger.info("POST Request for Create new CORDA Account");
        try {
            logger.info("Account Name: "+accountCreationRequest.getAccountName());
            AssetValidations.validateAccountName(accountCreationRequest.getAccountName());
            String message = accountService.createCordaAccount(accountCreationRequest);
            logger.info("Message: "+message);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Successful.name(),message,"200"), HttpStatus.OK);
        } catch (ApplicationException e) {
            logger.error("Application Exception: "+e.getErrorMessage());
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getErrorMessage(),"403"), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Internal Server Error: ",e);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getMessage(),"500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
