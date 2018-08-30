package com.tgi.libraryloginwithamazon.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tgi.libraryloginwithamazon.AmazonSocialLoginConstants;
import com.tgi.libraryloginwithamazon.R;
import com.tgi.libraryloginwithamazon.bean.AuthCodeResponseBean;
import com.tgi.libraryloginwithamazon.listener.AuthCodeRequestListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;


public class WebViewActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private ImageView mIvBack;
    private ImageView mIvClose;
    private String mRedirectUrl;
    private String mClientId;
    private String mClientSecret;
    private static AuthCodeRequestListener authCodeRequestListener;
    private static boolean isFirstTimeRunning=true;
    private ArrayList<String> mHistory=new ArrayList<>();
    private boolean isFromBackPress;
    private boolean isOptSuccess;
    private String mErrorMsg;


    public static void start(Context context, String clientId, String clientSecret, String htmlLoginPageSourceCode, String redirectUrl) {
        Intent starter = new Intent(context, WebViewActivity.class);
        starter.putExtra(AmazonSocialLoginConstants.KEY_CLIENT_ID, clientId);
        starter.putExtra(AmazonSocialLoginConstants.KEY_LOGIN_HTML_SRC, htmlLoginPageSourceCode);
        starter.putExtra(AmazonSocialLoginConstants.KEY_REDIRECT_URL, redirectUrl);
        starter.putExtra(AmazonSocialLoginConstants.KEY_CLIENT_SECRET, clientSecret);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is to skip the "Cannot use web view in System process Exception"
        if(isFirstTimeRunning){
            hookWebView();
            isFirstTimeRunning=false;
        }
        setContentView(R.layout.login_with_amazon_lib_activity_web_view);
        //find view
        mProgressBar = findViewById(R.id.activity_web_view_progress_bar);
        mWebView = findViewById(R.id.activity_web_view_web_view);
        mIvClose=findViewById(R.id.activity_web_view_iv_close);
        mIvBack=findViewById(R.id.activity_web_view_iv_back);
        //init data
        String htmlPage = getIntent().getStringExtra(AmazonSocialLoginConstants.KEY_LOGIN_HTML_SRC);
        mRedirectUrl = getIntent().getStringExtra(AmazonSocialLoginConstants.KEY_REDIRECT_URL);
        mClientId = getIntent().getStringExtra(AmazonSocialLoginConstants.KEY_CLIENT_ID);
        mClientSecret = getIntent().getStringExtra(AmazonSocialLoginConstants.KEY_CLIENT_SECRET);
        initWebView();

        //init listener
        initListeners();

        //https://www.cnblogs.com/baiqiantao/p/7249835.html 此处有坑
//        mWebView.loadData(htmlPage, "text/html;charset=UTF-8", null);
        mWebView.loadDataWithBaseURL(null,htmlPage,"text/html","utf-8",null);
    }

    private void initListeners() {
        mIvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mIvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                if(!isFromBackPress){
                    mHistory.add(url);
                }else {
                    isFromBackPress=false;
                }
                //https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html#access-token-response
                if (url.startsWith(mRedirectUrl)) {
                    mProgressBar.setVisibility(View.GONE);
                    AuthCodeResponseBean bean = genAuthCode(url);
                    if (bean != null && authCodeRequestListener != null) {
                        authCodeRequestListener.onGetAuthCode(bean);
                        isOptSuccess=true;
                    } else {
                        isOptSuccess=false;
                        mErrorMsg="The return url from the server is ill formed:" + url;
                    }
                    finish();
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
        //set a false user agent as if we request from a computer web browser because Google does not permit auth request from
        //android web view since 2016.
        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");

        //allow mix use of http and https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        //其他细节操作
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式

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
        if(size >1){
            isFromBackPress=true;
            String url = mHistory.get(size - 1);
            mHistory.remove(size-1);
            mWebView.loadUrl(url);
        }else {
            super.onBackPressed();
        }
    }


    @Override
    protected void onDestroy() {
        if(!isOptSuccess){
            if(TextUtils.isEmpty(mErrorMsg)){
                mErrorMsg="Login Aborts.";
            }
            if(authCodeRequestListener!=null){
                authCodeRequestListener.onError(mErrorMsg);
            }
        }
        authCodeRequestListener=null;
        super.onDestroy();
    }

    private AuthCodeResponseBean genAuthCode(String url) {
        AuthCodeResponseBean bean = null;
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            String[] strings = query.split("&");
            bean = new AuthCodeResponseBean();
            bean.setRedirectUrl(mRedirectUrl);
            bean.setClientId(mClientId);
            bean.setClientSecret(mClientSecret);
            for (String temp : strings) {
                String[] tempSplit = temp.split("=");
                switch (tempSplit[0]) {
                    case "code":
                        try {
                            tempSplit[1] = URLDecoder.decode(tempSplit[1], "UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        bean.setAuthCode(tempSplit[1]);
                        break;
                    case "state":
                        bean.setRequestState(tempSplit[1]);
                        break;
                    case "error":
                        bean.setErrorCode(tempSplit[1]);
                        break;
                    case "error_description":
                        bean.setErrorDescription(tempSplit[1]);
                        break;
                    case "error_uri":
                        bean.setErrorUrl(tempSplit[1]);
                        break;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            mErrorMsg=e.getMessage();
            finish();
        }

        return bean;
    }

    public static void setAuthCodeRequestListener(AuthCodeRequestListener authCodeRequestListener) {
        WebViewActivity.authCodeRequestListener = authCodeRequestListener;
    }
}
