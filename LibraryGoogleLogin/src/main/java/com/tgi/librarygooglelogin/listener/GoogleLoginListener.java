package com.tgi.librarygooglelogin.listener;

import com.tgi.librarygooglelogin.bean.AuthCodeResponse;
import com.tgi.librarygooglelogin.bean.DiscoveryDoc;
import com.tgi.librarygooglelogin.bean.RefreshAccessToken;
import com.tgi.librarygooglelogin.bean.TokenResponse;
import com.tgi.librarygooglelogin.bean.VerifyIdTokenResponse;

public class GoogleLoginListener {
    public void onError(String msg){

    }

    public void onGetIdTokenVerificationResponse(VerifyIdTokenResponse response){

    }

    public void onGetAccessToken(String string) {

    }

    public void onGetTokens(TokenResponse tokens){

    }

    public void log(String log) {

    }

    public void onGetDiscoveryDoc(DiscoveryDoc discoveryDoc) {

    }

    public void onGetRefreshAccessToken(RefreshAccessToken token) {

    }

    public void onRevokeAccessTokenSuccess() {

    }

    public void onRevokeAccessTokenFail(String msg) {

    }
}
