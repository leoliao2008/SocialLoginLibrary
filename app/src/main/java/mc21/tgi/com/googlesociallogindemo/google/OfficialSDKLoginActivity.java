package mc21.tgi.com.googlesociallogindemo.google;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.tgi.librarygooglelogin.GoogleLoginManager;
import com.tgi.librarygooglelogin.bean.VerifyIdTokenResponse;
import com.tgi.librarygooglelogin.listener.GoogleLoginListener;

import java.net.URLEncoder;

import mc21.tgi.com.googlesociallogindemo.CONSTANTS;
import mc21.tgi.com.googlesociallogindemo.R;
import mc21.tgi.com.googlesociallogindemo.TgiRESTApiModel;
import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;

public class OfficialSDKLoginActivity extends AppCompatActivity {

    private static final int REQ_GOOGLE_SIGN_IN = 357;
    private GoogleSignInClient mGoogleSignInClient;
    private EditText mEdtDescription;
    private TgiRESTApiModel mApiModel;
    private GoogleLoginManager mGoogleLoginManager;
    private String mIdToken;
    private String mAccessToken;

    public static void start(Context context) {
        Intent starter = new Intent(context, OfficialSDKLoginActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_sdklogin);
        mEdtDescription = findViewById(R.id.official_login_sdk_activity_edt_user_description);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_ID)
                .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                .requestServerAuthCode(CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_ID)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mApiModel=new TgiRESTApiModel();
        mGoogleLoginManager=new GoogleLoginManager(new GoogleLoginListener(){
            @Override
            public void onError(String msg) {
                super.onError(msg);
                updateUiWithMsg(msg);
            }

            @Override
            public void onGetIdTokenVerificationResponse(VerifyIdTokenResponse response) {
                super.onGetIdTokenVerificationResponse(response);
                updateUiWithMsg(response.toString());
            }

            @Override
            public void onGetAccessToken(String string) {
                super.onGetAccessToken(string);
                mAccessToken=string;
                updateUiWithMsg(string);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            updateUiWithAccount(account);
        }
    }

    private void updateUiWithAccount(final GoogleSignInAccount account) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (account != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Display Name:")
                            .append(account.getDisplayName())
                            .append("\r\n")
                            .append("Email:")
                            .append(account.getEmail())
                            .append("\r\n")
                            .append("ID:")
                            .append(account.getId())
                            .append("\r\n")
                            .append("IdToken:")
                            .append(account.getIdToken());
                    mEdtDescription.setText(sb.toString());
                } else {
                    mEdtDescription.setText("Account is null.");
                }

            }
        });
    }

    public void startLoginWorkFlow(View view) {
        showLog("start to login");
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, REQ_GOOGLE_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_GOOGLE_SIGN_IN) {
            showLog("begin to update account.");
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = null;
            try {
                account = task.getResult(ApiException.class);
                mIdToken=account.getIdToken();
            } catch (ApiException e) {
                e.printStackTrace();
            }
            updateUiWithAccount(account);
        }
    }

    private void showLog(String msg) {
        Log.e(getClass().getSimpleName(), msg);
    }

    public void signOut(View view) {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mEdtDescription.setText("The user  has signed out.");
            }
        });
    }

    public void clearUserData(View view) {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mEdtDescription.setText("User data has been cleared.");
            }
        });
    }

    public void uploadToken(View view) {
        if(TextUtils.isEmpty(mAccessToken)||TextUtils.isEmpty(mIdToken)){
            Toast.makeText(this,"Need to ge id token and access token first",Toast.LENGTH_SHORT).show();
            return;
        }
        showLog("AccessToken:"+mAccessToken);
        showLog("IdToken:"+mIdToken);
        mApiModel.uploadToken(
                mAccessToken,
                mIdToken,
                "google",
                new TgiRESTApiModel.SocialLoginListener() {
                    @Override
                    public void onError(String msg) {
                        updateUiWithMsg(msg);
                    }

                    @Override
                    public void onGetLoginResponse(TgiSocialLoginResponse response) {
                        updateUiWithMsg(response.toString());

                    }

                    @Override
                    public void log(String log) {
                        showLog(log);
                    }
                }
        );


    }

    private void updateUiWithMsg(final String msg) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEdtDescription.setText(msg);
            }
        });
    }

    public void verifyIdToken(View view) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            mGoogleLoginManager.verifyIdToken(account.getIdToken());
        }
    }

    public void getAccessToken(View view) {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account!=null){
            Log.e("auth code",account.getServerAuthCode());
            mGoogleLoginManager.getAccessToken(
                    account.getServerAuthCode(),
                    CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_ID,
                    CONSTANTS.GOOGLE_LOGIN_WEB_CLIENT_SECRET,
                    ""
            );
        }
    }
}
