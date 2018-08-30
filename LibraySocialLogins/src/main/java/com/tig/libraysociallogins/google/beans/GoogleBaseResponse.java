package com.tig.libraysociallogins.google.beans;

public class GoogleBaseResponse {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }


    @Override
    public String toString() {
        return "BaseResponse{" +
                "error='" + error + '\'' +
                '}';
    }
}
