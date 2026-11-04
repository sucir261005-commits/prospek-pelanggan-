package com.neosantara.prospek.data;

import android.content.Context;
import android.text.TextUtils;

import com.neosantara.prospek.BuildConfig;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public final class ApiClient {
    private static ApiService service;

    private ApiClient() {
    }

    public static synchronized ApiService getService(Context context) {
        if (service == null) {
            Context applicationContext = context.getApplicationContext();
            SessionManager sessionManager = new SessionManager(applicationContext);
            OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request.Builder request = chain.request()
                        .newBuilder()
                        .header("Accept", "application/json");
                    String token = sessionManager.getToken();

                    if (!TextUtils.isEmpty(token)) {
                        request.header("Authorization", "Bearer " + token);
                    }

                    return chain.proceed(request.build());
                })
                .build();

            Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

            service = retrofit.create(ApiService.class);
        }

        return service;
    }
}
