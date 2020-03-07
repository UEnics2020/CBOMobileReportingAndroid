package utils_new.multipledownload.interceptor;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.text.format.Formatter;

import com.cbo.cbomobilereporting.MyCustumApplication;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;

import utils_new.multipledownload.CBOFactory;
import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadInfo;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.DownloadProvider;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.ErrorCode;
import utils_new.multipledownload.connection.DownloadConnection;
import utils_new.multipledownload.db.DBService;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.service.IDownloadManager;
import utils_new.multipledownload.task.DownloadTask;
import utils_new.multipledownload.utils.LogUtil;
import utils_new.multipledownload.utils.Util;

import static utils_new.multipledownload.utils.Util.DOWNLOAD_PART;


public class CheckCacheInterceptor implements DownloadInterceptor {
    private DownloadDetailsInfo downloadDetailsInfo;
    private DownloadRequest downloadRequest;
    private String lastModified;
    private String eTag;
    private static final int CONTENT_LENGTH_NOT_FOUND = -1;
    private DownloadTask downloadTask;

    @Override
    public DownloadInfo intercept(DownloadChain chain) {
        downloadRequest = chain.request();
        downloadDetailsInfo = downloadRequest.getDownloadInfo();
        downloadTask = downloadDetailsInfo.getDownloadTask();
        DownloadConnection connection = buildRequest(downloadRequest);
        int responseCode;
        long contentLength;
        try {
            connection.connect();
            String transferEncoding = connection.getHeader("Transfer-Encoding");
            lastModified = connection.getHeader("Last-Modified");
            setFilePathIfNeed(connection);
            eTag = connection.getHeader("ETag");
            String contentLengthField = connection.getHeader("Content-Length");
            downloadDetailsInfo.setMD5(connection.getHeader("Content-MD5"));
            responseCode = connection.getResponseCode();
            contentLength = getContentLength(connection);
            long originalContentLength = Util.parseContentLength(connection.getHeader("Content-Length"));
            if (responseCode == HttpURLConnection.HTTP_OK) {
                contentLength = originalContentLength;
            }
            if (contentLength == CONTENT_LENGTH_NOT_FOUND && isNeedHeadContentLength(contentLengthField, transferEncoding)) {
                contentLength = headContentLength();
            }
        } catch (IOException e) {
            e.printStackTrace();
            downloadDetailsInfo.setErrorCode(ErrorCode.NETWORK_UNAVAILABLE);
            return downloadDetailsInfo.snapshot();
        } finally {
            connection.close();
        }
        return parseResponse(chain, responseCode, contentLength);
    }

