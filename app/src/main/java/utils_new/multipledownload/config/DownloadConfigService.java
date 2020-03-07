package utils_new.multipledownload.config;



import java.util.Collections;
import java.util.List;

import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.connection.DownloadConnection;
import utils_new.multipledownload.connection.OkHttpDownloadConnection;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.utils.OKHttpUtil;

public class DownloadConfigService implements IDownloadConfigService {

    private int maxRunningTaskNumber = 3;
    private long minUsableStorageSpace = 4 * 1024L;
    private DownloadConfig downloadConfig;
    private DownloadConnection.Factory connectionFactory;
    private List<DownloadInterceptor> interceptors;

    private DownloadConfigService() {
    }

    @Override
    public void setConfig(DownloadConfig downloadConfig) {
        this.downloadConfig = downloadConfig;
    }

    public int getMaxRunningTaskNumber() {
        if (downloadConfig == null) {
            return maxRunningTaskNumber;
        }
        return downloadConfig.getMaxRunningTaskNumber();
    }

    public long getMinUsableSpace() {
        if (downloadConfig == null) {
            return minUsableStorageSpace;
        }
        return downloadConfig.getMinUsableSpace();
    }


    public List<DownloadInterceptor> getDownloadInterceptors() {
        if (downloadConfig == null) {
            interceptors = Collections.emptyList();
        }else{
            interceptors = downloadConfig.getInterceptors();
        }
        return interceptors;
    }

    @Override
    public DownloadConnection.Factory getDownloadConnectionFactory() {
        if (downloadConfig == null) {
            connectionFactory = new OkHttpDownloadConnection.Factory(OKHttpUtil.get());
        } else {
            connectionFactory = downloadConfig.getDownloadConnectionFactory();
        }
        return connectionFactory;
    }
}
