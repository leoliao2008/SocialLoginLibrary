package com.tig.libraysociallogins.listeners;

import com.tig.libraysociallogins.amazon.bean.AmazonAccessToken;
import com.tig.libraysociallogins.amazon.bean.AmazonAuthCode;
import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.facebook.beans.FacebookAccessToken;
import com.tig.libraysociallogins.google.beans.GoogleAuthCode;

public class SocialLoginListener implements BaseListener{
    @Override
    public void onError(String msg) {

    }

    public void onGetAmazonAuthCode(AmazonAuthCode amazonAuthCode) {

    }

    public void onGetGoogleAuthCode(GoogleAuthCode googleAuthCode) {

    }

    public void onGetFacebookAccessToken(FacebookAccessToken accessToken) {

    }
}
