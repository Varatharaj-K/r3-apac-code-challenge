package com.asset.backend.service;

import com.asset.backend.exceptions.ApplicationException;
import com.asset.backend.mappers.AssetMappers;
import com.asset.backend.models.request.AssetCreateRequest;
import com.asset.backend.models.request.DeliveryUpdateRequest;
import com.asset.backend.models.request.ShipRequest;
import com.asset.backend.models.rest.Asset;
import com.asset.flows.AssetCreateFlow;
import com.asset.flows.DeliverAssetFlow;
import com.asset.flows.ShipAssetFlow;
import com.asset.flows.ViewMyAssets;
import com.asset.states.AssetState;
import net.corda.core.contracts.UniqueIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Service
public class AssetService {

    private static final Logger logger = LoggerFactory.getLogger(AssetService.class);

    @Autowired
    NodeRpcConnection nodeRpcConnection;

    /**
     * Processing Asset Creation Request
     * */
    public Asset processAssetCreateRequest(AssetCreateRequest assetCreateRequest) throws Exception{
        logger.info("Processing Asset Creation Request");
        AssetState assetState = new AssetState(
                null,
                null,
                null,
                assetCreateRequest.getOwnerAccountName(),
                assetCreateRequest.getAgentAccountName(),
                null,
                assetCreateRequest.getOwner(),
                null,
                null,
                null,
                null,
                new UniqueIdentifier(UUID.randomUUID().toString(), UUID.randomUUID())
        );
        try {
            AssetState createdAsset = invokeAssetCreateFlow(assetState);
            logger.info("Asset Creation success");
            return AssetMappers.mapAssetStateToAssetRestModel(createdAsset);
        } catch (Exception e) {
            logger.error("Asset Creation Failed ",e);
            throw new ApplicationException("Asset creation Failed");
        }

    }

    /**
     * Processing Asset Ship Request
     * */
    public Asset processShipAssetRequest(ShipRequest shipRequest) throws ApplicationException {
        logger.info("Processing Ship Asset Request");
        try {
            AssetState assetState = invokeShipAssetFlow(shipRequest);
            logger.info("Ship Asset success");
            return AssetMappers.mapAssetStateToAssetRestModel(assetState);
        } catch (Exception e) {
            logger.error("Ship asset failed: ",e);
            throw new ApplicationException("Ship Asset failed");
        }
    }

    /**
     * Processing Delivery update Request
     * */
    public Asset processDeliveryUpdateRequest(DeliveryUpdateRequest deliveryUpdateRequest) throws ApplicationException {
        logger.info("Processing delivery Asset Request");
        try {
            AssetState assetState = invokeDeliveryUpdateFlow(deliveryUpdateRequest);
            logger.info("Delivery Asset success");
            return AssetMappers.mapAssetStateToAssetRestModel(assetState);
        } catch (Exception e) {
            logger.error("Delivery asset failed: ",e);
            throw new ApplicationException("Delivery Asset failed");
        }
    }

    /**
     * Fetching All Assets Based on account name
     * */
    public List<Asset> fetchMyAssets(String accountName) throws ApplicationException {
        logger.info("Processing Fetch Assets Request");
        List<Asset> assetList = new ArrayList<>();
        try {
            List<AssetState> assetStateList = invokeFetchMyAssetsFlow(accountName);
            logger.info("Delivery Asset success");
            if (!assetStateList.isEmpty()) {
                for (AssetState assetState: assetStateList) {
                    assetList.add(AssetMappers.mapAssetStateToAssetRestModel(assetState));
                }
            }
            return assetList;
        } catch (Exception e) {
            logger.error("Delivery asset failed: ",e);
            throw new ApplicationException("Delivery Asset failed");
        }
    }

    /**
     * Invoking Asset create flow
    * */
    public AssetState invokeAssetCreateFlow(AssetState assetState) throws ExecutionException, InterruptedException {
        logger.info("Invoking Asset create flow");
        AssetState createdAsset = nodeRpcConnection.getProxy().startFlowDynamic(
                AssetCreateFlow.Initiator.class,
                assetState
        ).getReturnValue().get();
        logger.info("Asset create flow invoked");
        return createdAsset;
    }

    /**
     * Invoking ShipAsset Flow
     * */
    private AssetState invokeShipAssetFlow(ShipRequest shipRequest) throws ExecutionException, InterruptedException {
        logger.info("Invoking Ship Asset Flow");
        AssetState updatedAsset = nodeRpcConnection.getProxy().startFlowDynamic(
                ShipAssetFlow.Initiator.class,
                shipRequest.getCreatorName(), shipRequest.getAgentName(),shipRequest.getShipperAccountName(), shipRequest.getOrigin(), shipRequest.getAssetId()

        ).getReturnValue().get();
        logger.info("Ship Asset flow invoked");
        return updatedAsset;
    }

    /**
     * Invoking Deliver Asset Flow
     * */
    private AssetState invokeDeliveryUpdateFlow(DeliveryUpdateRequest deliveryUpdateRequest) throws ExecutionException, InterruptedException {
        logger.info("Invoking Delivery Asset Flow");
        AssetState updatedAsset = nodeRpcConnection.getProxy().startFlowDynamic(
                DeliverAssetFlow.Initiator.class,
                deliveryUpdateRequest.getCreatorName(), deliveryUpdateRequest.getAgentName(),deliveryUpdateRequest.getShipperAccountName(), deliveryUpdateRequest.getDestination(),
                deliveryUpdateRequest.getTransit(), deliveryUpdateRequest.getAssetId()

        ).getReturnValue().get();
        logger.info("Ship Asset flow invoked");
        return updatedAsset;

    }

    /**
     * Invoking ViewMy asset flow
     * */
    private List<AssetState> invokeFetchMyAssetsFlow(String accountName) throws ExecutionException, InterruptedException {
        logger.info("Invoking Fetch my asset Flow");
        List<?> assetStateList;
        assetStateList = nodeRpcConnection.getProxy().startFlowDynamic(
                ViewMyAssets.class,accountName

        ).getReturnValue().get();
        if (!assetStateList.isEmpty()) {
            List<AssetState> assets = (List<AssetState>) assetStateList;
            return assets;
        }
        logger.info("Ship Asset flow invoked");
        return new ArrayList<>();
    }
}
