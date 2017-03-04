package com.jackfang.httpcorelibrary.http;

import com.google.gson.Gson;

import com.jackfang.httpcorelibrary.converters.CustomGsonConverterFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * Created by JackFang on 2017/3/4, 17:45.
 * Project: HttpCore
 * Email: jackfangqi1314@gmail.com
 */
public class RetrofitManager {
    private static final String TAG = "RetrofitManager";

    private static RetrofitManager sInstance;
    private Map<String, String> mHeaders = new HashMap<>();
    private String apiBaseUrl;
    private Gson gson;
    private OkHttpClient.Builder httpClient;
    private Retrofit.Builder retrofit;
    private Interceptor headerInterceptor;

    private RetrofitManager() {
        gson = new Gson();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);
        httpClient = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor);
    }

    public static RetrofitManager getInstance() {
        if (sInstance == null) {
            synchronized (RetrofitManager.class) {
                if (sInstance == null) {
                    sInstance = new RetrofitManager();
                }
            }
        }

        return sInstance;
    }

    public void init() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(apiBaseUrl)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(CustomGsonConverterFactory.create(gson));
        }
        if (mHeaders != null && mHeaders.size() > 0) {
            List<Interceptor> interceptors = httpClient.interceptors();
            if (interceptors != null) {
                interceptors.remove(headerInterceptor);
            }
            headerInterceptor = new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request.Builder builder = original.newBuilder();
                    for (Map.Entry<String, String> entry : mHeaders.entrySet()) {
                        builder.header(entry.getKey(), entry.getValue());
                    }
                    Request request = builder.method(original.method(), original.body()).build();
                    return chain.proceed(request);
                }
            };
            httpClient.addInterceptor(headerInterceptor);
        }
        if (isHttps()) {
            X509TrustManager trustManager;
            SSLSocketFactory sslSocketFactory;
            try {
                trustManager = HttpsCertUtil.trustManagerForCertificates(
                        HttpsCertUtil.trustedCertificatesInputStream());
                SSLContext sslContext = SSLContext.getInstance("TLS");
                sslContext.init(null, new TrustManager[]{trustManager}, null);
                sslSocketFactory = sslContext.getSocketFactory();
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            }
            httpClient.sslSocketFactory(sslSocketFactory, trustManager);
        }
    }

    public boolean isHttps() {
        return this.apiBaseUrl != null && this.apiBaseUrl.startsWith("https://");
    }

    public void setApiBaseUrl(String apiBaseUrl) {
        this.apiBaseUrl = apiBaseUrl;
    }

    public String getApiBaseUrl() {
        return apiBaseUrl;
    }

    public Map<String, String> getHeaders() {
        return mHeaders;
    }

    public OkHttpClient.Builder getHttpClient() {
        return httpClient;
    }

    public Retrofit.Builder getRetrofit() {
        return retrofit;
    }

    public void addHeader(String headerKey, String headerValue) {
        mHeaders.put(headerKey, headerValue);
    }

    public void addHeaders(Map<String, String> header) {
        mHeaders.putAll(header);
    }

    public void replaceHeader(String headerKey, String headerValue) {
        if (mHeaders.containsKey(headerKey))
            mHeaders.remove(headerKey);
        mHeaders.put(headerKey, headerValue);
        init();
    }

    public void replaceHeaders(Map<String, String> headers) {
        mHeaders.clear();
        mHeaders.putAll(headers);
        init();
    }

    public void replaceApiBaseUrl(String baseUrl) {
        this.apiBaseUrl = baseUrl;
        this.retrofit = null;
        init();
    }

    public <S> S createService(Class<S> serviceClass) {
        return retrofit.client(httpClient.build()).build().create(serviceClass);
    }
}
