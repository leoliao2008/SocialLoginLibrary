package com.tig.libraysociallogins.amazon.manager;

import android.app.Activity;

import com.tig.libraysociallogins.activity.LibrarySocialLoginWebViewActivity;
import com.tig.libraysociallogins.amazon.bean.AmazonAuthCode;
import com.tig.libraysociallogins.amazon.listensers.AmazonLoginListener;
import com.tig.libraysociallogins.amazon.listensers.CheckAmazonAccessTokenValidListener;
import com.tig.libraysociallogins.amazon.models.AmazonLoginModel;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.listeners.SocialLoginListener;

import java.lang.ref.WeakReference;

public class AmazonLoginManager extends BaseLoginManager {
    private AmazonLoginListener mListener;
    private AmazonLoginModel mModel;
    private String mState;

    public AmazonLoginManager(AmazonLoginListener listener) {
        mListener = listener;
        mModel=new AmazonLoginModel();
    }

    public void getAccessToken(Activity activity, final String clientId, final String clientSecret, final String redirectUri){
        mState = genAntiForgeryTokenState();
        WeakReference<Activity> ref=new WeakReference<>(activity);
        if(ref.get()!=null){
            LibrarySocialLoginWebViewActivity.setSocialLoginListener(new SocialLoginListener(){
                @Override
                public void onError(String msg) {
                    super.onError(msg);
                    mListener.onError(msg);
                }

                @Override
                public void onGetAmazonAuthCode(AmazonAuthCode amazonAuthCode) {
                    super.onGetAmazonAuthCode(amazonAuthCode);
                    if(!amazonAuthCode.getState().equals(mState)){
                        mListener.onError("Invalid state.");
                        return;
                    }
                    mModel.requestAccessToken(
                            amazonAuthCode.getAuthCode(),
                            redirectUri,
                            clientId,
                            clientSecret,
                            mListener
                    );
                }
            });

            LibrarySocialLoginWebViewActivity.start(
                    ref.get(),
                    SOCIAL_PROVIDER_AMAZON,
                    clientId,
                    clientSecret,
                    mState,
                    redirectUri

            );
        }
    }

    public void getUserProfile(final String accessToken, String clientId){
        mModel.checkIfAccessTokeValid(accessToken, clientId, new CheckAmazonAccessTokenValidListener() {
            @Override
            public void onTokenNotValid(String msg) {
                mListener.onError(msg);
            }

            @Override
            public void onTokenValid() {
                mModel.requestUserProfile(accessToken, mListener);
            }

            @Override
            public void onError(String msg) {
                mListener.onError(msg);

            }
        });
    }

    public void refreshAccessToken(String refreshToken,String clientId,String clientSecret){
        mModel.refreshAccessToken(refreshToken,clientId,clientSecret,mListener);
    }
}
