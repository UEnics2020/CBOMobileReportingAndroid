package utils_new.multipledownload.service;

import android.content.Context;

import java.io.File;
import java.util.List;

import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadRequest;

public interface IDownloadManager {
    void start(Context context);

    void submit(DownloadRequest downloadRequest);

    void deleteById(String id);

    void deleteByTag(String tag);
    void deleteInfoAll();

    void pause(String id);

    void stop(String id);

    void resume(String id);

    List<DownloadInfo> getDownloadingList();

    List<DownloadInfo> getDownloadedList();

    List<DownloadInfo> getDownloadListByTag(String tag);

    List<DownloadInfo> getAllDownloadList();

    DownloadInfo getDownloadInfoById(String id);

    boolean hasDownloadSucceed(String id);

    boolean isTaskRunning(String id);

    File getFileIfSucceed(String id);

    void shutdown();

    boolean isShutdown();

    Context getContext();
}
