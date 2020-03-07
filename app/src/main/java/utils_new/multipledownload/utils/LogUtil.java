package utils_new.multipledownload.utils;

import android.util.Log;

public class LogUtil {
    private static String TAG = "CBOZipDownload";
    public static boolean mEnableLog = true;

    public static void e(String content) {
        if (mEnableLog) {
            Log.e(TAG, "CBOZipDownload " + content);
        }
    }

    public static void i(String content) {
        if (mEnableLog) {
            Log.i(TAG, "CBOZipDownload " + content);
        }
    }

    public static void d(String content) {
        if (mEnableLog) {
            Log.d(TAG, "CBOZipDownload " + content);
        }
    }

    public static void w(String content) {
        if (mEnableLog) {
            Log.w(TAG, "CBOZipDownload " + content);
        }
    }
}
