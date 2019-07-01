package com.hb.coop.di.module;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import androidx.annotation.Keep;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import com.hb.coop.BuildConfig;
import com.hb.coop.data.DataManager;
import com.hb.coop.utils.http.HttpHeaderInterceptor;
import com.hb.coop.utils.http.Tls12SocketFactory;
import com.hb.coop.utils.http.converter.gson.AppGsonConverterFactory;
import com.hb.lib.data.IDataManager;
import com.hb.lib.utils.Utils;
import dagger.Module;
import dagger.Provides;
import okhttp3.*;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

import javax.inject.Singleton;
import javax.net.ssl.*;
import java.lang.reflect.Field;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

/**
 * Created by haibt3 on 1/6/2017.
 */

@Module
public class NetModule {

    private static final long CONNECT_TIME_OUT = 60;
    private static final long READ_TIME_OUT = 60;

    static {
        System.loadLibrary("native-lib");
    }

    @Keep
    public native String getHost();


    @Provides
    @Singleton
    Cache provideHttpCache(Context context) {
        int cacheSize = 10 * 1024 * 1024;
        return new Cache(context.getCacheDir(), cacheSize);
    }

    @Provides
    @Singleton
    Gson provideGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);

        return gsonBuilder.create();
    }

    @Provides
    @Singleton
    Interceptor providesHttpHeaderInterceptor(Context context, IDataManager dataManager) {
        return new HttpHeaderInterceptor(context, (DataManager) dataManager);
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(Cache cache, Interceptor headerInterceptor) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(headerInterceptor);
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor((message) -> {
                Timber.i(message);
            });
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            builder.addInterceptor(interceptor);
        }
        builder.cache(cache);
        builder.connectionPool(new ConnectionPool(5, 5, TimeUnit.MINUTES));
        builder.connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(READ_TIME_OUT, TimeUnit.SECONDS);

        try {
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                    TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init((KeyStore) null);
            TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
            if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
                throw new IllegalStateException("Unexpected default trust managers:"
                        + Arrays.toString(trustManagers));
            }
            X509TrustManager trustManager = (X509TrustManager) trustManagers[0];

//            SSLContext sslContext = SSLContext.getInstance("TLS");
//            sslContext.init(null, new TrustManager[]{trustManager}, null);
//            SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            builder.sslSocketFactory(getSSLSocketFactory(), trustManager);
            builder.hostnameVerifier((s, sslSession) -> true);
        } catch (Exception e) {
            Timber.e(e.toString());
        }
        return enableTls12OnPreLollipop(builder).build();
    }

    void temp() {
        try {

            class TestObj {
                @SerializedName("key")
                public String key = "";
            }

            String test = new Gson().toJson(new TestObj());
            byte[] input = test.getBytes("utf-8");

            // Compress the bytes
            byte[] output = new byte[100];
            Deflater compresser = new Deflater();
            compresser.setInput(input);
            compresser.finish();
            int compressedDataLength = compresser.deflate(output);
            compresser.end();

//            String outputStr = new String(output, "UTF-8");
            String outputStr02 = Utils.INSTANCE.toHexadecimal(output, compressedDataLength);
            Timber.d("Encode 02: %s", outputStr02);

            // Decompress the bytes
            Inflater decompresser = new Inflater();
            decompresser.setInput(output, 0, compressedDataLength);
            byte[] result = new byte[100];
            int resultLength = decompresser.inflate(result);
            decompresser.end();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Singleton
    void zipData() {

    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(Gson gson, OkHttpClient client) {

        Retrofit retrofit = new Retrofit.Builder()
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(AppGsonConverterFactory.create(gson))
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl(getHost())
                .client(client)
                .build();

        return retrofit;
    }


//    private static class NullOnEmptyConverterFactory extends Converter.Factory {
//
//        @Override
//        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//            Timber.d("stringConverter");
//            return super.stringConverter(type, annotations, retrofit);
//        }
//
//        @Override
//        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
//            Timber.d("requestBodyConverter");
//            return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
//        }
//
//        @Override
//        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
//            Converter<ResponseBody, ?> delegate = retrofit.nextResponseBodyConverter(this, type, annotations);
//            return (body) -> {
//
//                if (String.class.equals(type)) {
//                    String text = new String(body.bytes());
//                    Timber.d("Test - %s", text);
//                    return text;
//                }
//
//                try {
//                    return delegate.convert(body);
//                } catch (EOFException e) {
//                    BaseResponse<Boolean> response = new BaseResponse<>();
//                    response.status = 200;
//                    response.data = true;
//                    return response;
//                }
//            };
//        }
//    }

    private static SSLSocketFactory getSSLSocketFactory() {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            return sslSocketFactory;
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            return null;
        }

    }

    private static OkHttpClient.Builder enableTls12OnPreLollipop(OkHttpClient.Builder client) {
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 22) {
            try {
                SSLContext sc = SSLContext.getInstance("TLSv1.2");
                sc.init(null, null, null);
                client.sslSocketFactory(new Tls12SocketFactory(sc.getSocketFactory()));

                ConnectionSpec cs = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                        .tlsVersions(TlsVersion.TLS_1_2)
                        .build();

                List<ConnectionSpec> specs = new ArrayList<>();
                specs.add(cs);
                specs.add(ConnectionSpec.COMPATIBLE_TLS);
                specs.add(ConnectionSpec.CLEARTEXT);

                client.connectionSpecs(specs);
            } catch (Exception exc) {
                Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc);
            }
        }

        return client;
    }

    public static void changeURL(Retrofit retrofit, String newUrl) {
        try {

            Field field = Retrofit.class.getDeclaredField("baseUrl");
            field.setAccessible(true);
            HttpUrl newHttpUrl = HttpUrl.parse(newUrl);
            field.set(retrofit, newHttpUrl);
        } catch (Exception e) {
            Timber.e("Change Url: %s", e.toString());
        }

    }
}
