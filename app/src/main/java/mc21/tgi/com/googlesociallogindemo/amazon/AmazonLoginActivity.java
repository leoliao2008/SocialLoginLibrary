package mc21.tgi.com.googlesociallogindemo.amazon;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tgi.libraryloginwithamazon.bean.AccessTokenResponseBean;
import com.tgi.libraryloginwithamazon.listener.AmazonSocialLoginListener;
import com.tgi.libraryloginwithamazon.manager.AmazonSocialLoginManager;

import mc21.tgi.com.googlesociallogindemo.CONSTANTS;
import mc21.tgi.com.googlesociallogindemo.R;
import mc21.tgi.com.googlesociallogindemo.TgiRESTApiModel;
import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;

public class AmazonLoginActivity extends AppCompatActivity {
    private EditText mEdtDescription;
    private AmazonSocialLoginManager mLoginManager;
    private TgiRESTApiModel mRESTApiModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, AmazonLoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amazon_login);
        mEdtDescription=findViewById(R.id.activity_amazon_login_edt_description);
        mRESTApiModel=new TgiRESTApiModel();
        mLoginManager=new AmazonSocialLoginManager(this,new AmazonSocialLoginListener(){
            @Override
            public void onError(String errorMsg) {
                super.onError(errorMsg);
                updateDescription(errorMsg);
            }

            @Override
            public void onGetAccessToken(AccessTokenResponseBean token) {
                super.onGetAccessToken(token);
                mRESTApiModel.uploadToken(
                        token.getAccessToken(),
                        null,
                        "amazon",
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
                                Log.e("AmazonLoginActivity",log);
                            }
                        }
                );
            }
        });
    }

    private void updateDescription(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEdtDescription.setText(msg);
            }
        });

    }

    public void login(View view) {
        mLoginManager.authorize(
                CONSTANTS.AMAZON_SOCIAL_LOGIN_SECURITY_PROFILE_CLIENT_ID,
                CONSTANTS.AMAZON_SOCIAL_LOGIN_SECURITY_PROFILE_CLIENT_SECRET,
                CONSTANTS.AMAZON_SOCIAL_LOGIN_SECURITY_PROFILE_REDIRECT_URL
        );

    }
}
