package utils_new.multipledownload.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import utils_new.multipledownload.CBOFactory;
import utils_new.multipledownload.DownloadInterceptor;
import utils_new.multipledownload.connection.DownloadConnection;
import utils_new.multipledownload.connection.OkHttpDownloadConnection;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.utils.OKHttpUtil;

public class DownloadConfig {

    private int maxRunningTaskNumber = 3;

    private long minUsableStorageSpace = 4 * 1024L;

    private DownloadConnection.Factory connectionFactory;
    private List<DownloadInterceptor> interceptors = new ArrayList<>();

    private DownloadConfig() {
    }

    public int getMaxRunningTaskNumber() {
        return maxRunningTaskNumber;
    }

    public long getMinUsableSpace() {
        return minUsableStorageSpace;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public List<DownloadInterceptor> getInterceptors() {
        return Collections.unmodifiableList(interceptors);
    }

    public DownloadConnection.Factory getDownloadConnectionFactory() {
        return connectionFactory == null ? new OkHttpDownloadConnection.Factory(OKHttpUtil.get())
                : connectionFactory;
    }

    public static class Builder {
        private DownloadConfig downloadConfig;

        private Builder() {
            this.downloadConfig = new DownloadConfig();
        }


        public Builder setMaxRunningTaskNum(int maxRunningTaskNumber) {
            downloadConfig.maxRunningTaskNumber = maxRunningTaskNumber;
            return this;
        }

        public Builder setMinUsableStorageSpace(long minUsableStorageSpace) {
            downloadConfig.minUsableStorageSpace = minUsableStorageSpace;
            return this;
        }

        public Builder addDownloadInterceptor(DownloadInterceptor interceptor) {
            downloadConfig.interceptors.add(interceptor);
            return this;
        }

        public Builder setDownloadConnectionFactory(DownloadConnection.Factory factory) {
            downloadConfig.connectionFactory = factory;
            return this;
        }

        public void build() {
            CBOFactory.getService(IDownloadConfigService.class).setConfig(downloadConfig);
        }
    }
}
