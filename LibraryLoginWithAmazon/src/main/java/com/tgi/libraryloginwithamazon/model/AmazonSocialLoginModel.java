package com.tgi.libraryloginwithamazon.model;

import com.tgi.libraryloginwithamazon.bean.AccessTokenResponseBean;
import com.tgi.libraryloginwithamazon.bean.AuthCodeResponseBean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class AmazonSocialLoginModel {

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference<a/>
     * @param clientId
     * @param state
     * @param redirectUri
     */
    public void requestAuthorizeCode(String clientId, String state, String redirectUri, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = builder.add("client_id", clientId)
                .add("scope", "profile postal_code")
                .add("response_type", "code")
                .add("state", state)
                .add("redirect_uri", redirectUri)
                .build();
        Call call = client.newCall(new Request.Builder().url("https://www.amazon.com/ap/oa").post(formBody).build());
        call.enqueue(callback);
    }

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference</a>
     * @param bean
     * @param callback
     */
    public void requestAccessToken(AuthCodeResponseBean bean, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", bean.getAuthCode())
                .add("redirect_uri", bean.getRedirectUrl())
                .add("client_id", bean.getClientId())
                .add("client_secret", bean.getClientSecret())
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("charset", "UTF-8")
                .post(formBody)
                .url("https://api.amazon.com/auth/o2/token")
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference</a>
     * @param token
     * @param callback
     */
    public void refreshAccessToken(AccessTokenResponseBean token, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", token.getRefreshToken())
                .add("client_id",token.getClientId())
                .add("client_secret",token.getClientSecret())
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("charset", "UTF-8")
                .post(formBody)
                .url("https://api.amazon.com/auth/o2/token")
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * https://developer.amazon.com/docs/login-with-amazon/obtain-customer-profile.html
     * @param token
     * @param callback
     */
    public void verifyAccessToke(AccessTokenResponseBean token,Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.amazon.com/auth/o2/tokeninfo?access_token="+token.getAccessToken())
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }

    /**
     * https://developer.amazon.com/docs/login-with-amazon/obtain-customer-profile.html
     * @param token
     * @param callback
     */
    public void requestUserProfile(AccessTokenResponseBean token, Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.amazon.com/user/profile?access_token="+token.getAccessToken())
                .get()
                .build();
        client.newCall(request).enqueue(callback);
    }


}
