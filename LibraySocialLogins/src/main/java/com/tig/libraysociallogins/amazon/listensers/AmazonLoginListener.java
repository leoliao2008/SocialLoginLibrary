package com.tig.libraysociallogins.amazon.listensers;

import com.tig.libraysociallogins.amazon.bean.AmazonAccessToken;
import com.tig.libraysociallogins.amazon.bean.AmazonUserProfile;
import com.tig.libraysociallogins.base.BaseListener;

public class AmazonLoginListener implements BaseListener {
    @Override
    public void onError(String msg) {

    }

    public void onGetAmazonAccessToken(AmazonAccessToken token) {

    }

    public void onGetAmazonNewlyRefreshToken(AmazonAccessToken token) {

    }

    public void onGetUserProfile(AmazonUserProfile profile) {

    }
}
