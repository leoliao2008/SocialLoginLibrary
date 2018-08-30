package mc21.tgi.com.googlesociallogindemo.facebook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import mc21.tgi.com.googlesociallogindemo.R;

public class OfficialFacebookLoginActivity extends AppCompatActivity {
    private LoginButton mLoginButton;
    private static final String EMAIL = "email";
    private CallbackManager mCallbackManager;
    private EditText mEdtDescription;

    public static void start(Context context) {
        Intent starter = new Intent(context, OfficialFacebookLoginActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_official_facebook_login);
        mLoginButton=findViewById(R.id.activity_official_facebook_login_btn);
        mEdtDescription =findViewById(R.id.activity_official_edt_description);
        mCallbackManager = CallbackManager.Factory.create();


        mLoginButton.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        mLoginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                String token = loginResult.getAccessToken().getToken();
                updateDescription(token);


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void showLog(String msg){
        Log.e(getClass().getSimpleName(),msg);
    }


}
