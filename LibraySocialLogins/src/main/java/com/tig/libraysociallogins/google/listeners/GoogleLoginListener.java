package com.tig.libraysociallogins.google.listeners;

import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.google.beans.GoogleRefreshedAccessToken;
import com.tig.libraysociallogins.google.beans.GoogleTokens;
import com.tig.libraysociallogins.google.beans.GoogleUserProfile;

public class GoogleLoginListener implements BaseListener {
    @Override
    public void onError(String msg) {

    }

    public void onGetGoogleTokens(GoogleTokens tokens) {

    }

    public void onGetGoogleNewlyRefreshedAccessToken(GoogleRefreshedAccessToken token) {

    }

    public void onGetUserProfile(GoogleUserProfile userProfile) {

    }

    public void onRevokeAccessTokenSuccess() {

    }

    public void onRevokeAccessTokenFail(String msg) {

    }
}
