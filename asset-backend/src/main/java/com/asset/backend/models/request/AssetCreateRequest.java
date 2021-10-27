package com.asset.backend.models.request;

public class AssetCreateRequest {

   private String ownerAccountName;

   private String agentAccountName;

   private String owner;

    public String getOwnerAccountName() {
        return ownerAccountName;
    }

    public void setOwnerAccountName(String ownerAccountName) {
        this.ownerAccountName = ownerAccountName;
    }

    public String getAgentAccountName() {
        return agentAccountName;
    }

    public void setAgentAccountName(String agentAccountName) {
        this.agentAccountName = agentAccountName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }
}
