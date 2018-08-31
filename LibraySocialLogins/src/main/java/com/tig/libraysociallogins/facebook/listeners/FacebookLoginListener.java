package com.tig.libraysociallogins.facebook.listeners;

import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.facebook.beans.FacebookAccessToken;
import com.tig.libraysociallogins.facebook.beans.FacebookUserProfile;

public class FacebookLoginListener implements BaseListener {
    @Override
    public void onError(String msg) {

    }

    public void onGetAccessToken(FacebookAccessToken accessToken) {

    }

    public void onGetUserProfile(FacebookUserProfile profile) {

    }

    public void onRevokeLoginSuccess() {

    }

    public void onRevokeLoginFailure() {

    }
}
