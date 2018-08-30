package com.tig.libraysociallogins.google.manager;

import android.app.Activity;

import com.google.gson.Gson;
import com.tig.libraysociallogins.activity.LibrarySocialLoginWebViewActivity;
import com.tig.libraysociallogins.base.BaseListener;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.google.beans.GoogleDiscoveryDoc;
import com.tig.libraysociallogins.google.listeners.GoogleLoginListener;
import com.tig.libraysociallogins.google.models.GoogleSocialLoginModel;
import com.tig.libraysociallogins.listeners.GetPermissionListener;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoogleLoginManager extends BaseLoginManager {
    private GoogleLoginListener mListener;
    private GoogleSocialLoginModel mModel;
    private static GoogleDiscoveryDoc googleDiscoveryDoc;
    private String mState;

    public GoogleLoginManager(GoogleLoginListener listener) {
        mListener = listener;
        mModel = new GoogleSocialLoginModel();
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
                    }
                }

            }
        });
    }

    public static GoogleDiscoveryDoc getGoogleDiscoveryDoc() {
        return googleDiscoveryDoc;
    }

    public void getAccessToken(Activity activity, String clientId, String redirectUri) {
        WeakReference<Activity> ref = new WeakReference<>(activity);
        if (googleDiscoveryDoc == null) {
            mListener.onError("GoogleDiscovery Doc is updating, please try later...");
            return;
        }
        mState = genAntiForgeryTokenState();
        if (ref.get() != null) {
            LibrarySocialLoginWebViewActivity.setGetPermissionListener(new GetPermissionListener() {

            });
            LibrarySocialLoginWebViewActivity.start(ref.get(), SOCIAL_PROVIDER_GOOGLE, clientId, null, mState, redirectUri);
        }


//        mModel.requestUserAuthentication(
//                googleDiscoveryDoc,
//                clientId,
//                redirectUri,
//                mState,
//                true,
//                new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//
//                    }
//                }
//        );

    }
}
