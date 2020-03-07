package utils_new.multipledownload;



import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import utils_new.multipledownload.service.IDownloadManager;


public class RxPump {

    public static Observable<Boolean> deleteByTag(final String tag) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                CBOFactory.getService(IDownloadManager.class).deleteByTag(tag);
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    public static Observable<Boolean> deleteById(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                CBOFactory.getService(IDownloadManager.class).deleteById(id);
                e.onNext(true);
                e.onComplete();
            }
        });
    }

    public static Observable<List<DownloadInfo>> getAllDownloadList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getAllDownloadList());
                e.onComplete();
            }
        });
    }

    public static Observable<List<DownloadInfo>> getDownloadingList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getDownloadingList());
                e.onComplete();
            }
        });
    }

    public static Observable<List<DownloadInfo>> getDownloadedList() {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getDownloadedList());
                e.onComplete();
            }
        });
    }


    public static Observable<List<DownloadInfo>> getDownloadListByTag(final String tag) {
        return Observable.create(new ObservableOnSubscribe<List<DownloadInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<DownloadInfo>> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getDownloadListByTag(tag));
                e.onComplete();
            }
        });
    }


    public static Observable<DownloadInfo> getDownloadInfoById(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<DownloadInfo>() {
            @Override
            public void subscribe(ObservableEmitter<DownloadInfo> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getDownloadInfoById(id));
                e.onComplete();
            }
        });
    }


    public static Observable<Boolean> hasDownloadSucceed(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).hasDownloadSucceed(id));
                e.onComplete();
            }
        });
    }

    public static Observable<File> getFileIfSucceed(@NonNull final String id) {
        return Observable.create(new ObservableOnSubscribe<File>() {
            @Override
            public void subscribe(ObservableEmitter<File> e) {
                e.onNext(CBOFactory.getService(IDownloadManager.class).getFileIfSucceed(id));
                e.onComplete();
            }
        });
    }

}
