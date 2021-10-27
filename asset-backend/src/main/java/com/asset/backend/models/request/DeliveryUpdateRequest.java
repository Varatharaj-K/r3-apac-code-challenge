package com.asset.backend.models.request;

public class DeliveryUpdateRequest {

    private String creatorName;

    private String agentName;

    private String shipperAccountName;

    private String destination;

    private String assetId;

    private String transit;

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public String getShipperAccountName() {
        return shipperAccountName;
    }

    public void setShipperAccountName(String shipperAccountName) {
        this.shipperAccountName = shipperAccountName;
    }

    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getTransit() {
        return transit;
    }

    public void setTransit(String transit) {
        this.transit = transit;
    }
}
