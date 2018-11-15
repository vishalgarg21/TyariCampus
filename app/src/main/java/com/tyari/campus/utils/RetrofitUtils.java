package com.tyari.campus.utils;

import android.content.Context;
import android.text.TextUtils;

import com.tyari.campus.BuildConfig;
import com.tyari.campus.common.APIInterface;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class RetrofitUtils {
    private static final String KEY_LANGUAGE = "language";
    private static final String BASE_URL = "http://my-whish.club/tyari/";

    private static RetrofitUtils sInstance;

    private RetrofitUtils() {
    }

    public static RetrofitUtils getInstance() {
        if (sInstance == null) {
            sInstance = new RetrofitUtils();
        }
        return sInstance;
    }

    public APIInterface getService(Context context) {
        HttpLoggingInterceptor logging = null;
        if (BuildConfig.DEBUG) {
            logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        }

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new AddHeaderInterceptor(context))
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        return retrofit.create(APIInterface.class);
    }

    private static class AddHeaderInterceptor implements Interceptor {
        private Context mContext;

        public AddHeaderInterceptor(Context context) {
            super();
            mContext = context;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request.Builder builder = chain.request().newBuilder();

            String language = PreferenceUtils.getInstance(mContext).getString(PreferenceUtils.KEY_LANG);
            if (!TextUtils.isEmpty(language)) {
                builder.addHeader(KEY_LANGUAGE, language).build();
            }
            return chain.proceed(builder.build());
        }
    }
}
