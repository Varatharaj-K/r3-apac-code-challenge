package com.asset.backend.validations;

import com.asset.backend.exceptions.ApplicationException;
import com.asset.backend.models.request.AssetCreateRequest;
import com.asset.backend.models.request.DeliveryUpdateRequest;
import com.asset.backend.models.request.ShipRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetValidations {

    private static final Logger logger = LoggerFactory.getLogger(AssetValidations.class);

    /**
     * Validating Asset Creation Request
     * Checking Any null values are present in the request
     * @param assetCreateRequest - Asset Creation Request Model
     * */
    public static void validateAssetCreationRequest(AssetCreateRequest assetCreateRequest) throws ApplicationException {
        logger.info("Validating Asset Creation Request");
        if (assetCreateRequest.getOwnerAccountName() == null) {
            logger.error("Owner Account Name is null");
            throw new ApplicationException("Owner Account Name is mandatory");
        }
        if (assetCreateRequest.getAgentAccountName() == null) {
            logger.error("Agent Account name is null");
            throw new ApplicationException("Agent Account name is mandatory");
        }
        if (assetCreateRequest.getOwner() == null) {
            logger.error("Owner value is null");
            throw new ApplicationException("Owner value should not be null");
        }
    }

    /**
     * Validating Shipment Request
     * Checking Any null values are present in the request
     * @param shipRequest - Shipment Update Request model
     * */
    public static void validateShipRequest(ShipRequest shipRequest) throws ApplicationException {
        logger.info("Validating ShipRequest");
        if (shipRequest.getOrigin() == null) {
            logger.error("Origin is null");
            throw new ApplicationException("Origin Should not be null");
        }
        if (shipRequest.getShipperAccountName() == null) {
            logger.error("Shipper Account Name is null");
            throw new ApplicationException("Shipper Account Name should not be null");
        }
    }

    /**
     * Validating Delivery Update Request
     * Checking Any null values are present in the request
     * @param deliveryUpdateRequest - Delivery Update Request model
     * */
    public static void validateDeliveryUpdateRequest(DeliveryUpdateRequest deliveryUpdateRequest) throws ApplicationException {
        logger.info("Validating Delivery Update Request");
        if (deliveryUpdateRequest.getDestination() == null) {
            logger.error("Destination is null");
            throw new ApplicationException("Destination should not be null");
        }
    }

    public static void validateAccountName(String accountName) throws ApplicationException {
        if (accountName == null) {
            logger.error("Account name is null");
            throw new ApplicationException("Account name should not be null");
        }
    }
}
