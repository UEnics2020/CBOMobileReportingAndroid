package utils_new.multipledownload.interceptor;


import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.RealDownloadChain;

import static utils_new.multipledownload.ErrorCode.NETWORK_UNAVAILABLE;

public class RetryInterceptor implements DownloadInterceptor {
    private int retryUpperLimit;
    private int tryCount;
    private DownloadDetailsInfo downloadDetailsInfo;

    @Override
    public DownloadInfo intercept(DownloadChain chain) {
        RealDownloadChain realDownloadChain = (RealDownloadChain) chain;
        DownloadRequest downloadRequest = chain.request();
        downloadDetailsInfo = downloadRequest.getDownloadInfo();
        int retryDelay = downloadRequest.getRetryDelay();
        retryUpperLimit = downloadRequest.getRetryCount();
        DownloadInfo downloadInfo;
        boolean shouldRetry = false;
        while (true) {
            downloadInfo = realDownloadChain.proceed(downloadRequest, shouldRetry);
            shouldRetry = shouldRetry();
            if (shouldRetry) {
                tryCount++;
                downloadDetailsInfo.setStatus(DownloadInfo.Status.RUNNING);
                downloadDetailsInfo.clearErrorCode();
                if (retryDelay > 0) {
                    try {
                        Thread.sleep(retryDelay);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                break;
            }
        }
        return downloadInfo;
    }

    private boolean shouldRetry() {
        return downloadDetailsInfo.getErrorCode() == NETWORK_UNAVAILABLE
                && retryUpperLimit > tryCount;
    }
}
