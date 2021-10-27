package com.asset.backend.models.rest;

import com.asset.backend.utils.FlowStatus;

public class Asset {

    private String assetId;

    private String creatorName;

    private String agentAccountName;

    private String shipperAccountName;

    private String destination;

    private String owner;

    private String origin;

    private String transit;

    private FlowStatus status;


    public String getAssetId() {
        return assetId;
    }

    public void setAssetId(String assetId) {
        this.assetId = assetId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getAgentAccountName() {
        return agentAccountName;
    }

    public void setAgentAccountName(String agentAccountName) {
        this.agentAccountName = agentAccountName;
    }

    public String getShipperAccountName() {
        return shipperAccountName;
    }

    public void setShipperAccountName(String shipperAccountName) {
        this.shipperAccountName = shipperAccountName;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public FlowStatus getStatus() {
        return status;
    }

    public void setStatus(FlowStatus status) {
        this.status = status;
    }

    public String getTransit() {
        return transit;
    }

    public void setTransit(String transit) {
        this.transit = transit;
    }
}
