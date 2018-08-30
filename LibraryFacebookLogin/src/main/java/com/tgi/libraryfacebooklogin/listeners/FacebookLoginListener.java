package com.tgi.libraryfacebooklogin.listeners;

import com.tgi.libraryfacebooklogin.beans.FbAccessTokenResponse;
import com.tgi.libraryfacebooklogin.beans.FbUserBean;

public class FacebookLoginListener implements BaseListener{

    public void onGetAccessToken(FbAccessTokenResponse token) {

    }

    public void onGetFbUser(FbUserBean userBean) {

    }

    @Override
    public void onError(String msg) {

    }

    public void onLog(String msg) {

    }

    public void onUserLogout(String string) {

    }
}
