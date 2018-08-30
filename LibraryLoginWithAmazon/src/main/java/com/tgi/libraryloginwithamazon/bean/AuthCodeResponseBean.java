package com.tgi.libraryloginwithamazon.bean;

/**
 * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Watch Reference<a/>
 */
public class AuthCodeResponseBean extends ResponseBean {
    private String authCode;
    private String requestState;
    private String redirectUrl;
    private String clientId;
    private String clientSecret;

    public void setAuthCode(String authCode) {
        this.authCode = authCode;
    }

    public void setRequestState(String requestState) {
        this.requestState = requestState;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }

    public String getAuthCode() {
        return authCode;
    }

    public String getRequestState() {
        return requestState;
    }

    @Override
    public String toString() {
        return "AuthCodeResponseBean{" +
                "authCode='" + authCode + '\'' +
                ", requestState='" + requestState + '\'' +
                ", redirectUrl='" + redirectUrl + '\'' +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                '}';
    }
}
