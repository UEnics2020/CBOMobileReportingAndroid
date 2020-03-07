package services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;

import java.io.File;

import rx.Subscriber;
import utils_new.cboDownload.CBODownload;
import utils_new.cboDownload.CBODownloadAPI;
import utils_new.cboDownload.CBODownloadProgressListener;
import utils_new.cboDownload.CBOFileUtils;
import utils_new.cboDownload.CBOStringUtils;


public class CBODownloadService extends IntentService {
    private static final String TAG = "CBODownloadService";
    int downloadCount = 0;
    private NotificationCompat.Builder notificationBuilder;
    private NotificationManager notificationManager;
    //    private String urlAPI = "http://test.cboinfotech.co.in/apicustom/download/VISUALAID/ProductNew/zip";
    private String urlAPI = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("API", "");
//      public static String urlAPI = "";
    private File outputFile;

    public CBODownloadService() {
        super("CBODownloadService");

    }

    public void setUrlAPI(String urlAPI) {
//        this.urlAPI = urlAPI;
//        this.urlAPI = this.urlAPI  + urlAPI + "/zip";
    }

    // your Service.java
    public static boolean performTasks = true;

    @Override
    public void onHandleIntent(Intent intent) {


        if (intent != null) {

            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
            notificationBuilder = new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("Download")
                    .setContentText("Downloading File")
                    .setAutoCancel(true);
            notificationManager.notify(0, notificationBuilder.build());
            download();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


//        if (performTasks) {
//            notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            notificationBuilder = new NotificationCompat.Builder(this)
//                    .setSmallIcon(R.drawable.ic_download)
//                    .setContentTitle("Download")
//                    .setContentText("Downloading File")
//                    .setAutoCancel(true);
//            notificationManager.notify(0, notificationBuilder.build());
//            download();
//        }

    }

    private void download() {
        CBODownloadProgressListener listener = new CBODownloadProgressListener() {
            @Override
            public void update(long bytesRead, long contentLength, boolean done) {
                int progress = (int) ((bytesRead * 100) / contentLength);
                if ((downloadCount == 0) || progress > downloadCount) {
                    CBODownload download = new CBODownload();
                    download.setTotalFileSize(contentLength);
                    download.setCurrentFileSize(bytesRead);
                    if (progress != 100) {
                        download.setProgress(progress);
                    }
                    sendNotification(download);
                }
            }
        };
        outputFile = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_DOWNLOADS), "file.zip");

        if (outputFile.exists()) {
            outputFile.delete();
        }


        String baseUrl = CBOStringUtils.getHostName(this.urlAPI);

        new CBODownloadAPI(baseUrl, listener).downloadAPK(urlAPI, outputFile, new Subscriber() {
            @Override
            public void onCompleted() {
                downloadCompleted();
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
                sendError();
//                downloadCompleted();
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onNext(Object o) {
            }
        });
    }

    private void sendError() {

        CBODownload download = new CBODownload();
        download.setProgress(5000);
        sendIntent(download);
        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

    }

    private void downloadCompleted() {
        CBODownload download = new CBODownload();
        download.setProgress(100);
        sendIntent(download);


        notificationManager.cancel(0);
        notificationBuilder.setProgress(0, 0, false);
        notificationBuilder.setContentText("File Downloaded");
        notificationManager.notify(0, notificationBuilder.build());

//        Toast.makeText(getApplicationContext(), "Download Success", Toast.LENGTH_SHORT).show();
        File zfile = new File(Environment.getExternalStorageDirectory().toString() + "/download/file.zip");
        File tfile = new File(Environment.getExternalStorageDirectory().toString() + "/cbo");

        CBOFileUtils.unzip(zfile, tfile);
    }

    private void sendNotification(CBODownload download) {

        sendIntent(download);
        notificationBuilder.setProgress(100, download.getProgress(), false);
        notificationBuilder.setContentText(
                CBOStringUtils.getDataSize(download.getCurrentFileSize()) + "/" +
                        CBOStringUtils.getDataSize(download.getTotalFileSize()));
        notificationManager.notify(0, notificationBuilder.build());
    }

    private void sendIntent(CBODownload download) {
        Intent intent = new Intent("message_progress");
        intent.putExtra("download", download);
        LocalBroadcastManager.getInstance(CBODownloadService.this).sendBroadcast(intent);
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        notificationManager.cancel(0);
    }

}
