package com.tig.libraysociallogins.listeners;

import com.tig.libraysociallogins.base.BaseListener;


public interface LoadSocialLoginFrontPageListener extends BaseListener{

    void onGetLoginPageSrcCode(String htmlSrcCode);
}
