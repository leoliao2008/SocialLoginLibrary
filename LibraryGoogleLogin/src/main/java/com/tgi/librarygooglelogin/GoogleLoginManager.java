package com.tgi.librarygooglelogin;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.tgi.librarygooglelogin.activity.GoogleLoginWebViewActivity;
import com.tgi.librarygooglelogin.bean.AuthCodeResponse;
import com.tgi.librarygooglelogin.bean.BaseResponse;
import com.tgi.librarygooglelogin.bean.DiscoveryDoc;
import com.tgi.librarygooglelogin.bean.RefreshAccessToken;
import com.tgi.librarygooglelogin.bean.TokenResponse;
import com.tgi.librarygooglelogin.bean.UserCodeResponse;
import com.tgi.librarygooglelogin.bean.VerifyIdTokenResponse;
import com.tgi.librarygooglelogin.listener.GoogleAuthCodeListener;
import com.tgi.librarygooglelogin.listener.GoogleLoginListener;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoogleLoginManager {
    private GoogleLoginModel mLoginModel;
    private GoogleLoginListener mListener;
    private String mState;

    public GoogleLoginManager(GoogleLoginListener listener) {
        mLoginModel = new GoogleLoginModel();
        mListener = listener;
    }

    /**
     * https://developers.google.com/identity/sign-in/devices
     * @param clientId
     */
    public void requestSignIn(
            final String clientId,
            final String clientSecret,
            final Activity context){

        final WeakReference<Activity> ref=new WeakReference<>(context);
        GoogleLoginWebViewActivity.setGoogleLoginListener(mListener);
        mLoginModel.requestUserCodeAndVerifyUrl(clientId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                final UserCodeResponse codeResponse = new Gson().fromJson(json, UserCodeResponse.class);
                if(codeResponse.getError()!=null){
                    mListener.onError(codeResponse.getError()+":"+codeResponse.getError_description());
                }else {
                    if(ref.get()!=null){
                        ref.get().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                GoogleLoginWebViewActivity.start(
                                        1,
                                        ref.get(),
                                        codeResponse,
                                        clientId,
                                        clientSecret);
                            }
                        });
                    }
                }

            }
        });
    }

    public void verifyIdToken(String idToken) {
        mLoginModel.verifyIdToken(idToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                VerifyIdTokenResponse bean = new Gson().fromJson(json, VerifyIdTokenResponse.class);
                mListener.onGetIdTokenVerificationResponse(bean);
            }
        });
    }

    public void getAccessToken(final String auCode, final String clientId, final String clientSecret, final String redirectUrl) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    GoogleTokenResponse response = new GoogleAuthorizationCodeTokenRequest(
                            new NetHttpTransport(),
                            JacksonFactory.getDefaultInstance(),
                            clientId,
                            clientSecret,
                            auCode,
                            redirectUrl
                    ).execute();
                    mListener.onGetAccessToken(response.getAccessToken());
                } catch (IOException e) {
                    e.printStackTrace();
                    mListener.onError(e.getMessage());
                }
            }
        }).start();
    }

    /*the above codes are based on the instruction of google sign in*/
    /*the following codes are based on the fundamental protocol of google auth 2.0 and its guide lines*/


    /**
     * See {@link DiscoveryDoc}
     */
    public void getDiscoveryDoc(){
        mLoginModel.getDiscoveryDoc(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleInvalidResponse(response)){
                    String json = response.body().string();
                    DiscoveryDoc discoveryDoc = new Gson().fromJson(json, DiscoveryDoc.class);
                    mListener.onGetDiscoveryDoc(discoveryDoc);
                }

            }
        });
    }

    /**
     * See {@link GoogleLoginModel#requestUserAuthentication(DiscoveryDoc, String, String, String, boolean, Callback)}
     * @param context
     * @param doc
     * @param clientId
     * @param clientSecret
     * @param redirectUri
     * @param isRequestNewRefreshToken
     */
    public void requestUserAuthentication(
            Activity context,
            final DiscoveryDoc doc,
            final String clientId,
            final String clientSecret,
            final String redirectUri,
            boolean isRequestNewRefreshToken){

        final WeakReference<Activity> ref=new WeakReference<>(context);
        mState = mLoginModel.genAntiForgeryTokenState();
        mLoginModel.requestUserAuthentication(doc, clientId, redirectUri, mState, isRequestNewRefreshToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String webPage = response.body().string();
                GoogleLoginWebViewActivity.setGoogleAuthCodeListener(new GoogleAuthCodeListener() {
                    @Override
                    public void onGetAuthCode(AuthCodeResponse code) {
                        if(!code.getState().equals(mState)){
                            mListener.onError("Invalid State.");
                        }else {
                            mLoginModel.exchangeAuthCodeForTokens(
                                    doc,
                                    code.getAuthCode(),
                                    clientId,
                                    clientSecret,
                                    redirectUri,
                                    new Callback() {
                                        @Override
                                        public void onFailure(Call call, IOException e) {
                                            mListener.onError(e.getMessage());
                                        }

                                        @Override
                                        public void onResponse(Call call, Response response) throws IOException {
                                            if(!checkAndHandleInvalidResponse(response)){
                                                String json = response.body().string();
                                                Log.e("Tokens from Google",json);
                                                TokenResponse tokens = new Gson().fromJson(json, TokenResponse.class);
                                                mListener.onGetTokens(tokens);
                                            }

                                        }
                                    }
                            );
                        }
                    }

                    @Override
                    public void onError(String errorMsg) {
                        mListener.onError(errorMsg);
                    }
                });
                if(ref.get()!=null){
                    GoogleLoginWebViewActivity.start(
                            0,
                            ref.get(),
                            clientId,
                            clientSecret,
                            redirectUri,
                            webPage
                    );
                }

            }
        });
    }


    /**
     * See {@link GoogleLoginModel#refreshAccessToken(DiscoveryDoc, String, String, String, Callback)}
     * @param doc
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     */
    public void refreshAccessToken(DiscoveryDoc doc, final String refreshToken, String clientId, String clientSecret){
        mLoginModel.refreshAccessToken(
                doc,
                refreshToken,
                clientId, clientSecret,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        mListener.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(!checkAndHandleInvalidResponse(response)){
                            String json = response.body().string();
                            RefreshAccessToken token = new Gson().fromJson(json, RefreshAccessToken.class);
                            mListener.onGetRefreshAccessToken(token);
                        }
                    }
                }
        );
    }

    /**
     * See {@link GoogleLoginModel#revokeToken(DiscoveryDoc, String, Callback)}
     * @param doc
     * @param accessToken
     */
    public void revokeAccessToken(DiscoveryDoc doc,String accessToken){
        mLoginModel.revokeToken(doc, accessToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    mListener.onRevokeAccessTokenSuccess();
                }else {
                    BaseResponse baseResponse = new Gson().fromJson(response.body().string(), BaseResponse.class);
                    mListener.onRevokeAccessTokenFail(baseResponse.getError());
                }
            }
        });
    }

    private boolean checkAndHandleInvalidResponse(Response response){
        if(response.code()!=200){
            String message = response.message();
            if(TextUtils.isEmpty(message)){
                message="some thing is wrong with the access token responses.";
                mListener.onError(message);
                return true;
            }
        }
        return false;
    }
}
