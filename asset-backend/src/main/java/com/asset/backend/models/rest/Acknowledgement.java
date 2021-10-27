package com.asset.backend.models.rest;

public class Acknowledgement {

    private String status;

    private String description;

    private String statusCode;

    public Acknowledgement() {
    }

    public Acknowledgement(String status, String description, String statusCode) {
        this.status = status;
        this.description = description;
        this.statusCode = statusCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }
}
