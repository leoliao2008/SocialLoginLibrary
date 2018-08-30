package mc21.tgi.com.googlesociallogindemo.google;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tgi.librarygooglelogin.GoogleLoginManager;
import com.tgi.librarygooglelogin.bean.AuthCodeResponse;
import com.tgi.librarygooglelogin.bean.DiscoveryDoc;
import com.tgi.librarygooglelogin.bean.TokenResponse;
import com.tgi.librarygooglelogin.listener.GoogleLoginListener;

import mc21.tgi.com.googlesociallogindemo.CONSTANTS;
import mc21.tgi.com.googlesociallogindemo.R;
import mc21.tgi.com.googlesociallogindemo.TgiRESTApiModel;
import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;

public class GoogleOAuth2Activity extends AppCompatActivity {
    private EditText mEdtDescription;
    private GoogleLoginManager mLoginManager;
    private DiscoveryDoc mDiscoveryDoc;
    private TgiRESTApiModel mRESTApiModel;

    public static void start(Context context) {
        Intent starter = new Intent(context, GoogleOAuth2Activity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_oauth2);
        mEdtDescription=findViewById(R.id.activity_google_oauth2_edt_description);
        mRESTApiModel=new TgiRESTApiModel();
        mLoginManager=new GoogleLoginManager(new GoogleLoginListener(){
            @Override
            public void onError(String msg) {
                super.onError(msg);
                updateDescription(msg);
            }

            @Override
            public void onGetDiscoveryDoc(DiscoveryDoc discoveryDoc) {
                super.onGetDiscoveryDoc(discoveryDoc);
                updateDescription(discoveryDoc.toString());
                mDiscoveryDoc=discoveryDoc;
            }

            @Override
            public void onGetTokens(TokenResponse tokens) {
                super.onGetTokens(tokens);
                Log.e("Tokens from Google",tokens.toString());
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

                            }
                        }
                );
            }
        });
        mLoginManager.getDiscoveryDoc();
    }


    public void startAuthentication(View view) {
        if(mDiscoveryDoc==null){
            updateDescription("Need to fetch the latest Discovery Document first.");
            return;
        }
        mLoginManager.requestUserAuthentication(
                this,
                mDiscoveryDoc,
                CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_ID,
                CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_SECRET,
                CONSTANTS.GOOGLE_LOGIN_WEB_REDIRECT_URI,
                true
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
}
