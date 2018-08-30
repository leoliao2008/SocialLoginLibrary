package com.tig.libraysociallogins.google.manager;

import android.content.Intent;
import android.graphics.PaintFlagsDrawFilter;

import com.google.gson.Gson;
import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.google.beans.GoogleDiscoveryDoc;
import com.tig.libraysociallogins.google.listeners.GoogleLoginListener;
import com.tig.libraysociallogins.google.models.GoogleLoginModel;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoogleLoginManager extends BaseLoginManager {
    private GoogleLoginListener mListener;
    private GoogleLoginModel mModel;
    private GoogleDiscoveryDoc mDiscoveryDoc;
    private String mState;

    public GoogleLoginManager(GoogleLoginListener listener) {
        mListener = listener;
        mModel=new GoogleLoginModel();
        mModel.getDiscoveryDoc(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                boolean isError = mModel.checkAndHandleResponseError(response, new BaseListener() {
                    @Override
                    public void onError(String msg) {
                        mListener.onError(msg);
                    }
                });
                if(!isError){
                    String json = response.body().string();
                    GoogleDiscoveryDoc discoveryDoc = new Gson().fromJson(json, GoogleDiscoveryDoc.class);
                    if(discoveryDoc.getError()==null){
                        mDiscoveryDoc=discoveryDoc;
                    }
                }

            }
        });
    }

    public void getAccessToken(String clientId,String redirectUri){
        mState=genAntiForgeryTokenState();
        mModel.requestUserAuthentication(
                mDiscoveryDoc,
                clientId,
                redirectUri,
                mState,
                true,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                    }
                }
        );

    }
}
