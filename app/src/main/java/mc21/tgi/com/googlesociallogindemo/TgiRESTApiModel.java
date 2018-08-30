package mc21.tgi.com.googlesociallogindemo;

import android.support.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import mc21.tgi.com.googlesociallogindemo.bean.TgiSocialLoginResponse;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TgiRESTApiModel {
    public void uploadToken(String token,@Nullable String googleIdToken, String provider, final SocialLoginListener listener) {
        OkHttpClient client = new OkHttpClient();
        FormBody formBody;
        if (provider.equals("google")) {
            formBody = new FormBody.Builder()
                    .add("provider", provider)
                    .add("token",token)
                    .add("idToken", googleIdToken)
                    .build();
        }else {
            formBody=new FormBody.Builder()
                    .add("provider", provider)
                    .add("token",token)
                    .build();
        }
        Request request = new Request.Builder()
                .url(CONSTANTS.TGI_REST_END_POINTS + "/users/social/login")
                .post(formBody)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                listener.onError("IOException :" + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String jason = response.body().string();
                listener.log(jason);
                TgiSocialLoginResponse bean = new Gson().fromJson(jason, TgiSocialLoginResponse.class);
                listener.onGetLoginResponse(bean);
            }
        });
    }

    public interface SocialLoginListener {
        void onError(String msg);

        void onGetLoginResponse(TgiSocialLoginResponse response);

        void log(String log);
    }
}
