package utils_new.cboDownload;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

public class CBODownloadProgressInterceptor implements Interceptor {

    private CBODownloadProgressListener listener;

    public CBODownloadProgressInterceptor(CBODownloadProgressListener listener) {
        this.listener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new CBODownloadProgressResponseBody(originalResponse.body(), listener))
                .build();
    }
}
