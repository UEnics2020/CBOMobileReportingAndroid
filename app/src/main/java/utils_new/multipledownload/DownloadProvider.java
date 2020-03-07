package utils_new.multipledownload;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import utils_new.multipledownload.config.DownloadConfigService;
import utils_new.multipledownload.db.DBService;
import utils_new.multipledownload.service.IDownloadConfigService;
import utils_new.multipledownload.service.IDownloadManager;
import utils_new.multipledownload.service.IMessageCenter;
import utils_new.multipledownload.utils.OKHttpUtil;
import utils_new.multipledownload.utils.ReflectUtil;


public class DownloadProvider extends ContentProvider {
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static final String AUTHORITY_URI = "content://%s.huxq17.download-provider";
    public static Uri CONTENT_URI;

    public static Uri getContentUri(Context context) {
        if (CONTENT_URI == null) {
            CONTENT_URI = Uri.parse(String.format(AUTHORITY_URI, context.getPackageName()));
        }
        return CONTENT_URI;
    }

    @Override
    public boolean onCreate() {
        context = getContext();
        DBService.init(context);
        DownloadManager downloadManager = ReflectUtil.newInstance(DownloadManager.class);
        downloadManager.start(context);
        CBOFactory.addService(IDownloadManager.class, downloadManager);
        MessageCenter messageCenter = ReflectUtil.newInstance(MessageCenter.class);
        messageCenter.start(context);
        CBOFactory.addService(IMessageCenter.class, messageCenter);
        IDownloadConfigService downloadConfig = ReflectUtil.newInstance(DownloadConfigService.class);
        CBOFactory.addService(IDownloadConfigService.class, downloadConfig);
        OKHttpUtil.init(context);
        return true;
    }

    public static final class DownloadTable {
        public static final String TABLE_NAME = "download_info";
        public static final String ID = "id";
        public static final String URL = "url";
        public static final String PATH = "path";
        public static final String THREAD_NUM = "thread_num";
        public static final String FILE_LENGTH = "file_length";
        public static final String FINISHED = "finished";
        public static final String CREATE_TIME = "create_time";
        public static final String TAG = "tag";
    }

    public static final class CacheTable {
        public static final String TABLE_NAME = "download_cache";
        public static final String URL = "url";
        public static final String LAST_MODIFIED = "Last_modified";
        public static final String ETAG = "eTag";
    }

    public static final class CacheBean {
        public String lastModified;
        public String eTag;
        public String url;

        public CacheBean(String url, String lastModified, String eTag) {
            this.lastModified = lastModified;
            this.eTag = eTag;
            this.url = url;
        }

    }


    @Override
    public Cursor query( Uri uri,
                         String[] projection,  String selection, String[] selectionArgs, String sortOrder) {
        throw new SQLException("Not support to query.");
    }


    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete( Uri uri,  String selection,  String[] selectionArgs) {
        throw new SQLException("Not support to delete.");
    }

    @Override
    public int update( Uri uri,  ContentValues values,  String selection,  String[] selectionArgs) {
        throw new SQLException("Not support to update.");
    }

    private SQLiteDatabase getDatabase() {
        return DBService.getInstance().getWritableDatabase();
    }
}
