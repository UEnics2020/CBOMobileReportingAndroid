package utils_new.multipledownload.task;

import java.io.File;
import java.io.IOException;

import utils_new.multipledownload.CBOFactory;
import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.ErrorCode;
import utils_new.multipledownload.connection.DownloadConnection;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.utils.Util;


public class SimpleDownloadTask extends Task {
    private DownloadDetailsInfo downloadInfo;
    private DownloadConnection connection;

    public SimpleDownloadTask(DownloadRequest downloadRequest) {
        downloadInfo = downloadRequest.getDownloadInfo();
        connection = CBOFactory.getService(IDownloadConfigService.class).getDownloadConnectionFactory().create(downloadRequest.getUrl());
    }

    @Override
    public void execute() {
        DownloadTask downloadTask = downloadInfo.getDownloadTask();
        File downloadFile = new File(downloadInfo.getFilePath());
        try {
            if (downloadFile.createNewFile()) {
                connection.connect();

                long contentLength = Util.parseContentLength(connection.getHeader("Content-Length"));
                if (contentLength > 0) {
                    downloadInfo.setContentLength(contentLength);
                }
                if (connection.isSuccessful()) {
                    byte[] buffer = new byte[8092];
                    connection.prepareDownload(downloadFile);
                    int len;
                    while (!isCanceled() && (len = connection.downloadBuffer(buffer)) != -1) {
                        if (!downloadTask.onDownload(len)) {
                            break;
                        }
                    }
                    connection.flushDownload();
                    downloadInfo.setContentLength(downloadFile.length());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            downloadInfo.setErrorCode(ErrorCode.NETWORK_UNAVAILABLE);
        } finally {
            connection.close();
        }
    }

    @Override
    public void cancel() {
        if (!connection.isCanceled()) {
            connection.cancel();
        }
    }

}
