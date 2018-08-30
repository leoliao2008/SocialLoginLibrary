package com.tgi.libraryfacebooklogin.listeners;

public interface FbAuthCodeListener extends BaseListener{
    void onGetSignInWebPage(String htmlSrcCode);
}
