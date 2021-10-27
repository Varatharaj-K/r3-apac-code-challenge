package com.asset.backend.controllers;

import com.asset.backend.exceptions.ApplicationException;
import com.asset.backend.models.request.AssetCreateRequest;
import com.asset.backend.models.request.DeliveryUpdateRequest;
import com.asset.backend.models.request.ShipRequest;
import com.asset.backend.models.rest.Acknowledgement;
import com.asset.backend.models.rest.Asset;
import com.asset.backend.service.AssetService;
import com.asset.backend.utils.StatusCode;
import com.asset.backend.validations.AssetValidations;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class AssetController {

    private static final Logger logger = LoggerFactory.getLogger(AssetController.class);

    @Autowired
    private AssetService assetService;

    ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();


    /**
     * Controller to Creating a Asset
     * Steps:
     *      1. Validate the Creation Request
     *      2. Select the Agent
     *      3. Add the Asset to Blockchain
     *      4. Return the Created Asset
     * */
    @PostMapping(value = "/create")
    public ResponseEntity<?> createAsset(@RequestBody AssetCreateRequest assetCreateRequest) {
        logger.info("POST request for Creating new Asset");
        try {
            String assetCreateRequestJson = objectWriter.writeValueAsString(assetCreateRequest);
            logger.info("Asset Creation Request: "+assetCreateRequestJson);
            AssetValidations.validateAssetCreationRequest(assetCreateRequest);
            Asset asset = assetService.processAssetCreateRequest(assetCreateRequest);
            String assetJson = objectWriter.writeValueAsString(asset);
            logger.info("Created Asset: "+assetJson);
            return new ResponseEntity<>(asset,HttpStatus.OK);        } catch (ApplicationException e) {
            logger.error("Application Exception: "+e.getErrorMessage());
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getErrorMessage(),"403"), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Internal Server Error: ",e);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getMessage(),"500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Controller to send a shipRequest to Shipper
     * Steps:
     *      1. Select the Origin
     *      2. Select the Shipper
     *      3. Update the Asset in Blockchain
     * */
    @PutMapping(value = "/ship")
    public ResponseEntity<?> shipTheAsset(@RequestBody ShipRequest shipRequest) {
        logger.info("PUT request for Shipment Request");
        try {
            String shipRequestJson = objectWriter.writeValueAsString(shipRequest);
            logger.info("ShipRequest: "+shipRequestJson);
            AssetValidations.validateShipRequest(shipRequest);
            Asset asset = assetService.processShipAssetRequest(shipRequest);
            String assetJson = objectWriter.writeValueAsString(asset);
            logger.info("Created Asset: "+assetJson);
            return new ResponseEntity<>(asset,HttpStatus.OK);
        } catch (ApplicationException e) {
            logger.error("Application Exception: "+e.getErrorMessage());
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getErrorMessage(),"403"), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Internal Server Error: ",e);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getMessage(),"500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Controller to Update Delivery Detail
     * Steps:
     *      1. Set the Destination
     *      2. Update the Asset in Blockchain
     * */
    @PutMapping(value = "/deliver")
    public ResponseEntity<?> deliverTheAsset(@RequestBody DeliveryUpdateRequest deliveryUpdateRequest) {
        logger.info("PUT request for DeliverUpdate");
        try {
            String deliveryUpdateRequestJson = objectWriter.writeValueAsString(deliveryUpdateRequest);
            logger.info("DeliveryUpdateRequest: "+deliveryUpdateRequestJson);
            AssetValidations.validateDeliveryUpdateRequest(deliveryUpdateRequest);
            Asset asset = assetService.processDeliveryUpdateRequest(deliveryUpdateRequest);
            String assetJson = objectWriter.writeValueAsString(asset);
            logger.info("Created Asset: "+assetJson);
            return new ResponseEntity<>(asset,HttpStatus.OK);
        } catch (ApplicationException e) {
            logger.error("Application Exception: "+e.getErrorMessage());
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getErrorMessage(),"403"), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Internal Server Error: ",e);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getMessage(),"500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Controller to fetch the asset based on Account name
     * Steps:
     *      1. Fetch the Assets from Blockchain
     * */
    @GetMapping("/myasset")
    public ResponseEntity<?> getMyAssets(@RequestParam String accountName) {
        logger.info("GET Request for fetch the Asset based on account name");
        try {
            logger.info("Account Name: "+accountName);
            AssetValidations.validateAccountName(accountName);
            List<Asset> assetList = assetService.fetchMyAssets(accountName);
            String assetListJson = objectWriter.writeValueAsString(assetList);
            logger.info("Asset List: "+assetListJson);
            return new ResponseEntity<>(assetList,HttpStatus.OK);
        } catch (ApplicationException e) {
            logger.error("Application Exception: "+e.getErrorMessage());
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getErrorMessage(),"403"), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            logger.error("Internal Server Error: ",e);
            return new ResponseEntity<>(new Acknowledgement(StatusCode.Failure.name(),e.getMessage(),"500"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
