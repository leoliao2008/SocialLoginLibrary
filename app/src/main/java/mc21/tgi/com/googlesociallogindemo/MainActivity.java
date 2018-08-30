package mc21.tgi.com.googlesociallogindemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import mc21.tgi.com.googlesociallogindemo.amazon.AmazonLoginActivity;
import mc21.tgi.com.googlesociallogindemo.facebook.OfficialFacebookLoginActivity;
import mc21.tgi.com.googlesociallogindemo.facebook.TgiFbLoginActivity;
import mc21.tgi.com.googlesociallogindemo.google.GoogleLoginActivity;
import mc21.tgi.com.googlesociallogindemo.google.GoogleOAuth2Activity;
import mc21.tgi.com.googlesociallogindemo.google.OfficialSDKLoginActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toOfficialSDK(View view) {
        OfficialSDKLoginActivity.start(this);
    }

    public void toAmazonLogin(View view) {
        AmazonLoginActivity.start(this);

    }

    public void toCustomizedGoogleLogin(View view) {
        GoogleLoginActivity.start(this);
    }

    public void toOAuth2GoogleLogin(View view) {
        GoogleOAuth2Activity.start(this);
    }

    public void toFbOfficialLogin(View view) {
        OfficialFacebookLoginActivity.start(this);
    }

    public void toFbCustomizedLogin(View view) {
        TgiFbLoginActivity.start(this);
    }
}
