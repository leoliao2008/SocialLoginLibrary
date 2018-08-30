package com.tig.libraysociallogins.base;

import android.text.TextUtils;

import okhttp3.Response;

public class BaseLoginModel {
    public boolean checkAndHandleResponseError(Response response, BaseListener listener){
        if(response.code()!=200){
            String msg = response.message();
            if(TextUtils.isEmpty(msg)){
                msg="Unknown Error: response code ="+response.code();
            }
            listener.onError(msg);
            return true;
        }
        return false;
    }
}
