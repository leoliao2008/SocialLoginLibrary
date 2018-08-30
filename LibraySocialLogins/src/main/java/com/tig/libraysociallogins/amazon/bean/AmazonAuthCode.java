package com.tig.libraysociallogins.amazon.bean;

public class AmazonAuthCode extends AmazonBaseResponse {
    private String mAuthCode;
    private String mState;

    public String getAuthCode() {
        return mAuthCode;
    }

    public void setAuthCode(String authCode) {
        mAuthCode = authCode;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    @Override
    public String toString() {
        return "AmazonAuthCode{" +
                "mAuthCode='" + mAuthCode + '\'' +
                ", mState='" + mState + '\'' +
                "} " + super.toString();
    }
}
