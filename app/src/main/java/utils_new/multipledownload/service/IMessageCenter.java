package utils_new.multipledownload.service;

import android.content.Context;

import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadListener;

public interface IMessageCenter {
    void start(Context context);

    void register(DownloadListener downloadListener);

    void unRegister(String url);

    void unRegister(DownloadListener downloadListener);

    void notifyProgressChanged(DownloadDetailsInfo downloadInfo);

}
