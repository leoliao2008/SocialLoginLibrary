package com.tig.libraysociallogins.google.manager;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.tig.libraysociallogins.activity.LibrarySocialLoginWebViewActivity;
import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.google.beans.GoogleAuthCode;
import com.tig.libraysociallogins.google.beans.GoogleDiscoveryDoc;
import com.tig.libraysociallogins.google.listeners.GoogleLoginListener;
import com.tig.libraysociallogins.google.models.GoogleLoginModel;
import com.tig.libraysociallogins.listeners.SocialLoginListener;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoogleLoginManager extends BaseLoginManager {
    private GoogleLoginListener mListener;
    private GoogleLoginModel mModel;
    private static GoogleDiscoveryDoc googleDiscoveryDoc;
    private String mState;

    public GoogleLoginManager(GoogleLoginListener listener) {
        mListener = listener;
        mModel = new GoogleLoginModel();
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
                if (!isError) {
                    String json = response.body().string();
                    GoogleDiscoveryDoc discoveryDoc = new Gson().fromJson(json, GoogleDiscoveryDoc.class);
                    if (discoveryDoc.getError() == null) {
                        googleDiscoveryDoc = discoveryDoc;
                    }else {
                        mListener.onError(discoveryDoc.getError());
                    }
                }

            }
        });
    }

    public static GoogleDiscoveryDoc getGoogleDiscoveryDoc() {
        return googleDiscoveryDoc;
    }

    public void getAccessToken(Activity activity, final String clientId, final String clientSecret, final String redirectUri) {
        WeakReference<Activity> ref = new WeakReference<>(activity);
        if (googleDiscoveryDoc == null) {
            mListener.onError("GoogleDiscovery Doc is updating, please try later...");
            return;
        }
        mState = genAntiForgeryTokenState();
        if (ref.get() != null) {
            LibrarySocialLoginWebViewActivity.setSocialLoginListener(new SocialLoginListener() {
                @Override
                public void onGetGoogleAuthCode(GoogleAuthCode googleAuthCode) {
                    super.onGetGoogleAuthCode(googleAuthCode);
                    String state = googleAuthCode.getState();
                    if(!TextUtils.isEmpty(state)){
                        if(!state.equals(mState)){
                            mListener.onError("Invalid State.");
                            return;
                        }
                    }
                    mModel.exchangeAuthCodeForTokens(
                            googleDiscoveryDoc,
                            googleAuthCode.getAuthCode(),
                            clientId,
                            clientSecret,
                            redirectUri,
                            mListener
                    );
                }

                @Override
                public void onError(String msg) {
                    super.onError(msg);
                    mListener.onError(msg);
                }
            });
            LibrarySocialLoginWebViewActivity.start(
                    ref.get(),
                    SOCIAL_PROVIDER_GOOGLE,
                    clientId,
                    clientSecret,
                    mState,
                    redirectUri);
        }

    }

    public void refreshAccessToken(String refreshToken,String clientId,String clientSecret){
        mModel.refreshAccessToken(
                googleDiscoveryDoc,
                refreshToken,
                clientId,
                clientSecret,
                mListener
        );
    }

    public void revokeAccessToken(String accessToken){
        mModel.revokeToken(
                googleDiscoveryDoc,
                accessToken,
                mListener
        );
    }

    public void getUserProfile(String idToken){
        mModel.getUserProfile(idToken,mListener);
    }
}
