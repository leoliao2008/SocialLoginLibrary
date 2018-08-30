package com.tig.libraysociallogins.listeners;

import com.tig.libraysociallogins.amazon.bean.AmazonAccessToken;
import com.tig.libraysociallogins.amazon.bean.AmazonAuthCode;
import com.tig.libraysociallogins.base.BaseListener;

public class GetPermissionListener implements BaseListener{
    @Override
    public void onError(String msg) {

    }

    public void onGetAmazonAuthCode(AmazonAuthCode amazonAuthCode) {

    }
}
