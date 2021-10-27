package com.asset.backend.exceptions;

public class ApplicationException extends Exception{

    private String errorMessage;

    public ApplicationException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
