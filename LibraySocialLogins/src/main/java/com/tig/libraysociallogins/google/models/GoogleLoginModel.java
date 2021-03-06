package com.tig.libraysociallogins.google.models;




import android.text.TextUtils;

import com.google.gson.Gson;
import com.tig.libraysociallogins.base.BaseLoginModel;
import com.tig.libraysociallogins.google.beans.GoogleBaseResponse;
import com.tig.libraysociallogins.google.beans.GoogleDiscoveryDoc;
import com.tig.libraysociallogins.google.beans.GoogleRefreshedAccessToken;
import com.tig.libraysociallogins.google.beans.GoogleTokens;
import com.tig.libraysociallogins.google.beans.GoogleUserProfile;
import com.tig.libraysociallogins.google.listeners.GoogleLoginListener;
import com.tig.libraysociallogins.listeners.LoadSocialLoginFrontPageListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoogleLoginModel extends BaseLoginModel {


    /**
     * Get a json object that contains the latest end points for various http REST request.
     * <a href="https://developers.google.com/identity/protocols/OpenIDConnect#discovery">Click to know more...</a>
     *
     * @param callback
     */
    public void getDiscoveryDoc(Callback callback) {
        Request request = new Request.Builder()
                .url("https://accounts.google.com/.well-known/openid-configuration")
                .get()
                .build();
        executeRequest(request, callback);
    }


    /**
     * Generate params and apply for a authenticate session from Google Auth Service.
     * The client will be redirected to Google official login web page where the user
     * is prompted to enter user name and pw and give consent to our app to access the
     * requested user data.If the user permits the access, a auth code will be returned
     * with the redirect uri, if the user refuses to grant access, an error code will be returned.
     * <a href="https://developers.google.com/identity/protocols/OpenIDConnect">Click to know more...</a>
     *
     * @param doc
     * @param clientId
     * @param redirectUri
     * @param tokenState
     * @param isRequestNewRefreshToken
     */
    public void requestUserAuthentication(
            GoogleDiscoveryDoc doc,
            final String clientId,
            final String redirectUri,
            final String tokenState,
            final boolean isRequestNewRefreshToken,
            final LoadSocialLoginFrontPageListener listener) {

        HashMap<String, String> requestBody = new HashMap<String, String>() {
            {
                put("client_id", clientId);
                put("response_type", "code");
                put("scope", "openid email profile");
                put("redirect_uri", redirectUri);
                put("state", tokenState);
                put("nonce", String.valueOf(System.currentTimeMillis()));
                put("display", "popup");
                if (isRequestNewRefreshToken) {
                    put("access_type", "offline");
                }
                put("include_granted_scopes", "true");
            }
        };
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getAuthorization_endpoint())
                .append("?");
        Iterator<Map.Entry<String, String>> iterator = requestBody.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, String> next = iterator.next();
            sb.append(next.getKey())
                    .append("=")
                    .append(next.getValue());
            if (iterator.hasNext()) {
                sb.append("&");
            }
        }
        //the request method should be GET
        Request request = new Request.Builder()
                .url(sb.toString())
                .get()
                .build();
        executeRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response, listener)){
                    listener.onGetLoginPageSrcCode(response.body().string());
                }

            }
        });
    }

    /**
     * Use the authorization code returned by the consent of users(See:{@link GoogleLoginModel#requestUserAuthentication(GoogleDiscoveryDoc, String, String, String, boolean, LoadSocialLoginFrontPageListener)})
     * to exchange for access token and id token.
     * <a href="https://developers.google.com/identity/protocols/OpenIDConnect">Click to know more...</a>
     *
     * @param doc
     * @param authCode
     * @param clientId
     * @param clientSecret
     * @param redirectUri
     */
    public void exchangeAuthCodeForTokens(
            GoogleDiscoveryDoc doc,
            String authCode,
            String clientId,
            String clientSecret,
            String redirectUri,
            final GoogleLoginListener listener) {
        FormBody formBody = new FormBody.Builder()
                .add("code", authCode)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("redirect_uri", redirectUri)
                .add("grant_type", "authorization_code")
                .build();
        Request request = new Request.Builder()
                .url(doc.getToken_endpoint())
                .post(formBody)
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        executeRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    GoogleTokens tokens = new Gson().fromJson(response.body().string(), GoogleTokens.class);
                    String error = tokens.getError();
                    if(TextUtils.isEmpty(error)){
                        listener.onGetGoogleTokens(tokens);
                    }else {
                        listener.onError(error);
                    }

                }

            }
        });
    }

    /**
     * Access tokens periodically expire. You can refresh an access token without prompting the user
     * for permission (including when the user is not present) if you requested offline access to the
     * scopes associated with the token.
     * This function enable the app to acquire a new access token with a valid refresh token.
     * <a href="https://developers.google.com/identity/protocols/OAuth2WebServer#offline">Click here to know more...</a>
     * @param doc
     * @param refreshToken
     * @param clientId
     * @param clientSecret
     */
    public void refreshAccessToken(
            GoogleDiscoveryDoc doc,
            String refreshToken,
            String clientId,
            String clientSecret,
            final GoogleLoginListener listener) {
        FormBody formBody = new FormBody.Builder()
                .add("refresh_token", refreshToken)
                .add("client_id", clientId)
                .add("client_secret", clientSecret)
                .add("grant_type", "refresh_token")
                .build();
        Request request = new Request.Builder()
                .url(doc.getToken_endpoint())
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .post(formBody)
                .build();
        executeRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    String json = response.body().string();
                    GoogleRefreshedAccessToken token = new Gson().fromJson(json, GoogleRefreshedAccessToken.class);
                    String error = token.getError();
                    if(TextUtils.isEmpty(error)){
                        listener.onGetGoogleNewlyRefreshedAccessToken(token);
                    }else {
                        listener.onError(error);
                    }
                }

            }
        });
    }

    /**
     * In some cases a user may wish to revoke access given to an application.
     * A user can revoke access by visiting Account Settings. It is also possible
     * for an application to programmatically revoke the access given to it. Programmatic
     * revocation is important in instances where a user unsubscribes or removes an application.
     * In other words, part of the removal process can include an API request to ensure the
     * permissions granted to the application are removed.
     * <a href="https://developers.google.com/identity/protocols/OAuth2WebServer#offline">Click here to know more...</a>
     * @param doc
     * @param accessToken
     */
    public void revokeToken(GoogleDiscoveryDoc doc, String accessToken, final GoogleLoginListener listener){
        StringBuilder sb=new StringBuilder();
        String url = sb.append(doc.getRevocation_endpoint())
                .append("?token=")
                .append(accessToken)
                .toString();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        executeRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.code()==200){
                    listener.onRevokeAccessTokenSuccess();
                }else {
                    GoogleBaseResponse baseResponse = new Gson().fromJson(response.body().string(), GoogleBaseResponse.class);
                    listener.onRevokeAccessTokenFail(baseResponse.getError());
                }

            }
        });
    }



    /**
     * https://developers.google.com/identity/sign-in/android/backend-auth
     * @param idToken
     */
    public void getUserProfile(String idToken, final GoogleLoginListener listener) {
        try {
            idToken = URLDecoder.decode(idToken, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        final Request request = new Request.Builder()
                .url("https://www.googleapis.com/oauth2/v3/tokeninfo?id_token=" + idToken)
                .get()
                .build();
        executeRequest(request, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(!checkAndHandleResponseError(response,listener)){
                    GoogleUserProfile userProfile = new Gson().fromJson(response.body().string(), GoogleUserProfile.class);
                    String error = userProfile.getError();
                    if(TextUtils.isEmpty(error)){
                        listener.onGetUserProfile(userProfile);
                    }else {
                        listener.onError(error);
                    }
                }
            }
        });
    }

    private void executeRequest(Request request, Callback callback) {
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(callback);
    }


}
