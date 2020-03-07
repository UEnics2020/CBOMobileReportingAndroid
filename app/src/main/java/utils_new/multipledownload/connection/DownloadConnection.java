package utils_new.multipledownload.connection;


import java.io.File;
import java.io.IOException;


public interface DownloadConnection {
    void addHeader(String key, String value);

    String getHeader(String key);

    void connect() throws IOException;

    void connect(String method) throws IOException;

    void prepareDownload(File file) throws IOException;

    int downloadBuffer(byte[] buffer) throws IOException;

    void flushDownload() throws IOException;

    int getResponseCode();

    boolean isSuccessful();

    void close();

    void cancel();

    boolean isCanceled();

    interface Factory {
        DownloadConnection create(String url);
    }
}
