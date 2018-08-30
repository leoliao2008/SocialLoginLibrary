package com.tgi.librarygooglelogin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tgi.librarygooglelogin.GoogleLoginModel;
import com.tgi.librarygooglelogin.LibraryGoogleLoginConstants;
import com.tgi.librarygooglelogin.R;
import com.tgi.librarygooglelogin.bean.AuthCodeResponse;
import com.tgi.librarygooglelogin.bean.TokenResponse;
import com.tgi.librarygooglelogin.bean.UserCodeResponse;
import com.tgi.librarygooglelogin.listener.GoogleAuthCodeListener;
import com.tgi.librarygooglelogin.listener.GoogleLoginListener;
import com.tgi.librarygooglelogin.util.AlertDialogUtil;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GoogleLoginWebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private static boolean isHookWebView;
    private ProgressBar mProgressBar;
    private ArrayList<String> mHistory = new ArrayList<>();
    private boolean isFromBackPress;
    private boolean isOptSuccess;
    private String mErrorMsg;
    private UserCodeResponse mUserCodeResponse;
    private String mClientId;
    private String mClientSecret;
    private GoogleLoginModel mLoginModel;
    private Handler mHandler;
    private Callback mOKHttpCallback = new Callback() {
        @Override
        public void onFailure(Call call, IOException e) {
            onOperationFail(e.getMessage());
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            String json = response.body().string();
            TokenResponse tokenResponse = new Gson().fromJson(json, TokenResponse.class);
            if (tokenResponse.getError() == null) {
                onOperationSuccess(tokenResponse);
            } else {
                if (tokenResponse.getError().equals("access_denied")) {
                    onOperationFail("User Denies Access Permissions.");
                } else {
                    mHandler.postDelayed(mRunnablePollingForToken, mUserCodeResponse.getInterval() * 1000);
                }
            }

        }
    };


    private Runnable mRunnablePollingForToken = new Runnable() {
        @Override
        public void run() {
            mLoginModel.pollToObtainToken(
                    mClientId,
                    mClientSecret,
                    mUserCodeResponse.getDevice_code(),
                    mOKHttpCallback
            );
        }
    };
    private static GoogleLoginListener googleLoginListener;
    private int mLoginMode;
    private static final int LOGIN_MODE_DEVICE=1;
    private static final int LOGIN_MODE_OAUTH2=0;
    private String mRedirectUri;
    private static GoogleAuthCodeListener googleAuthCodeListener;
    private FloatingActionButton mFloatingActionButton;


    public static void start(
            int loginMode,
            Context context,
            UserCodeResponse userCodeResponse,
            String clientId,
            String clientSecret) {
        Intent starter = new Intent(context, GoogleLoginWebViewActivity.class);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_FLAG_LOGIN_MODE, loginMode);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_USER_CODE_RESPONSE, userCodeResponse);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_CLIENT_ID, clientId);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_CLIENT_SECRET, clientSecret);
        context.startActivity(starter);
    }

    public static void start(
            int loginMode,
            Context context,
            String clientId,
            String clientSecret,
            String redirectUri,
            String htmlData) {
        Intent starter = new Intent(context, GoogleLoginWebViewActivity.class);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_FLAG_LOGIN_MODE,loginMode);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_CLIENT_SECRET,clientSecret);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_CLIENT_ID,clientId);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_REDIRECT_URI,redirectUri);
        starter.putExtra(LibraryGoogleLoginConstants.KEY_HTML_DATA,htmlData);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!isHookWebView) {
            hookWebView();
            isHookWebView = true;
        }
        //init views
        setContentView(R.layout.activity_google_login_web_view);
        mWebView = findViewById(R.id.activity_google_login_web_view);
        mProgressBar = findViewById(R.id.activity_google_login_progress_bar);
        mFloatingActionButton=findViewById(R.id.activity_google_login_flbtn_close);
        //init data
        initWebView();
        mLoginMode=getIntent().getIntExtra(LibraryGoogleLoginConstants.KEY_FLAG_LOGIN_MODE,LOGIN_MODE_OAUTH2);
        if(mLoginMode==LOGIN_MODE_DEVICE){
            mHandler = new Handler();
            mUserCodeResponse = getIntent().getParcelableExtra(LibraryGoogleLoginConstants.KEY_USER_CODE_RESPONSE);
            mClientId = getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_CLIENT_ID);
            mClientSecret = getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_CLIENT_SECRET);
            if (mUserCodeResponse != null) {
                mLoginModel = new GoogleLoginModel();
                mWebView.loadUrl(mUserCodeResponse.getVerification_url());
                AlertDialogUtil.displayUserCode(
                        this,
                        mUserCodeResponse.getUser_code()
                );
                mHandler.post(mRunnablePollingForToken);
            }
        }else if(mLoginMode==LOGIN_MODE_OAUTH2){
            mClientId=getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_CLIENT_ID);
            mClientSecret = getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_CLIENT_SECRET);
            mRedirectUri=getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_REDIRECT_URI);
            String htmlData=getIntent().getStringExtra(LibraryGoogleLoginConstants.KEY_HTML_DATA);
            mWebView.loadDataWithBaseURL(
                    null,
                    htmlData,
                    "text/html",
                    "utf-8",
                    null
            );
        }
        //init listener
        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void hookWebView() {
        Class<?> factoryClass = null;
        try {
            factoryClass = Class.forName("android.webkit.WebViewFactory");
            Method getProviderClassMethod = null;
            Object sProviderInstance = null;

            if (Build.VERSION.SDK_INT == 23) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getProviderClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 22) {
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                Class<?> delegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> constructor = providerClass.getConstructor(delegateClass);
                if (constructor != null) {
                    constructor.setAccessible(true);
                    Constructor<?> constructor2 = delegateClass.getDeclaredConstructor();
                    constructor2.setAccessible(true);
                    sProviderInstance = constructor.newInstance(constructor2.newInstance());
                }
            } else if (Build.VERSION.SDK_INT == 21) {//Android 21无WebView安全限制
                getProviderClassMethod = factoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> providerClass = (Class<?>) getProviderClassMethod.invoke(factoryClass);
                sProviderInstance = providerClass.newInstance();
            }
            if (sProviderInstance != null) {
                Log.i("cym", sProviderInstance.toString());
                Field field = factoryClass.getDeclaredField("sProviderInstance");
                field.setAccessible(true);
                field.set("sProviderInstance", sProviderInstance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initWebView() {
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.loadUrl(request.getUrl().toString());
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!isFromBackPress) {
                    mHistory.add(url);
                } else {
                    isFromBackPress = false;
                }
                if(mLoginMode==LOGIN_MODE_OAUTH2&&url.startsWith(mRedirectUri)){
                    AuthCodeResponse response = genGoogleAuthCode(url);
                    if(response.getError()==null){
                        onOperationSuccess(response);
                    }else {
                        onOperationFail(response.getError());
                    }
                }
                mProgressBar.setVisibility(View.VISIBLE);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                mProgressBar.setVisibility(View.GONE);
            }

        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                mProgressBar.setProgress(newProgress);
            }
        });
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        settings.setAppCacheEnabled(true);

        //auto fits the screen scale
        //        settings.setUseWideViewPort(true);
        //        settings.setLoadWithOverviewMode(true);

        //allow mix use of http and https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //其他细节操作
        //        settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK); //关闭webview中缓存
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
        //set a false user agent as if we request from a computer web browser because Google does not permit auth request from
        //android web view since 2016.
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

    }

    private AuthCodeResponse genGoogleAuthCode(String responseBody) {
        AuthCodeResponse response=new AuthCodeResponse();
        try {
            URL url=new URL(responseBody);
            String[] queries = url.getQuery().split("&");
            for(String query:queries){
                String[] temp = query.split("=");
                switch (temp[0]){
                    case "state":
                        response.setState(temp[1]);
                        break;
                    case "code":
                        response.setAuthCode(temp[1]);
                        break;
                    case "error":
                        response.setError(temp[1]);
                        break;
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            googleLoginListener.onError(e.getMessage());
        }
        return response;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    public void onBackPressed() {
        int size = mHistory.size();
        if (size > 1) {
            isFromBackPress = true;
            String url = mHistory.get(size - 1);
            mHistory.remove(size - 1);
            mWebView.loadUrl(url);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        if (!isOptSuccess) {
            if (TextUtils.isEmpty(mErrorMsg)) {
                mErrorMsg = "Login Aborts.";
            }
            if (googleLoginListener != null) {
                googleLoginListener.onError(mErrorMsg);
            }
            if(googleAuthCodeListener!=null){
                googleAuthCodeListener.onError(mErrorMsg);
            }
        }
        googleLoginListener = null;
        googleAuthCodeListener=null;
        if(mHandler!=null){
            mHandler.removeCallbacks(mRunnablePollingForToken);
        }
        super.onDestroy();
    }

    private void errorButNotQuit(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(
                        GoogleLoginWebViewActivity.this,
                        message,
                        Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private void onOperationSuccess(AuthCodeResponse response) {
        isOptSuccess = true;
        if(googleAuthCodeListener!=null){
            googleAuthCodeListener.onGetAuthCode(response);
        }
        finish();
    }

    private void onOperationSuccess(TokenResponse tokenResponse) {
        isOptSuccess = true;
        if(googleLoginListener!=null){
            googleLoginListener.onGetTokens(tokenResponse);
        }
        finish();
    }

    private void onOperationFail(String message) {
        isOptSuccess = false;
        mErrorMsg = message;
        finish();
    }

    public static void setGoogleLoginListener(GoogleLoginListener listener) {
        googleLoginListener = listener;
    }

    public static void setGoogleAuthCodeListener(GoogleAuthCodeListener listener) {
        googleAuthCodeListener = listener;
    }
}
