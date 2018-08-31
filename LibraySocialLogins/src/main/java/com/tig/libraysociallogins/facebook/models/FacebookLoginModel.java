package com.tig.libraysociallogins.facebook.models;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tig.libraysociallogins.base.BaseLoginModel;
import com.tig.libraysociallogins.facebook.beans.FacebookAccessToken;
import com.tig.libraysociallogins.facebook.beans.FacebookUserProfile;
import com.tig.libraysociallogins.facebook.listeners.FacebookLoginListener;
import com.tig.libraysociallogins.listeners.LoadSocialLoginFrontPageListener;

import java.io.IOException;
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
public class FacebookLoginModel extends BaseLoginModel {
    /**
     * Call for a login dialog from Facebook official website and execute permission granting from the user.
     * <a href="https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow">Click here to know more...</a>
     */
    public void login(final String appId, final String stateCode, final String redirectUri, final LoadSocialLoginFrontPageListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("client_id", appId);
                put("state", stateCode);
                put("redirect_uri", redirectUri);
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
                        if (!checkAndHandleResponseError(response, listener)) {
                            String htmlSrcCode = response.body().string();
                            listener.onGetLoginPageSrcCode(htmlSrcCode);
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
            final String redirectUri,
            final FacebookLoginListener listener) {
        HashMap<String, String> params = new HashMap<String, String>() {
            {
                put("client_id", appId);
                put("client_secret", appSecret);
                put("redirect_uri", redirectUri);
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
                        if (!checkAndHandleResponseError(response, listener)) {
                            String json = response.body().string();
                            FacebookAccessToken token = new Gson().fromJson(json, FacebookAccessToken.class);
                            String error = token.getError();
                            if(TextUtils.isEmpty(error)){
                                listener.onGetAccessToken(token);
                            }else {
                                listener.onError(error);
                            }
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
        final HashMap<String, String> params = new HashMap<String, String>() {
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
                        if (!checkAndHandleResponseError(response, listener)) {
                            String json = response.body().string();
                            FacebookUserProfile profile = new Gson().fromJson(json, FacebookUserProfile.class);
                            String errorMsg = profile.getError().getError_user_msg();
                            if(TextUtils.isEmpty(errorMsg)){
                                listener.onGetUserProfile(profile);
                            }else {
                                listener.onError(errorMsg);
                            }

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
     * @param userId The userId of the current user. It can be acquired through {@link FacebookLoginModel#inspectAccessToken(String, String, String, FacebookLoginListener)}
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
                        int code = response.code();
                        if(code==200){
                            listener.onRevokeLoginSuccess();
                        }else {
                            listener.onRevokeLoginFailure();
                        }
                    }
                });
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
