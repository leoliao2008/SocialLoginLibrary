package com.tig.libraysociallogins.amazon.models;


import com.google.gson.Gson;
import com.tig.libraysociallogins.amazon.bean.AmazonAccessToken;
import com.tig.libraysociallogins.amazon.bean.AmazonUserProfile;
import com.tig.libraysociallogins.amazon.listensers.AmazonLoginListener;
import com.tig.libraysociallogins.amazon.listensers.CheckAmazonAccessTokenValidListener;
import com.tig.libraysociallogins.base.BaseLoginModel;
import com.tig.libraysociallogins.listeners.LoadSocialLoginFrontPageListener;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AmazonLoginModel extends BaseLoginModel {

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference<a/>
     *
     * @param clientId
     * @param state
     * @param redirectUri
     */
    public void userSignIn(
            String clientId,
            String state,
            final String redirectUri,
            final LoadSocialLoginFrontPageListener listener) {

        OkHttpClient client = new OkHttpClient();
        FormBody.Builder builder = new FormBody.Builder();
        FormBody formBody = builder.add("client_id", clientId)
                .add("scope", "profile postal_code")
                .add("response_type", "code")
                .add("state", state)
                .add("redirect_uri", redirectUri)
                .build();
        Call call = client.newCall(new Request.Builder().url("https://www.amazon.com/ap/oa").post(formBody).build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!checkAndHandleResponseError(response, listener)) {
                    listener.onGetLoginPageSrcCode(response.body().string());
                }
            }
        });
    }

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference</a>
     */
    public void requestAccessToken(
            String authCode,
            String redirectUri,
            String clientId,
            String clientSecret,
            final AmazonLoginListener listener) {

        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "authorization_code")
                .add("code", authCode)
                .add("redirect_uri", redirectUri)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("charset", "UTF-8")
                .post(formBody)
                .url("https://api.amazon.com/auth/o2/token")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    String json = response.body().string();
                    AmazonAccessToken token = new Gson().fromJson(json, AmazonAccessToken.class);
                    listener.onGetAmazonAccessToken(token);
                }

            }
        });
    }

    /**
     * <a href="https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html">Official Reference</a>
     *
     */
    public void refreshAccessToken(
            String refreshToken,
            String clientId,
            String clientSecret,
            final AmazonLoginListener listener) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("grant_type", "refresh_token")
                .add("refresh_token", refreshToken)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .build();
        Request request = new Request.Builder()
                .header("Content-Type", "application/x-www-form-urlencoded")
                .header("charset", "UTF-8")
                .post(formBody)
                .url("https://api.amazon.com/auth/o2/token")
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    String json = response.body().string();
                    AmazonAccessToken token = new Gson().fromJson(json, AmazonAccessToken.class);
                    listener.onGetAmazonNewlyRefreshToken(token);
                }
            }
        });
    }

    /**
     * https://developer.amazon.com/docs/login-with-amazon/obtain-customer-profile.html
     */
    public void checkIfAccessTokeValid(String accessToken, final String clientId, final CheckAmazonAccessTokenValidListener listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.amazon.com/auth/o2/tokeninfo?access_token=" + accessToken)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    if(checkIfTheSameClientId(response.body().string(),clientId)){
                        listener.onTokenValid();
                    }else {
                        listener.onTokenNotValid("Client Id does not fit.The response may come from a malicious source!");
                    }
                }
            }
        });
    }



    /**
     * https://developer.amazon.com/docs/login-with-amazon/obtain-customer-profile.html
     */
    public void requestUserProfile(String accessToken, final AmazonLoginListener listener) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.amazon.com/user/profile?access_token=" + accessToken)
                .get()
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    String json = response.body().string();
                    AmazonUserProfile profile = new Gson().fromJson(json, AmazonUserProfile.class);
                    listener.onGetUserProfile(profile);
                }

            }
        });
    }

    private boolean checkIfTheSameClientId(String responseBody, String clientId) {
        responseBody = responseBody.replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .trim();
        String[] split = responseBody.split(",");
        for (String temp : split) {
            String[] tempSplit = temp.split(":");
            if (tempSplit[0].trim().equals("aud")) {
                return tempSplit[1].trim().equals(clientId);
            }
        }
        return false;
    }


}