    private DownloadInfo parseResponse(DownloadChain chain, int responseCode, long contentLength) {
        if (responseCode >= 200 && responseCode < 300) {
            if (contentLength != CONTENT_LENGTH_NOT_FOUND) {
                long downloadDirUsableSpace = Util.getUsableSpace(new File(downloadDetailsInfo.getFilePath()));
                long dataFileUsableSpace = Util.getUsableSpace(Environment.getDataDirectory());
                long minUsableStorageSpace = CBOFactory.getService(IDownloadConfigService.class).getMinUsableSpace();
                if (downloadDirUsableSpace < contentLength * 2 || dataFileUsableSpace <= minUsableStorageSpace) {
                    //space is unusable.
                    downloadDetailsInfo.setErrorCode(ErrorCode.USABLE_SPACE_NOT_ENOUGH);
                    Context context = CBOFactory.getService(IDownloadManager.class).getContext();
                    String downloadFileAvailableSize = Formatter.formatFileSize(context, downloadDirUsableSpace);
                    LogUtil.e("Download directory usable space is " + downloadFileAvailableSize + ";but download file's contentLength is " + contentLength);
                    return downloadDetailsInfo.snapshot();
                } else {
                    downloadDetailsInfo.setCacheBean(new DownloadProvider.CacheBean(downloadRequest.getId(), lastModified, eTag));
                }
            }
            if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                downloadDetailsInfo.setSupportBreakpoint(contentLength != CONTENT_LENGTH_NOT_FOUND
                        && !downloadRequest.isDisableBreakPointDownload());
            } else {
                downloadDetailsInfo.setSupportBreakpoint(false);
            }
            checkDownloadFile(contentLength);
        } else if (responseCode == HttpURLConnection.HTTP_NOT_MODIFIED) {
            if (downloadDetailsInfo.isFinished()) {
                downloadDetailsInfo.setCompletedSize(downloadDetailsInfo.getContentLength());
                downloadDetailsInfo.setFinished(1);
                downloadDetailsInfo.setProgress(100);
                downloadDetailsInfo.setStatus(DownloadInfo.Status.FINISHED);
                downloadTask.updateInfo();
                return downloadDetailsInfo.snapshot();
            } else {
                checkDownloadFile(contentLength);
            }
        } else {
            if (responseCode == HttpURLConnection.HTTP_NOT_FOUND) {
                downloadDetailsInfo.setErrorCode(ErrorCode.FILE_NOT_FOUND);
            } else {
                downloadDetailsInfo.setErrorCode(ErrorCode.UNKNOWN_SERVER_ERROR);
            }
            return downloadDetailsInfo.snapshot();
        }
        return chain.proceed(downloadRequest);
    }

    private void checkDownloadFile(long contentLength) {
        File tempDir = downloadDetailsInfo.getTempDir();
        String[] childList = tempDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(DOWNLOAD_PART);
            }
        });
        if (childList != null && childList.length != downloadRequest.getThreadNum() && downloadDetailsInfo.isSupportBreakpoint()
                || contentLength != downloadDetailsInfo.getContentLength()
                || contentLength == CONTENT_LENGTH_NOT_FOUND || !downloadDetailsInfo.isSupportBreakpoint()) {
            downloadDetailsInfo.deleteTempDir();
        }
        downloadDetailsInfo.setContentLength(contentLength);
        downloadDetailsInfo.setFinished(0);
        downloadDetailsInfo.deleteDownloadFile();
        downloadTask.updateInfo();
    }

    private void setFilePathIfNeed(DownloadConnection connection) {
        if (downloadRequest.getFilePath() == null) {
            String fileName = Util.guessFileName(downloadRequest.getUrl(),
                    connection.getHeader("Content-Disposition"), connection.getHeader("Content-Type"));
            downloadRequest.setFilePath(Util.getCachePath(DownloadProvider.context) + "/" + fileName);
            downloadTask.updateInfo();
        }
    }

    private DownloadConnection buildRequest(DownloadRequest downloadRequest) {
        String url = downloadRequest.getUrl();
        DownloadConnection connection = createConnection(url);
        connection.addHeader("Range", "bytes=0-0");
        connection.addHeader("Authorization", "MOBILEREPORTING abcxyzsdfsdrwewer53345345sdwerwer234234") ;// <-- this is the important line
        connection.addHeader("Company-Code", MyCustumApplication.getInstance().getUser().getCompanyCode());
        connection.addHeader("Content-Type", "application/json");
        if (downloadRequest.getDownloadInfo().isFinished() && !downloadRequest.isForceReDownload()) {
            DownloadProvider.CacheBean cacheBean = DBService.getInstance().queryCache(url);
            if (cacheBean != null) {
                String eTag = cacheBean.eTag;
                String lastModified = cacheBean.lastModified;
                if (!TextUtils.isEmpty(lastModified)) {
                    connection.addHeader("If-Modified-Since", lastModified);
                }
                if (!TextUtils.isEmpty(eTag)) {
                    connection.addHeader("If-None-Match", eTag);
                }
            }

        }
        return connection;
    }

    private long getContentLength(DownloadConnection connection) {
        long contentLength = CONTENT_LENGTH_NOT_FOUND;
        String contentRange = connection.getHeader("Content-Range");
        if (contentRange != null) {
            final String[] session = contentRange.split("/");
            if (session.length >= 2) {
                try {
                    contentLength = Long.parseLong(session[1]);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return contentLength;
    }

    private boolean isNeedHeadContentLength(String contentLengthField, String transferEncoding) {
        if (transferEncoding != null && transferEncoding.equals("chunked")) {
            // because of the Transfer-Encoding can certain the result is right, so pass.
            return false;
        }

        if (contentLengthField == null || contentLengthField.length() <= 0) {
            return false;
        }
        return true;
    }

    private long headContentLength() {
        DownloadConnection connection = createConnection(downloadRequest.getUrl());
        try {
            connection.connect("HEAD");
            return Util.parseContentLength(connection.getHeader("Content-Length"));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return CONTENT_LENGTH_NOT_FOUND;
    }

    private DownloadConnection createConnection(String url) {
        return CBOFactory.getService(IDownloadConfigService.class).getDownloadConnectionFactory().create(url);
    }
}