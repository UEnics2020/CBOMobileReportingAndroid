package utils_new.cboUtils;


import android.content.Context;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkClient {
    //    private static final String BASE_URL = "http://192.168.0.166/api/";
    private static String BASE_URL = "http://test.cboinfotech.co.in/apicustom/api/";
    private static Retrofit retrofit;
    private static String AUTHORIZATION = "";
    private static String APP_VERSION = "";
    private static String MOBILE_ID = "";
    private static String IMEI = "";
    private static String COMPANY_CODE = "";
    private static String CONTENT_TYPE = "";


    public NetworkClient() {

    }

    private static Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    //Common Request Method

    public static Retrofit getRetrofitClient(Context context, String contentType) {

        AUTHORIZATION = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("API_TOKEN", "");
        BASE_URL = MyCustumApplication.getInstance()
                .getDataFrom_FMCG_PREFRENCE("API_URL_MOBILE", "");
//        BASE_URL = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("API_URL", "");
        COMPANY_CODE = MyCustumApplication.getInstance().getUser().getCompanyCode();
        CONTENT_TYPE = contentType;
        APP_VERSION = MyCustumApplication.getInstance().getUser().getAppVersion();
        MOBILE_ID = MyCustumApplication.getInstance().getUser().getIMEI();

//        AUTHORIZATION = " RXDR qwerpioq645ASEGWEs32498ASDrtqwegQ683123FwdThSaW6Wfds== ";
//        COMPANY_CODE = "Demotest";
        CONTENT_TYPE = "multipart/form-data";

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggingInterceptor())
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS);
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .header("Authorization", AUTHORIZATION) // <-- this is the important line
                            .header("Company-Code", COMPANY_CODE)
                            .header("Content-Type", CONTENT_TYPE)
                            .header("APP_VERSION", APP_VERSION)
                            .header("MOBILE_ID", MOBILE_ID);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);
                }
            });

            OkHttpClient client = httpClient.build();


            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }
        return retrofit;
    }


    static class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Interceptor.Chain chain) throws IOException {
            Request request = chain.request();
            long t1 = System.nanoTime();
            Response response = chain.proceed(request);
            long t2 = System.nanoTime();
            return response;
        }
    }

}
