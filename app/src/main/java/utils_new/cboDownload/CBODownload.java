package utils_new.cboDownload;

import android.os.Parcel;
import android.os.Parcelable;

public class CBODownload implements Parcelable {

    private int progress;
    private long currentFileSize;
    private long totalFileSize;

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getCurrentFileSize() {
        return currentFileSize;
    }

    public void setCurrentFileSize(long currentFileSize) {
        this.currentFileSize = currentFileSize;
    }

    public long getTotalFileSize() {
        return totalFileSize;
    }

    public void setTotalFileSize(long totalFileSize) {
        this.totalFileSize = totalFileSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.progress);
        dest.writeLong(this.currentFileSize);
        dest.writeLong(this.totalFileSize);
    }

    public CBODownload() {
    }

    protected CBODownload(Parcel in) {
        this.progress = in.readInt();
        this.currentFileSize = in.readLong();
        this.totalFileSize = in.readLong();
    }

    public static final Creator<CBODownload> CREATOR = new Creator<CBODownload>() {
        @Override
        public CBODownload createFromParcel(Parcel source) {
            return new CBODownload(source);
        }

        @Override
        public CBODownload[] newArray(int size) {
            return new CBODownload[size];
        }
    };
}