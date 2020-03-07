package utils_new.multipledownload.interceptor;

import android.os.Environment;
import android.util.Log;

import com.cbo.cbomobilereporting.MyCustumApplication;

import java.io.File;
import java.io.FilenameFilter;

import utils_new.cboDownload.CBOFileUtils;
import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.DownloadProvider;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.ErrorCode;
import utils_new.multipledownload.db.DBService;
import utils_new.multipledownload.task.DownloadTask;
import utils_new.multipledownload.utils.FileUtil;
import utils_new.multipledownload.utils.LogUtil;

import static utils_new.multipledownload.utils.Util.DOWNLOAD_PART;


public class MergeFileInterceptor implements DownloadInterceptor {
    private DownloadDetailsInfo downloadInfo;
    private DownloadRequest downloadRequest;

    @Override
    public DownloadInfo intercept(DownloadChain chain) {
        downloadRequest = chain.request();
        downloadInfo = downloadRequest.getDownloadInfo();
        DownloadTask downloadTask = downloadInfo.getDownloadTask();

        synchronized (downloadTask.getLock()) {
            long contentLength = downloadInfo.getContentLength();
            long completedSize = downloadInfo.getCompletedSize();
            if (!downloadInfo.isSupportBreakpoint()) {
                checkDownloadResult(contentLength, completedSize);
                return downloadInfo.snapshot();
            }

            File tempDir = downloadInfo.getTempDir();
            File[] downloadPartFiles = tempDir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.startsWith(DOWNLOAD_PART);
                }
            });
            if (contentLength > 0 && completedSize == contentLength && downloadPartFiles != null
                    && downloadPartFiles.length == downloadTask.getRequest().getThreadNum()) {
                File file = downloadInfo.getDownloadFile();
                downloadInfo.deleteDownloadFile();
                long startTime = System.currentTimeMillis();
                boolean mergeSuccess = false;
                if (downloadPartFiles.length == 1) {
                    if (FileUtil.renameTo(downloadPartFiles[0], file)) {
                        mergeSuccess = true;
                    }
                } else {
                    if (FileUtil.mergeFiles(downloadPartFiles, file)) {
                        mergeSuccess = true;
                    }
                }
                downloadInfo.deleteTempDir();
                if (mergeSuccess) {
                    LogUtil.d("Merge " + downloadInfo.getName() + " spend=" +
                            (System.currentTimeMillis() - startTime) + "; file.length=" + file.length());
                    checkDownloadResult(contentLength, completedSize);
                } else {
                    downloadInfo.setErrorCode(ErrorCode.MERGE_FILE_FAILED);
                }
            }
        }
        return downloadInfo.snapshot();
    }

    private void checkDownloadResult(long contentLength, long completedSize) {
        File downloadFile = downloadInfo.getDownloadFile();
        long downloadFileLength = downloadFile == null ? 0 : downloadFile.length();
        if (downloadFileLength == contentLength) {
            DownloadProvider.CacheBean cacheBean = downloadInfo.getCacheBean();
            if (cacheBean != null) {
                DBService.getInstance().updateCache(cacheBean);
            }
            downloadInfo.setFinished(1);
            downloadInfo.setStatus(DownloadInfo.Status.FINISHED);
            downloadInfo.setCompletedSize(completedSize);
            File file1 = new File(Environment.getExternalStorageDirectory(), "cbo/tiwari");
            File file2 = new File("" + downloadInfo.getFilePath());
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("FILE_SOURCE", downloadInfo.getName());
            CBOFileUtils.unzip(file2, file1);
//            Log.d("UnZip File", "" + file2);


        } else {
            downloadInfo.setErrorCode(ErrorCode.DOWNLOAD_FAILED);
        }
    }

}
