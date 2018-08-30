package mc21.tgi.com.googlesociallogindemo.google;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tgi.librarygooglelogin.GoogleLoginManager;
import com.tgi.librarygooglelogin.bean.TokenResponse;
import com.tgi.librarygooglelogin.bean.VerifyIdTokenResponse;
import com.tgi.librarygooglelogin.listener.GoogleLoginListener;

import mc21.tgi.com.googlesociallogindemo.CONSTANTS;
import mc21.tgi.com.googlesociallogindemo.R;
import mc21.tgi.com.googlesociallogindemo.TgiRESTApiModel;
import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;

public class GoogleLoginActivity extends AppCompatActivity {
    private EditText mEdtDescription;
    private GoogleLoginManager mGoogleLoginManager;
    private TgiRESTApiModel mRESTApiModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, GoogleLoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);
        mEdtDescription=findViewById(R.id.activity_google_login_edt_description);
        mRESTApiModel=new TgiRESTApiModel();
        mGoogleLoginManager=new GoogleLoginManager(new GoogleLoginListener(){
            @Override
            public void onGetTokens(TokenResponse tokens) {
                super.onGetTokens(tokens);
                showLog("IdToken",tokens.getId_token());
                showLog("Access Token",tokens.getAccess_token());
                mRESTApiModel.uploadToken(
                        tokens.getAccess_token(),
                        tokens.getId_token(),
                        "google",
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
                                Log.e("uploadToken:",log);
                            }
                        }
                );
            }

            @Override
            public void onError(String msg) {
                super.onError(msg);
                updateDescription(msg);
            }

            @Override
            public void log(String log) {
                super.log(log);
                Log.e("GoogleLoginListener",log);
            }
        });
    }

    public void login(View view) {
        mGoogleLoginManager.requestSignIn(
                CONSTANTS.GOOGLE_LOGIN_DEVICE_CLIENT_ID,
                CONSTANTS.GOOGLE_LOGIN_DEVICE_CLIENT_SECRET,
                this
        );
    }

    private void updateDescription(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEdtDescription.setText(msg);
            }
        });
    }

    private void showLog(String tag,String msg){
        Log.e(tag,msg);
    }
}
