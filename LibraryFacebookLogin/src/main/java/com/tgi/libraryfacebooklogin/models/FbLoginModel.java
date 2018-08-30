package com.tgi.libraryfacebooklogin.models;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.tgi.libraryfacebooklogin.LibraryFacebookLoginConstants;
import com.tgi.libraryfacebooklogin.beans.FbAccessTokenResponse;
import com.tgi.libraryfacebooklogin.beans.FbUserBean;
import com.tgi.libraryfacebooklogin.listeners.BaseListener;
import com.tgi.libraryfacebooklogin.listeners.FacebookLoginListener;
import com.tgi.libraryfacebooklogin.listeners.FbAuthCodeListener;
import com.tgi.libraryfacebooklogin.utils.LibraryFbLoginSpUtil;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow
 */
public class FbLoginModel {
    /**
     * Call for a login dialog from Facebook official website and execute permission granting from the user.
     * <a href="https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow">Click here to know more...</a>
     */
    public void login(final String appId, final FbAuthCodeListener listener) {
        final String stateCode = genAntiForgeryTokenState();
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("client_id", appId);
                put("state", stateCode);
                put("redirect_uri", LibraryFacebookLoginConstants.REDIRECT_URL);
                put("scope", "email");
                //we have no choice but to set the "response_type" to "token" and later acquire access token directly from the fragment
                //portion of the redirect url because if we choose to set the "response_type" to "code", we
                //will need to post another https GET request using the returned auth code as param to get the access token,
                //and in that request, the APP secret is required. We shall not use APP secret in client
                //app according to the official document.
                put("response_type", "token");
            }
        };
        executeRequest(
                "https://www.facebook.com/v3.1/dialog/oauth",
                null,
                params,
                RequestMethod.GET,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listener.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!checkAndHandleInvalidResponse(response, listener)) {
                            String htmlSrcCode = response.body().string();
                            listener.onGetSignInWebPage(htmlSrcCode);
                        }
                    }
                }
        );
    }

    /**
     * This function is not advised because it involves the use of APP secret.
     * APP secret shall only be used on server side according to official document.
     * <a href="https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow">Click to learn more...<a/>
     *
     * @param authCode
     * @param appId
     * @param appSecret
     * @param listener
     */
    public void exchangeAuthCodeForAccessToken(
            final String authCode,
            final String appId,
            final String appSecret,
            final FacebookLoginListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("client_id", appId);
                put("client_secret", appSecret);
                put("redirect_uri", LibraryFacebookLoginConstants.REDIRECT_URL);
                put("code", authCode);
            }
        };
        executeRequest(
                "https://graph.facebook.com/v3.1/oauth/access_token",
                null,
                params,
                RequestMethod.GET,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listener.onError(e.getMessage());

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!checkAndHandleInvalidResponse(response, listener)) {
                            String json = response.body().string();
                            FbAccessTokenResponse token = new Gson().fromJson(json, FbAccessTokenResponse.class);
                            listener.onGetAccessToken(token);

                        }

                    }
                }
        );

    }

    /**
     * This function is not advised because it involves the use of APP secret.
     * APP secret shall only be used on server side according to official document.
     * <a href="https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow">Click to learn more...<a/>
     */
    public void inspectAccessToken(
            final String accessToken,
            final String appId,
            final String appSecret,
            final FacebookLoginListener listener
    ) {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("input_token", accessToken);
                put("access_token", appId + "|" + appSecret);
            }
        };
        executeRequest(
                "https://graph.facebook.com/debug_token",
                null,
                params,
                RequestMethod.GET,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listener.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (!checkAndHandleInvalidResponse(response, listener)) {
                            String json = response.body().string();
                            FbUserBean userBean = new Gson().fromJson(json, FbUserBean.class);
                            listener.onGetFbUser(userBean);
                        }
                    }
                }
        );

    }

    /**
     * This function will revoke the user permissions on the app. After this, the app will need
     * to gain the permission of the user as if the user login for the first time.
     * <a href="https://developers.facebook.com/docs/graph-api/using-graph-api/common-scenario">See here...</a>
     * <a href="https://developers.facebook.com/docs/facebook-login/permissions/requesting-and-revoking#revokelogin">And here...</a>
     * @param userId The userId of the current user. It can be acquired through {@link FbLoginModel#inspectAccessToken(String, String, String, FacebookLoginListener)}
     * @param accessToken The access token of the current user.
     */
    public void revokeLogin(String userId, final String accessToken, final FacebookLoginListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("access_token", accessToken);
            }
        };
        executeRequest(
                "https://graph.facebook.com/" + userId + "/permissions",
                null,
                params,
                RequestMethod.DELETE,
                new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        listener.onError(e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        listener.onUserLogout(response.body().string());
                    }
                });
    }











    private boolean checkAndHandleInvalidResponse(Response response, BaseListener listener) {
        if (response.code() != 200) {
            String msg = response.message();
            if (TextUtils.isEmpty(msg)) {
                msg = "Unknown Error from Facebook Server.";
                listener.onError(msg);
            }
            return true;
        }
        return false;
    }


    private String genAntiForgeryTokenState() {
        return new BigInteger(130, new SecureRandom()).toString(32);
    }


    private String genGetRequest(String url, Map<String, String> params) {
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
        if (!params.isEmpty()) {
            sb.append('?');
        }
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            sb.append(entry.getKey())
                    .append('=')
                    .append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append('&');
            }
        }
        return sb.toString();
    }

    private void executeRequest(
            String url,
            @Nullable Map<String, String> headers,
            @Nullable Map<String, String> params,
            RequestMethod method,
            Callback callback) {

        Request.Builder builder = new Request.Builder();
        if (method == RequestMethod.GET) {
            if (params != null && params.size() > 0) {
                url = genGetRequest(url, params);
            }
            builder = builder.get();
        } else if (method == RequestMethod.POST || method == RequestMethod.DELETE) {
            if (params != null && params.size() > 0) {
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    formBodyBuilder.add(entry.getKey(), entry.getValue());
                }
                FormBody formBody = formBodyBuilder.build();
                if (method == RequestMethod.POST) {
                    builder.post(formBody);
                } else if (method == RequestMethod.DELETE) {
                    builder.delete(formBody);
                }

            }
        }
        if (builder != null && headers != null && headers.size() > 0) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        if (builder != null) {
            new OkHttpClient().newCall(builder.url(url).build()).enqueue(callback);
        }
    }

    private enum RequestMethod {
        GET, POST, DELETE
    }
}
