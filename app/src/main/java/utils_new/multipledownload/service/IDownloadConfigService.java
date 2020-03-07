package utils_new.multipledownload.service;

import java.util.List;

import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.config.DownloadConfig;
import utils_new.multipledownload.connection.DownloadConnection;

public interface IDownloadConfigService {
    void setConfig(DownloadConfig downloadConfig);

    int getMaxRunningTaskNumber();

    long getMinUsableSpace();

    List<DownloadInterceptor> getDownloadInterceptors();

    DownloadConnection.Factory getDownloadConnectionFactory();
}
