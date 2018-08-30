package com.tgi.librarygooglelogin.bean;

public class AuthCodeResponse extends BaseResponse {
    private String authCode;
    private String state;

    public String getAuthCode() {
        return authCode;
    }

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "AuthCodeResponse{" +
                "authCode='" + authCode + '\'' +
                "} " + super.toString();
    }
}
