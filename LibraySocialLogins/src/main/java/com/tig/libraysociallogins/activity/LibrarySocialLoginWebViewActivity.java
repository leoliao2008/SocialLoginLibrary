package com.tig.libraysociallogins.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.tig.libraysociallogins.LibrarySocialLoginsConstants;
import com.tig.libraysociallogins.R;
import com.tig.libraysociallogins.amazon.bean.AmazonAuthCode;
import com.tig.libraysociallogins.amazon.models.AmazonSocialLoginModel;
import com.tig.libraysociallogins.base.BaseLoginManager;
import com.tig.libraysociallogins.listeners.GetPermissionListener;
import com.tig.libraysociallogins.listeners.LoadSocialLoginFrontPageListener;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.tig.libraysociallogins.base.BaseLoginManager.SOCIAL_PROVIDER_AMAZON;
import static com.tig.libraysociallogins.base.BaseLoginManager.SOCIAL_PROVIDER_FACEBOOK;
import static com.tig.libraysociallogins.base.BaseLoginManager.SOCIAL_PROVIDER_GOOGLE;


public class LibrarySocialLoginWebViewActivity extends AppCompatActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private ImageView mIvBack;
    private ImageView mIvClose;
    private static boolean isFirstTimeRunning = true;
    private ArrayList<String> mHistory = new ArrayList<>();
    private boolean isFromBackPress;
    private String mErrorMsg;
    private String mClientId;
    private String mState;
    private String mRedirectUri;
    private String mClientSecret;
    private int mSocialProvider;
    private static GetPermissionListener mGetPermissionListener;
    private AmazonSocialLoginModel mAmazonSocialLoginModel;


    public static void start(
            Context context,
            int socialProvider,
            String clientId,
            @Nullable String clientSecret,
            String state,
            String redirectUri) {

        Intent starter = new Intent(context, LibrarySocialLoginWebViewActivity.class);
        starter.putExtra(LibrarySocialLoginsConstants.KEY_SOCIAL_PROVIDER, socialProvider);
        starter.putExtra(LibrarySocialLoginsConstants.KEY_CLIENT_ID, clientId);
        starter.putExtra(LibrarySocialLoginsConstants.KEY_REQUEST_STATE, state);
        starter.putExtra(LibrarySocialLoginsConstants.KEY_REDIRECT_URI, redirectUri);
        if (clientSecret != null) {
            starter.putExtra(LibrarySocialLoginsConstants.KEY_CLIENT_SECRET, clientSecret);
        }
        context.startActivity(starter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //this is to skip the "Cannot use web view in System process Exception"
        if (isFirstTimeRunning) {
            hookWebView();
            isFirstTimeRunning = false;
        }
        setContentView(R.layout.social_logins_lib_activity_web_view);
        //find view
        mProgressBar = findViewById(R.id.lib_social_log_in_activity_web_view_progress_bar);
        mWebView = findViewById(R.id.lib_social_log_in_activity_web_view_web_view);
        mIvClose = findViewById(R.id.lib_social_log_in_activity_web_view_iv_close);
        mIvBack = findViewById(R.id.lib_social_log_in_activity_web_view_iv_back);
        //init data
        mSocialProvider = getIntent().getIntExtra(LibrarySocialLoginsConstants.KEY_SOCIAL_PROVIDER, -1);
        mClientId = getIntent().getStringExtra(LibrarySocialLoginsConstants.KEY_CLIENT_ID);
        mRedirectUri = getIntent().getStringExtra(LibrarySocialLoginsConstants.KEY_REDIRECT_URI);
        mState = getIntent().getStringExtra(LibrarySocialLoginsConstants.KEY_REQUEST_STATE);
        mClientSecret = getIntent().getStringExtra(LibrarySocialLoginsConstants.KEY_CLIENT_SECRET);
        initWebView();

        //init listener
        initListeners();

        //https://www.cnblogs.com/baiqiantao/p/7249835.html 此处有坑
        switch (mSocialProvider) {
            case SOCIAL_PROVIDER_AMAZON:
                mAmazonSocialLoginModel = new AmazonSocialLoginModel();
                mAmazonSocialLoginModel.userSignIn(
                        mClientId,
                        mState,
                        mRedirectUri,
                        new LoadSocialLoginFrontPageListener() {
                            @Override
                            public void onGetLoginPageSrcCode(String htmlSrcCode) {
                                mWebView.loadDataWithBaseURL(
                                        null,
                                        htmlSrcCode,
                                        "text/html",
                                        "utf-8",
                                        null
                                );
                            }

                            @Override
                            public void onError(String msg) {
                                mGetPermissionListener.onError(msg);
                                finish();
                            }
                        }
                );
                break;
            case SOCIAL_PROVIDER_FACEBOOK:
                break;
            case SOCIAL_PROVIDER_GOOGLE:
                break;
            default:
                break;
        }
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
                if (!isFromBackPress) {
                    mHistory.add(url);
                } else {
                    isFromBackPress = false;
                }

                if (url.startsWith(mRedirectUri)) {
                    mProgressBar.setVisibility(View.GONE);
                    switch (mSocialProvider) {
                        case SOCIAL_PROVIDER_GOOGLE:
                            break;
                        case SOCIAL_PROVIDER_AMAZON:
                            AmazonAuthCode amazonAuthCode = genAmazonAuthCode(url);
                            mGetPermissionListener.onGetAmazonAuthCode(amazonAuthCode);
                            break;
                        case SOCIAL_PROVIDER_FACEBOOK:
                            break;
                        default:
                            break;
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
        if (mSocialProvider == BaseLoginManager.SOCIAL_PROVIDER_GOOGLE) {
            settings.setUserAgentString("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
        } else {
            settings.setUserAgentString(null);
        }

        //allow mix use of http and https
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

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
        mGetPermissionListener = null;
        //this is vital for facebook login
        CookieManager.getInstance().removeAllCookies(null);
        super.onDestroy();
    }


    /**
     * https://developer.amazon.com/docs/login-with-amazon/authorization-code-grant.html#access-token-response
     * @param url
     * @return
     */
    private AmazonAuthCode genAmazonAuthCode(String url) {
        AmazonAuthCode bean = null;
        try {
            URI uri = new URI(url);
            String query = uri.getQuery();
            String[] strings = query.split("&");
            bean = new AmazonAuthCode();
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
                        bean.setState(tempSplit[1]);
                        break;
                    case "error":
                        bean.setError(tempSplit[1]);
                        break;
                    case "error_description":
                        bean.setError_description(tempSplit[1]);
                        break;
                    case "error_uri":
                        bean.setError_uri(tempSplit[1]);
                        break;
                }
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
            mErrorMsg = e.getMessage();
            finish();
        }

        return bean;
    }


    public static void setGetPermissionListener(GetPermissionListener listener) {
        mGetPermissionListener = listener;
    }
}
