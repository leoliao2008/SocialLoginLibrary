package com.tig.libraysociallogins.facebook.manager;

import android.app.Activity;
import android.text.TextUtils;

import com.tig.libraysociallogins.activity.LibrarySocialLoginWebViewActivity;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.facebook.beans.FacebookAccessToken;
import com.tig.libraysociallogins.facebook.listeners.FacebookLoginListener;
import com.tig.libraysociallogins.facebook.models.FacebookLoginModel;
import com.tig.libraysociallogins.listeners.LoadSocialLoginFrontPageListener;
import com.tig.libraysociallogins.listeners.SocialLoginListener;

import java.lang.ref.WeakReference;

public class FacebookLoginManager extends BaseLoginManager {
    private FacebookLoginListener mListener;
    private FacebookLoginModel mModel;
    private String mTokenState;

    public FacebookLoginManager(FacebookLoginListener listener) {
        mListener = listener;
        mModel=new FacebookLoginModel();
    }

    /**
     * {@link FacebookLoginModel#login(String, String, String, LoadSocialLoginFrontPageListener)}
     * @param activity
     * @param clientId
     * @param redirectUri
     */
    public void getAccessToken(Activity activity,String clientId,String redirectUri){
        WeakReference<Activity> ref=new WeakReference<>(activity);
        mTokenState = genAntiForgeryTokenState();
        LibrarySocialLoginWebViewActivity.setSocialLoginListener(
                new SocialLoginListener(){
                    @Override
                    public void onError(String msg) {
                        super.onError(msg);
                        mListener.onError(msg);
                    }

                    @Override
                    public void onGetFacebookAccessToken(FacebookAccessToken accessToken) {
                        super.onGetFacebookAccessToken(accessToken);
                        String state = accessToken.getState();
                        if(!TextUtils.isEmpty(state)&&!state.equals(mTokenState)){
                            mListener.onError("Invalid state.");
                            return;
                        }
                        mListener.onGetAccessToken(accessToken);
                    }
                }
        );
        if(ref.get()!=null){
            LibrarySocialLoginWebViewActivity.start(
                    ref.get(),
                    SOCIAL_PROVIDER_FACEBOOK,
                    clientId,
                    null,
                    mTokenState,
                    redirectUri
            );
        }
    }

    /**
     * {@link FacebookLoginModel#exchangeAuthCodeForAccessToken(String, String, String, String, FacebookLoginListener)}
     * @param authCode
     * @param appId
     * @param appSecret
     * @param redirectUri
     */
    public void exchangeAuthCodeForAccessToken(String authCode,String appId,String appSecret,String redirectUri){
        mModel.exchangeAuthCodeForAccessToken(
                authCode,
                appId,
                appSecret,
                redirectUri,
                mListener
        );
    }

    /**
     * {@link FacebookLoginModel#inspectAccessToken(String, String, String, FacebookLoginListener)}
     * @param appId
     * @param appSecret
     * @param accessToken
     */
    public void getUserProfile(String appId,String appSecret,String accessToken){
        mModel.inspectAccessToken(
                accessToken,
                appId,
                appSecret,
                mListener
        );
    }

    /**
     * {@link FacebookLoginModel#revokeLogin(String, String, FacebookLoginListener)}
     * @param userId
     * @param accessToken
     */
    public void logOut(String userId,String accessToken){
        mModel.revokeLogin(userId,accessToken,mListener);
    }
}
