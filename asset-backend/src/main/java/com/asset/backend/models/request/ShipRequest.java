package com.asset.backend.models.request;

public class ShipRequest {

    private String creatorName;

    private String agentName;

    private String shipperAccountName;

    private String origin;

    private String assetId;

    public String getShipperAccountName() {
        return shipperAccountName;
    }

    public void setShipperAccountName(String shipperAccountName) {
        this.shipperAccountName = shipperAccountName;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getAgentName() {
        return agentName;
    }

    public void setAgentName(String agentName) {
        this.agentName = agentName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }
}
