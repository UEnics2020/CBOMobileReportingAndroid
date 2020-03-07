package utils_new.multipledownload.task;


import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;

import utils_new.multipledownload.CBOFactory;
import utils_new.multipledownload.DownloadDetailsInfo;
import utils_new.multipledownload.DownloadRequest;
import utils_new.multipledownload.ErrorCode;
import utils_new.multipledownload.connection.DownloadConnection;
import utils_new.multipledownload.service.IDownloadConfigService;

import static utils_new.multipledownload.utils.Util.DOWNLOAD_PART;

public class DownloadBlockTask extends Task {
    private DownloadConnection connection;
    private int blockId;
    private long completedSize;
    private File tempFile;
    private DownloadRequest downloadRequest;
    private DownloadDetailsInfo downloadInfo;

    public DownloadBlockTask(DownloadRequest downloadRequest, int blockId) {
        this.downloadRequest = downloadRequest;
        downloadInfo = downloadRequest.getDownloadInfo();
        connection = CBOFactory.getService(IDownloadConfigService.class).getDownloadConnectionFactory().create(downloadRequest.getUrl());
        this.blockId = blockId;
        calculateCompletedSize();
    }

    @Override
    public void cancel() {
        if (!connection.isCanceled()) {
            connection.cancel();
        }
    }

    @Override
    public void execute() {
        DownloadTask downloadTask = downloadInfo.getDownloadTask();
        long threadNum = downloadRequest.getThreadNum();
        long fileLength = downloadRequest.getDownloadInfo().getContentLength();
        long startPosition = blockId * fileLength / threadNum + completedSize;
        long endPosition;
        if (threadNum == blockId + 1) {
            endPosition = fileLength - 1;
        } else {
            endPosition = (blockId + 1) * fileLength / threadNum - 1;
        }

        if (startPosition != endPosition + 1) {
            connection.addHeader("Range", "bytes=" + startPosition + "-" + endPosition);
            try {
                connection.connect();
                int code = connection.getResponseCode();
                if (code == HttpURLConnection.HTTP_PARTIAL) {
                    int len;
                    connection.prepareDownload(tempFile);
                    byte[] buffer = new byte[8092];
                    while (!isCanceled() && (len = connection.downloadBuffer(buffer)) != -1) {
                        if (!downloadTask.onDownload(len)) {
                            break;
                        }
                    }
                    connection.flushDownload();
                } else {
                    downloadInfo.setErrorCode(ErrorCode.NETWORK_UNAVAILABLE);
                    downloadTask.cancel();
                }
            } catch (IOException e) {
                e.printStackTrace();
                downloadInfo.setErrorCode(ErrorCode.NETWORK_UNAVAILABLE);
            } finally {
                connection.close();
            }
        }
    }

    private void calculateCompletedSize() {
        File tempDir = downloadInfo.getTempDir();
        tempFile = new File(tempDir, DOWNLOAD_PART + blockId);
        if (tempFile.exists()) {
            completedSize = tempFile.length();
        } else {
            try {
                if (!tempDir.exists()) {
                    tempDir.mkdirs();
                }
                tempFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            completedSize = 0;
        }
    }

    public long getCompletedSize() {
        return completedSize;
    }

}
