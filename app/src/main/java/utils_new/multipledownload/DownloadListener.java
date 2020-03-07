package utils_new.multipledownload;

import android.text.TextUtils;

public class DownloadListener  {
    private String id;
    private DownloadInfo.Status status;
    private boolean enable;

    public DownloadListener() {
    }

    public final void disable() {
        CBOZipDownload.unSubscribe(this);
    }

    void setEnable(boolean enable) {
        this.enable = enable;
    }

    void setId(String id) {
        this.id = id;
    }

    public final void enable() {
        if (!enable) {
            CBOZipDownload.subscribe(this);
        }
    }

    public final boolean isEnable() {
        return enable;
    }

    private DownloadInfo downloadInfo;

    public final DownloadInfo.Status getStatus() {
        return status;
    }

    public final DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    final void downloading(DownloadInfo downloadInfo) {
        DownloadInfo.Status status = downloadInfo.getStatus();
        this.downloadInfo = downloadInfo;
        this.status = status;
        int progress = downloadInfo.getProgress();
        onProgress(progress);
        if (status == DownloadInfo.Status.FAILED) {
            onFailed();
            unSubscribe();
        } else if (downloadInfo.getStatus() == DownloadInfo.Status.FINISHED) {
            onSuccess();
            unSubscribe();
        }
    }
    private void unSubscribe(){
        if(id!=null){
            CBOZipDownload.unSubscribe(id);
        }
    }

    public String getId() {
        return id;
    }


    public boolean filter(DownloadInfo downloadInfo) {
        return id == null || id.equals(downloadInfo.getId());
    }

    public void onProgress(int progress) {
    }


    public void onSuccess() {
    }

    public void onFailed() {
    }

    @Override
    public int hashCode() {
        if (!TextUtils.isEmpty(id)) {
            return id.hashCode();
        }
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (DownloadListener.class.isAssignableFrom(obj.getClass())) {
            DownloadListener that = (DownloadListener) obj;
            String thatId = that.getId();
            if (!TextUtils.isEmpty(thatId) && thatId.equals(getId())) {
                return true;
            }
        }
        return super.equals(obj);
    }
}