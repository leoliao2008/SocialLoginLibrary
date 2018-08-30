package com.tig.libraysociallogins.amazon.listensers;

import com.tig.libraysociallogins.base.BaseListener;

public interface CheckAmazonAccessTokenValidListener extends BaseListener {

    void onTokenNotValid(String msg);

    void onTokenValid();
}
