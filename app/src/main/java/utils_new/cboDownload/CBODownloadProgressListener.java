package utils_new.cboDownload;


public interface CBODownloadProgressListener {
    void update(long bytesRead, long contentLength, boolean done);
}
