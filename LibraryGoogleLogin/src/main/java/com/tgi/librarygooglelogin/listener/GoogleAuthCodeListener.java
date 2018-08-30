package com.tgi.librarygooglelogin.listener;

import com.tgi.librarygooglelogin.bean.AuthCodeResponse;

public interface GoogleAuthCodeListener {
    void onGetAuthCode(AuthCodeResponse code);

    void onError(String errorMsg);
}
