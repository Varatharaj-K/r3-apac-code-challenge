package com.asset.backend.mappers;

import com.asset.backend.models.rest.Asset;
import com.asset.backend.utils.FlowStatus;
import com.asset.states.AssetState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AssetMappers {

    private static final Logger logger = LoggerFactory.getLogger(AssetMappers.class);
    /**
     * Mapping Asset Corda state to Asset Rest model
     * */
    public static Asset mapAssetStateToAssetRestModel(AssetState assetState) {
        logger.info("Mapping Asset state to rest");
        Asset asset = new Asset();
        asset.setAssetId(assetState.getLinearId().getId().toString());
        if (assetState.getCreatorAccountName() != null) {
            asset.setCreatorName(assetState.getCreatorAccountName());
        }
        if (assetState.getAgentAccountName() != null) {
            asset.setAgentAccountName(assetState.getAgentAccountName());
        }
        if (assetState.getShipperAccountName() != null) {
            asset.setShipperAccountName(assetState.getShipperAccountName());
        }
        if (assetState.getOwner() != null) {
            asset.setOwner(assetState.getOwner());
        }
        if (assetState.getDestination() != null) {
            asset.setDestination(assetState.getDestination());
        }
        if (assetState.getOrigin() != null) {
            asset.setOrigin(assetState.getOrigin());
        }
        if (assetState.getTransit() != null) {
            asset.setTransit(assetState.getTransit());
        }
        if (assetState.getStatus() != null) {
            asset.setStatus(FlowStatus.valueOf(assetState.getStatus()));
        }
        logger.info("Asset state mapped successfully!");
        return asset;
    }
}
