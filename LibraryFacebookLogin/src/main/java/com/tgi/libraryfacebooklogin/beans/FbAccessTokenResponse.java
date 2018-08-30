package com.tgi.libraryfacebooklogin.beans;

/**
 * https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow
 */
public class FbAccessTokenResponse {

   private String mAccessToken;
   private String mState;
   private long mExpiresIn;
   private String mError;
   private String mErrorReason;
   private String mErrorDescription;
   private boolean mIsError;

    public String getAccessToken() {
        return mAccessToken;
    }

    public void setAccessToken(String accessToken) {
        mAccessToken = accessToken;
    }

    public String getState() {
        return mState;
    }

    public void setState(String state) {
        mState = state;
    }

    public long getExpiresIn() {
        return mExpiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        mExpiresIn = expiresIn;
    }

    public String getError() {
        return mError;
    }

    public void setError(String error) {
        mError = error;
    }

    public String getErrorReason() {
        return mErrorReason;
    }

    public void setErrorReason(String errorReason) {
        mErrorReason = errorReason;
    }

    public String getErrorDescription() {
        return mErrorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        mErrorDescription = errorDescription;
    }

    public boolean isError() {
        return mIsError;
    }

    public void setIsError(boolean error) {
        mIsError = error;
    }

    @Override
    public String toString() {
        return "FbAccessTokenResponse{" +
                "mAccessToken='" + mAccessToken + '\'' +
                ", mState='" + mState + '\'' +
                ", mExpiresIn=" + mExpiresIn +
                ", mError='" + mError + '\'' +
                ", mErrorReason='" + mErrorReason + '\'' +
                ", mErrorDescription='" + mErrorDescription + '\'' +
                ", mIsError=" + mIsError +
                '}';
    }
}
