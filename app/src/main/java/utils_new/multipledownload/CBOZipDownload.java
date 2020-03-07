package utils_new.multipledownload;


import java.io.File;
import java.util.List;

import utils_new.multipledownload.service.IDownloadManager;
import utils_new.multipledownload.service.IMessageCenter;

public class CBOZipDownload {

    public static DownloadRequest.DownloadGenerator newRequest(String url) {
        return newRequest(url, null);
    }


    public static DownloadRequest.DownloadGenerator newRequest(String url, String filePath) {
        return DownloadRequest.newRequest(url, filePath);
    }


    @Deprecated
    public static void download(String url, String filePath) {
        DownloadRequest.newRequest(url, filePath).submit();
    }


    public static void subscribe(DownloadListener downloadListener) {
        CBOFactory.getService(IMessageCenter.class).register(downloadListener);
    }


    public static void unSubscribe(String id) {
        CBOFactory.getService(IMessageCenter.class).unRegister(id);
    }


    public static void unSubscribe(DownloadListener downloadListener) {
        CBOFactory.getService(IMessageCenter.class).unRegister(downloadListener);
    }


    public static void pause(String id) {
        CBOFactory.getService(IDownloadManager.class).pause(id);
    }


    public static void stop(String id) {
        CBOFactory.getService(IDownloadManager.class).stop(id);
    }


    public static void deleteByTag(String tag) {
        CBOFactory.getService(IDownloadManager.class).deleteByTag(tag);
    }


    public static void deleteById(String id) {
        CBOFactory.getService(IDownloadManager.class).deleteById(id);
    }


    public static void resume(String id) {
        CBOFactory.getService(IDownloadManager.class).resume(id);
    }

    public static void shutdown() {
        CBOFactory.getService(IDownloadManager.class).shutdown();
    }


    public static List<DownloadInfo> getAllDownloadList() {
        return CBOFactory.getService(IDownloadManager.class).getAllDownloadList();
    }

    public static List<DownloadInfo> getDownloadingList() {
        return CBOFactory.getService(IDownloadManager.class).getDownloadingList();
    }

    public static List<DownloadInfo> getDownloadedList() {
        return CBOFactory.getService(IDownloadManager.class).getDownloadedList();
    }


    public static List<DownloadInfo> getDownloadListByTag(String tag) {
        return CBOFactory.getService(IDownloadManager.class).getDownloadListByTag(tag);
    }


    public static DownloadInfo getDownloadInfoById(String id) {
        return CBOFactory.getService(IDownloadManager.class).getDownloadInfoById(id);
    }


    public static boolean hasDownloadSucceed(String id) {
        return CBOFactory.getService(IDownloadManager.class).hasDownloadSucceed(id);
    }


    public static File getFileIfSucceed(String id) {
        return CBOFactory.getService(IDownloadManager.class).getFileIfSucceed(id);
    }

    public static void deleteInfoAll() {
        CBOFactory.getService(IDownloadManager.class).deleteInfoAll();
    }

}
