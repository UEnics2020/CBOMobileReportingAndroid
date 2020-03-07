package utils_new.multipledownload;

public interface DownloadInterceptor {
    DownloadInfo intercept(DownloadChain chain);

    interface DownloadChain {
        DownloadRequest request();

        DownloadInfo proceed(DownloadRequest request);
    }
}
