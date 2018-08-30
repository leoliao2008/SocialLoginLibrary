package com.tgi.libraryfacebooklogin.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.tgi.libraryfacebooklogin.models.FbLoginModel;
import com.tgi.libraryfacebooklogin.LibraryFacebookLoginConstants;
import com.tgi.libraryfacebooklogin.R;
import com.tgi.libraryfacebooklogin.beans.FbAccessTokenResponse;
import com.tgi.libraryfacebooklogin.listeners.FacebookLoginListener;
import com.tgi.libraryfacebooklogin.listeners.FbAuthCodeListener;
import com.tgi.libraryfacebooklogin.utils.LibraryFbLoginSpUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;

public class FbWebViewActivity extends AppCompatActivity {

    private WebView mWebView;
    private FbLoginModel mFbLoginModel;
    private String mClientId;
    private static FacebookLoginListener facebookLoginListener;


    public static void start(Context context, String clientId) {
        Intent starter = new Intent(context, FbWebViewActivity.class);
        starter.putExtra(LibraryFacebookLoginConstants.KEY_CLIENT_ID, clientId);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        hookWebView();
        setContentView(R.layout.activity_fb_webview);
        mWebView = findViewById(R.id.activity_fb_web_view_web_view);
        initWebView();
        mClientId = getIntent().getStringExtra(LibraryFacebookLoginConstants.KEY_CLIENT_ID);
        mFbLoginModel = new FbLoginModel();
        //the reason why I use Model class here instead of passing the html src code to this activity is that the
        //html src code has exceeded the max size in a bundle or an intent. It is more
        //applicable to get the html code here rather than pass the code into a bundle and pass
        //it here.
        mFbLoginModel.login(mClientId, new FbAuthCodeListener() {

            @Override
            public void onError(String msg) {
                showLog("FbWebViewActivity-mFbLoginModel-login.onError:" + msg);
            }

            @Override
            public void onGetSignInWebPage(final String htmlSrcCode) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadDataWithBaseURL(
                                null,
                                htmlSrcCode,
                                "text/html",
                                "utf-8",
                                null
                        );
                    }
                });


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
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                showLog("FbWebViewActivity-mWebView-onPageStarted:" + url);
                if (url.startsWith(LibraryFacebookLoginConstants.REDIRECT_URL)) {
                    try {
                        URI uri = new URI(url);
                        //for reference: https://developers.facebook.com/docs/facebook-login/manually-build-a-login-flow
                        String query = uri.getQuery();//if "?" exist, that means there is an error
                        String fragment = uri.getFragment();//if "#" exist, that means token is returned
                        FbAccessTokenResponse token = genFbAccessToken(query, fragment);
                        if(facebookLoginListener!=null){
                            if(token.isError()){
                                facebookLoginListener.onError(token.getErrorReason());
                            }else {
                                facebookLoginListener.onGetAccessToken(token);
                                LibraryFbLoginSpUtil.saveToken(FbWebViewActivity.this,token.getAccessToken());
                            }
                        }
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                        if(facebookLoginListener!=null){
                            facebookLoginListener.onError(e.getMessage());
                        }
                    }
                    finish();
                }
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                showLog("error:" + errorCode + " description:" + description);
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
//        settings.setCacheMode(WebSettings.LOAD_NO_CACHE); //关闭webview中缓存
        settings.setAllowFileAccess(true); //设置可以访问文件
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadsImagesAutomatically(true); //支持自动加载图片
        settings.setDefaultTextEncodingName("utf-8");//设置编码格式
//        settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
    }

    private FbAccessTokenResponse genFbAccessToken(String query, String fragment) {
        FbAccessTokenResponse token = new FbAccessTokenResponse();
        //there is an error
        if (!TextUtils.isEmpty(query)) {
            token.setIsError(true);
            String[] querySplits = query.split("&");
            for (String temp : querySplits) {
                String[] tempSplits = temp.split("=");
                if (tempSplits.length == 2) {
                    switch (tempSplits[0]) {
                        case "error_reason":
                            token.setErrorReason(tempSplits[1]);
                            break;
                        case "error":
                            token.setError(tempSplits[1]);
                            break;
                        case "error_description":
                            token.setErrorDescription(tempSplits[1]);
                            break;
                    }
                }
            }
        }
        //there is an access token
        if(!TextUtils.isEmpty(fragment)){
            String[] fragmentSplits = fragment.split("&");
            for(String temp:fragmentSplits){
                String[] tempSplits = temp.split("=");
                if(tempSplits.length==2){
                    switch (tempSplits[0]){
                        case "state":
                            token.setState(tempSplits[1]);
                            break;
                        case "access_token":
                            token.setAccessToken(tempSplits[1]);
                            break;
                        case "expires_in":
                            //transform into milli seconds
                            token.setExpiresIn(Long.valueOf(tempSplits[1])*1000);
                            break;
                    }
                }
            }
        }

        return token;
    }

    private void showLog(String url) {
        Log.e("FbWebViewActivity", url);
    }

    public static void setFacebookLoginListener(FacebookLoginListener listener) {
        facebookLoginListener = listener;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        facebookLoginListener = null;
        clearWebViewCache();
    }

    //this is vital to keep facebook social login going.
    public void clearWebViewCache() {
        // 清除cookie即可彻底清除缓存
        CookieSyncManager.createInstance(this);
        CookieManager.getInstance().removeAllCookie();
    }
}
