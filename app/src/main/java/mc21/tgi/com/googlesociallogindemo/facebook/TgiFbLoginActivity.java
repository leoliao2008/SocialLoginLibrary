package mc21.tgi.com.googlesociallogindemo.facebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.tgi.libraryfacebooklogin.FbLoginManager;
import com.tgi.libraryfacebooklogin.beans.FbAccessTokenResponse;
import com.tgi.libraryfacebooklogin.beans.FbUserBean;
import com.tgi.libraryfacebooklogin.listeners.FacebookLoginListener;

import mc21.tgi.com.googlesociallogindemo.CONSTANTS;
import mc21.tgi.com.googlesociallogindemo.R;
import mc21.tgi.com.googlesociallogindemo.TgiRESTApiModel;
import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;

public class TgiFbLoginActivity extends AppCompatActivity {
    private EditText mEdtDescription;
    private FbLoginManager mFbLoginManager;
    private TgiRESTApiModel mTgiRESTApiModel;
    private FbUserBean mFbUserBean;


    public static void start(Context context) {
        Intent starter = new Intent(context, TgiFbLoginActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tgi_fb_login);
        mEdtDescription=findViewById(R.id.activity_tgi_fb_login_edt_description);
        mTgiRESTApiModel=new TgiRESTApiModel();
        mFbLoginManager=new FbLoginManager(new FacebookLoginListener(){
            @Override
            public void onGetFbUser(FbUserBean userBean) {
                super.onGetFbUser(userBean);
                mFbUserBean=userBean;
                updateDescription(userBean.toString());

            }

            @Override
            public void onGetAccessToken(FbAccessTokenResponse token) {
                super.onGetAccessToken(token);
                updateDescription(token.toString());
                mTgiRESTApiModel.uploadToken(
                        token.getAccessToken(),
                        null,
                        "facebook",
                        new TgiRESTApiModel.SocialLoginListener() {
                            @Override
                            public void onError(String msg) {
                                updateDescription(msg);
                            }

                            @Override
                            public void onGetLoginResponse(TgiSocialLoginResponse response) {
                                updateDescription(response.toString());
                            }

                            @Override
                            public void log(String log) {

                            }
                        }
                );

            }

            @Override
            public void onUserLogout(String string) {
                super.onUserLogout(string);
                updateDescription(string);
            }



            @Override
            public void onError(String message) {
                updateDescription(message);
            }

            @Override
            public void onLog(String msg) {
                super.onLog(msg);
                updateDescription(msg);
            }
        });
    }

    public void getAuthCode(View view) {
        mFbLoginManager.logIn(
                this,
                CONSTANTS.FACEBOOK_LOGIN_APP_ID
        );

    }

    private void updateDescription(final String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEdtDescription.setText(msg);
            }
        });
    }

    public void logOut(View view) {
        if(mFbUserBean==null){
            return;
        }
        mFbLoginManager.logOut(
                mFbUserBean.getData().getUser_id(),
                mFbLoginManager.getExistingAccessToken(TgiFbLoginActivity.this));
    }

    public void inspectAccessToken(View view) {
        String accessToken = mFbLoginManager.getExistingAccessToken(TgiFbLoginActivity.this);
        if(TextUtils.isEmpty(accessToken)){
            return;
        }
        mFbLoginManager.getFbUser(
                accessToken,
                CONSTANTS.FACEBOOK_LOGIN_APP_ID,
                CONSTANTS.FACEBOOK_LOGIN_APP_SECRET
        );

    }
}
