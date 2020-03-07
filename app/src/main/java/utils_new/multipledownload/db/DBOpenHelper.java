package utils_new.multipledownload.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import utils_new.multipledownload.DownloadProvider;


public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context) {
        super(context, "cbovisual.db", null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE IF NOT EXISTS " + DownloadProvider.DownloadTable.TABLE_NAME + " ("
                + DownloadProvider.DownloadTable.URL + " CHAR,"
                + DownloadProvider.DownloadTable.PATH + " CHAR,"
                + DownloadProvider.DownloadTable.THREAD_NUM + " INTEGER,"
                + DownloadProvider.DownloadTable.FILE_LENGTH + " INTEGER,"
                + DownloadProvider.DownloadTable.FINISHED + " INTEGER,"
                + DownloadProvider.DownloadTable.CREATE_TIME + " INTEGER,"
                + DownloadProvider.DownloadTable.TAG + " CHAR,"
                + DownloadProvider.DownloadTable.ID + " CHAR primary key);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DownloadProvider.CacheTable.TABLE_NAME + " ("
                + DownloadProvider.CacheTable.URL + " CHAR primary key,"
                + DownloadProvider.CacheTable.ETAG + " CHAR,"
                + DownloadProvider.CacheTable.LAST_MODIFIED + " CHAR"
                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 0) {
            onCreate(db);
        } else {
            try {
                if (newVersion == 2) {
                    onCreate(db);
                } else if (newVersion == 3) {
                    db.execSQL("ALTER TABLE " + DownloadProvider.DownloadTable.TABLE_NAME + " ADD COLUMN " + DownloadProvider.DownloadTable.TAG + " CHAR default('');");
                    onCreate(db);
                } else if (newVersion == 4) {
                    if (oldVersion < 3) {
                        db.execSQL("ALTER TABLE " + DownloadProvider.DownloadTable.TABLE_NAME + " ADD COLUMN " + DownloadProvider.DownloadTable.TAG + " CHAR default('');");
                    }
                    String tempTable = DownloadProvider.DownloadTable.TABLE_NAME + "_temp";
                    //New temporary download table
                    db.execSQL("CREATE TABLE IF NOT EXISTS " + tempTable + " ("
                            + DownloadProvider.DownloadTable.URL + " CHAR,"
                            + DownloadProvider.DownloadTable.PATH + " CHAR,"
                            + DownloadProvider.DownloadTable.THREAD_NUM + " INTEGER,"
                            + DownloadProvider.DownloadTable.FILE_LENGTH + " INTEGER,"
                            + DownloadProvider.DownloadTable.FINISHED + " INTEGER,"
                            + DownloadProvider.DownloadTable.CREATE_TIME + " INTEGER,"
                            + DownloadProvider.DownloadTable.TAG + " CHAR,"
                            + DownloadProvider.DownloadTable.ID + " CHAR primary key);");

                    db.execSQL("INSERT INTO " + tempTable + " SELECT "
                            + DownloadProvider.DownloadTable.URL + ","
                            + DownloadProvider.DownloadTable.PATH + ","
                            + DownloadProvider.DownloadTable.THREAD_NUM + ","
                            + DownloadProvider.DownloadTable.FILE_LENGTH + ","
                            + DownloadProvider.DownloadTable.FINISHED + ","
                            + DownloadProvider.DownloadTable.CREATE_TIME + ","
                            + DownloadProvider.DownloadTable.TAG + ","
                            + DownloadProvider.DownloadTable.URL
                            + " FROM " + DownloadProvider.DownloadTable.TABLE_NAME + ";");

                    //Delete old download table
                    db.execSQL(String.format("DROP TABLE %s;", DownloadProvider.DownloadTable.TABLE_NAME));

                    //Rebuild the old download list and copy the temporary download list data
                    db.execSQL(String.format("CREATE TABLE %s AS SELECT * FROM %s", DownloadProvider.DownloadTable.TABLE_NAME, tempTable));

                    //Delete the temporary download table
                    db.execSQL(String.format("DROP TABLE %s", tempTable));
                    onCreate(db);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}