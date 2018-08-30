package com.tgi.libraryfacebooklogin;

import android.app.Activity;
import android.content.Context;

import com.tgi.libraryfacebooklogin.activity.FbWebViewActivity;
import com.tgi.libraryfacebooklogin.listeners.FacebookLoginListener;
import com.tgi.libraryfacebooklogin.models.FbLoginModel;
import com.tgi.libraryfacebooklogin.utils.LibraryFbLoginSpUtil;

public class FbLoginManager {
    private FacebookLoginListener mListener;
    private FbLoginModel mLoginModel;

    public FbLoginManager(FacebookLoginListener listener) {
        mListener = listener;
        mLoginModel=new FbLoginModel();
    }

    public void logIn(Activity context,String clientId){
//        mLoginModel.login(context,clientId,mListener);
        FbWebViewActivity.setFacebookLoginListener(mListener);
        FbWebViewActivity.start(context,clientId);
    }

    public void logOut(String clientId,String accessToken){
        mLoginModel.revokeLogin(clientId,accessToken,mListener);
    }

    public String getExistingAccessToken(Context context){
        return LibraryFbLoginSpUtil.getToken(context);
    }

    public void getFbUser(String accessToken,String appId,String appSecret){
        mLoginModel.inspectAccessToken(
                accessToken,
                appId,
                appSecret,
                mListener
        );
    }


}
