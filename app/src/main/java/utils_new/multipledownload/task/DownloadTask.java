package utils_new.multipledownload.task;


import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import utils_new.multipledownload.CBOFactory;
import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.RealDownloadChain;
import utils_new.multipledownload.db.DBService;
import utils_new.multipledownload.interceptor.CheckCacheInterceptor;
import utils_new.multipledownload.interceptor.DownloadFetchInterceptor;
import utils_new.multipledownload.interceptor.MergeFileInterceptor;
import utils_new.multipledownload.interceptor.RetryInterceptor;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.service.IMessageCenter;

public class DownloadTask extends Task {
    private final DownloadDetailsInfo downloadInfo;
    private final Object lock;
    private DBService dbService;
    private IMessageCenter messageCenter;
    private int lastProgress;
    private DownloadRequest downloadRequest;
    private DownloadFetchInterceptor fetchInterceptor;

    public DownloadTask(DownloadRequest downloadRequest) {
        if (downloadRequest != null) {
            this.downloadRequest = downloadRequest;
            this.downloadInfo = downloadRequest.getDownloadInfo();
            lock = downloadInfo;
            downloadInfo.setDownloadTask(this);
            dbService = DBService.getInstance();
            messageCenter = CBOFactory.getService(IMessageCenter.class);
            downloadInfo.setErrorCode(0);
            downloadInfo.setStatus(DownloadInfo.Status.WAIT);
            downloadInfo.setCompletedSize(0);
            downloadInfo.setProgress(0);
            if (downloadInfo.getCompletedSize() == downloadInfo.getContentLength()
                    && downloadRequest.isForceReDownload()
                    || downloadRequest.isDisableBreakPointDownload()) {
                downloadInfo.deleteDownloadFile();
                downloadInfo.setCompletedSize(0);
                downloadInfo.setProgress(0);
                updateInfo();
            }
            notifyProgressChanged(downloadInfo);
        } else {
            downloadInfo = null;
            lock = null;
        }
    }

    public Object getLock() {
        return lock;
    }

    public DownloadRequest getRequest() {
        return downloadRequest;
    }

    public String getUrl() {
        return downloadRequest.getUrl();
    }

    public String getId() {
        return downloadRequest.getId();
    }

    public String getName() {
        String name = downloadRequest.getDownloadInfo().getName();
        if (TextUtils.isEmpty(name)) {
            name = downloadRequest.getName();
        }
        return name;
    }

    @Override
    public void execute() {
        if (isRunning()) {
            downloadInfo.setStatus(DownloadInfo.Status.RUNNING);
            notifyProgressChanged(downloadInfo);
            downloadWithDownloadChain();
            notifyProgressChanged(downloadInfo);
        }
    }

    private void downloadWithDownloadChain() {
        List<DownloadInterceptor> interceptors = new ArrayList<>(CBOFactory.getService(IDownloadConfigService.class)
                .getDownloadInterceptors());
        fetchInterceptor = new DownloadFetchInterceptor();
        interceptors.add(new RetryInterceptor());
        interceptors.add(new CheckCacheInterceptor());
        interceptors.add(fetchInterceptor);
        interceptors.add(new MergeFileInterceptor());
        RealDownloadChain realDownloadChain = new RealDownloadChain(interceptors, downloadRequest, 0);
        realDownloadChain.proceed(downloadRequest);
        synchronized (lock) {
            if (downloadInfo.getStatus() == DownloadInfo.Status.PAUSING) {
                downloadInfo.setStatus(DownloadInfo.Status.PAUSED);
            }
        }
        updateInfo();
    }

    boolean onDownload(int length) {
        synchronized (lock) {
            if (!isRunning()) {
                return false;
            }
            downloadInfo.download(length);
            int progress = (int) (downloadInfo.getCompletedSize() * 1f / downloadInfo.getContentLength() * 100);
            downloadInfo.setProgress(progress);
            if (progress != lastProgress) {
                if (progress != 100) {
                    lastProgress = progress;
                    notifyProgressChanged(downloadInfo);
                }
            }
        }
        return true;
    }

    public void notifyProgressChanged(DownloadDetailsInfo downloadInfo) {
        if (messageCenter != null)
            messageCenter.notifyProgressChanged(downloadInfo);
    }

    public DownloadDetailsInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void pause() {
        synchronized (lock) {
            if (isRunning()) {
                downloadInfo.setStatus(DownloadInfo.Status.PAUSING);
                notifyProgressChanged(downloadInfo);
                cancel();
            }
        }
    }

    public void stop() {
        synchronized (lock) {
            if (downloadInfo.getStatus().shouldStop()) {
                downloadInfo.setStatus(DownloadInfo.Status.STOPPED);
                cancel();
            }
        }
    }

    public void cancel() {
        if (fetchInterceptor != null) {
            fetchInterceptor.cancel();
        }
        if (currentThread != null) {
            currentThread.interrupt();
        }
    }

    public void updateInfo() {
        synchronized (lock) {
            dbService.updateInfo(downloadInfo);
        }
    }

    public boolean isRunning() {
        return currentThread != null && downloadInfo != null && downloadInfo.isRunning();
    }
}