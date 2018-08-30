package com.tgi.libraryloginwithamazon.manager;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tgi.libraryloginwithamazon.activity.WebViewActivity;
import com.tgi.libraryloginwithamazon.bean.AccessTokenResponseBean;
import com.tgi.libraryloginwithamazon.bean.AuthCodeResponseBean;
import com.tgi.libraryloginwithamazon.bean.ResponseBean;
import com.tgi.libraryloginwithamazon.bean.UserProfileBean;
import com.tgi.libraryloginwithamazon.listener.AmazonSocialLoginListener;
import com.tgi.libraryloginwithamazon.listener.AuthCodeRequestListener;
import com.tgi.libraryloginwithamazon.model.AmazonSocialLoginModel;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * This manager class is used to handle the Amazon social login feature.
 * It will open a web view{@link WebViewActivity} and direct the user to Amazon login home page
 * to finish the login and access granting process.
 */
public class AmazonSocialLoginManager {
    private AmazonSocialLoginModel mSocialLoginModel;
    private WeakReference<Context> mContext;
    private String mRequestState;
    private AmazonSocialLoginListener mLoginListener;
    private static String Tag;

    //public
    public AmazonSocialLoginManager(Context context, AmazonSocialLoginListener listener) {
        Tag = getClass().getSimpleName();
        mContext = new WeakReference<>(context);
        mLoginListener = listener;
        mSocialLoginModel = new AmazonSocialLoginModel();
        //I choose not to use broadcasting but a simple call back to acquire auth code because
        //it makes this lib easier to use.
        WebViewActivity.setAuthCodeRequestListener(new AuthCodeRequestListener() {
            @Override
            public void onGetAuthCode(AuthCodeResponseBean bean) {
                if (bean != null) {
                    String requestState = bean.getRequestState();
                    if (mRequestState != null && !mRequestState.equals(requestState)) {
                        mLoginListener.onError("State Error.");
                        return;
                    }
                    if (checkIfResponseError(bean)) {
                        mLoginListener.onError(genResponseErrorMsg(bean));
                    } else {
                        requestAccessToken(bean);
                    }
                } else {
                    mLoginListener.onError("No response from Amazon Server.");
                }
            }

            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                mLoginListener.onError(errorMsg);
            }
        });

    }

    /**
     * Start the process of requesting for a user's Amazon account access token.
     * The first step should be getting an authorization code.
     * The second step is to use the auth code to apply for an access token(this step is automatically done after we get the auth code).
     *
     * @param clientId     client id from Login With Amazon <a href="https://developer.amazon.com/iba-sp/overview.html">security profile<a/>
     * @param clientSecret client secret from Login With Amazon security profile
     * @param redirectUrl  the Url which the web view will return to after a successful login. The Url address must be firstly listed
     *                     into the white list of security profile.
     */
    public void authorize(final String clientId, final String clientSecret, final String redirectUrl) {
        mRequestState = String.valueOf(System.currentTimeMillis());
        mSocialLoginModel.requestAuthorizeCode(clientId, mRequestState, redirectUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLoginListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String htmlPage = response.body().string();
                if (mContext.get() != null) {
                    WebViewActivity.start(mContext.get(), clientId, clientSecret, htmlPage, redirectUrl);
                }
            }
        });
    }

    /**
     * Every access token has an expire period, usually within 1 hour.
     * Use this function to get a refresh access token.
     *
     * @param accessToken an access token.
     */
    public void refreshAccessToke(final AccessTokenResponseBean accessToken) {
        mSocialLoginModel.refreshAccessToken(accessToken, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLoginListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                AccessTokenResponseBean token = genAccessToken(
                        accessToken.getClientId(),
                        accessToken.getClientSecret(),
                        response.body().string()
                );
                if (checkIfResponseError(token)) {
                    mLoginListener.onError(genResponseErrorMsg(token));
                } else {
                    mLoginListener.onGetNewlyRefreshedToken(token);
                }
            }
        });
    }

    /**
     * Use the access token to request the user's profile.
     *
     * @param token An access token.
     */
    public void requestUserProfile(final AccessTokenResponseBean token) {
        mSocialLoginModel.verifyAccessToke(token, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLoginListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (checkIfTheSameClientId(response.body().string(), token.getClientId())) {
                    mSocialLoginModel.requestUserProfile(token, new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            mLoginListener.onError(e.getMessage());
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            UserProfileBean userProfile = genUserProfile(response.body().string());
                            if (checkIfResponseError(userProfile)) {
                                mLoginListener.onError(genResponseErrorMsg(userProfile));
                            } else {
                                mLoginListener.onGetUserProfile(userProfile);
                            }
                        }
                    });
                } else {
                    mLoginListener.onError("Access Token May Come from a Malicious Source. Abort Request.");
                }
            }
        });
    }


    //private

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

    private UserProfileBean genUserProfile(String responseBody) {
        showLog("responseBody=" + responseBody);
        UserProfileBean bean = new UserProfileBean();
        responseBody = responseBody.replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .trim();
        String[] split = responseBody.split(",");
        for (String temp : split) {
            String[] tempSplit = temp.trim().split(":");
            switch (tempSplit[0]) {
                case "user_id":
                    bean.setUserId(tempSplit[1]);
                    break;
                case "email":
                    bean.setEmail(tempSplit[1]);
                    break;
                case "name":
                    bean.setName(tempSplit[1]);
                    break;
                case "postal_code":
                    bean.setPostCode(tempSplit[1]);
                    break;
                case "error":
                    bean.setErrorCode(tempSplit[1]);
                    break;
                case "error_description":
                    bean.setErrorDescription(tempSplit[1]);
                    break;
                case "request_id":
                    bean.setRequestId(tempSplit[1]);
                    break;
            }
        }
        return bean;
    }

    private void requestAccessToken(final AuthCodeResponseBean authCode) {
        mSocialLoginModel.requestAccessToken(authCode, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mLoginListener.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                AccessTokenResponseBean token = genAccessToken(
                        authCode.getClientId(),
                        authCode.getClientSecret(),
                        response.body().string()
                );
                if (checkIfResponseError(token)) {
                    mLoginListener.onError(genResponseErrorMsg(token));
                } else {
                    mLoginListener.onGetAccessToken(token);
                }
            }
        });
    }

    private String genResponseErrorMsg(ResponseBean token) {
        StringBuilder sb = new StringBuilder();
        sb.append("Error:").append(token.getErrorCode());
        String description = token.getErrorDescription();
        if (!TextUtils.isEmpty(description)) {
            sb.append(",").append("Description:").append(description);
        }
        String errorUri = token.getErrorUrl();
        if (!TextUtils.isEmpty(errorUri)) {
            sb.append(",").append("ErrorUri:").append(errorUri);
        }
        sb.append(".");
        return sb.toString();
    }

    private boolean checkIfResponseError(ResponseBean token) {
        return token.getErrorCode() != null;
    }


    private AccessTokenResponseBean genAccessToken(String clientId, String clientSecret, String responseBody) {
        AccessTokenResponseBean tokenResponseBean = new AccessTokenResponseBean();
        tokenResponseBean.setClientId(clientId);
        tokenResponseBean.setClientSecret(clientSecret);
        String[] split = responseBody.replace("{", "")
                .replace("}", "")
                .replace("\"", "")
                .trim()
                .split(",");

        for (String temp : split) {
            String[] tempSplit = temp.trim().split(":");
            tempSplit[0] = tempSplit[0].trim();
            tempSplit[1] = tempSplit[1].trim();
            switch (tempSplit[0]) {
                case "access_token":
                    tokenResponseBean.setAccessToken(URLDecoder.decode(tempSplit[1]));
                    break;
                case "token_type":
                    tokenResponseBean.setTokenType(tempSplit[1]);
                    break;
                case "expires_in":
                    tokenResponseBean.setDurability(tempSplit[1]);
                    break;
                case "refresh_token":
                    tokenResponseBean.setRefreshToken(URLDecoder.decode(tempSplit[1]));
                    break;
                case "error":
                    tokenResponseBean.setErrorCode(tempSplit[1]);
                    break;
                case "error_description":
                    tokenResponseBean.setErrorDescription(tempSplit[1]);
                    break;
                case "error_uri":
                    tokenResponseBean.setErrorUrl(tempSplit[1]);
                    break;
            }
        }
        return tokenResponseBean;
    }

    private void showLog(String msg) {
        Log.e(Tag, msg);
    }


}
