package com.cbo.cbomobilereporting.ui_new.utilities_activities.VisualAdsDownload;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;

import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.uenics.javed.CBOLibrary.CBOServices;
import com.uenics.javed.CBOLibrary.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import services.MyAPIService;
import utils_new.AppAlert;
import utils_new.Custom_Variables_And_Method;
import utils_new.up_down_ftp;

public class VisualAdsDownloadActivity extends AppCompatActivity {

    Context context;
    ArrayList<mVisualAds> visualAds = new ArrayList<>();
    VisualAdsDownloadAdaptor visualAdsDownloadAdaptor;
    Custom_Variables_And_Method customVariablesAndMethod;
    RecyclerView listView;
    CBO_DB_Helper cbohelp;
    TextView percent, tname, comp_name, msg;
    ProgressBar pd;
    int PA_ID;
    int count = 0;
    String pa_name = "", msg_text;
    Button StartDownloading;
    LinearLayout DownloadProgess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //new ExceptionHandler(this);
        setContentView(R.layout.activity_visual_ads_download);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_hadder);
        TextView textView = (TextView) findViewById(R.id.hadder_text_1);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            textView.setText("VisualAid Download");
            getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }

        percent = findViewById(R.id.per);
        //update=(Button)findViewById(R.id.chk_update);
        pd = findViewById(R.id.pb);
        tname = findViewById(R.id.visual_empname);
        comp_name = findViewById(R.id.visual_compname);
        msg = findViewById(R.id.msgbox);
        DownloadProgess = findViewById(R.id.downloadProgress);
        StartDownloading = findViewById(R.id.startdownload);

        PA_ID = Custom_Variables_And_Method.PA_ID;
        pa_name = Custom_Variables_And_Method.PA_NAME;


        context = this;
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();


        tname.setText("Welcome  " + pa_name);
        comp_name.setText(Custom_Variables_And_Method.COMPANY_NAME);

        listView = findViewById(R.id.file_list);
        context = this;
        cbohelp = new CBO_DB_Helper(context);


        DownloadProgess.setVisibility(View.GONE);
        StartDownloading.setVisibility(View.VISIBLE);

        StartDownloading.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                StrartDownload(0);
            }
        });


        if (getIntent().getStringExtra("V_DOWNLOAD") != null) {
            if (getIntent().getStringExtra("V_DOWNLOAD").equalsIgnoreCase("Y")) {
                getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
                StrartDownload(0);
                //MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", ""));
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private void StrartDownload(int who) {
        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", cbohelp.getCompanyCode());
        request.put("iPA_ID", "" + Custom_Variables_And_Method.PA_ID);
        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);
        new MyAPIService(context)
                .execute(new ResponseBuilder("VISUALAID_DOWNLOAD", request)
                        .setMultiTable(false).setTables(tables).setDescription("Please Wait....\nProcessing downloadable files....").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle message) {

                                DownloadProgess.setVisibility(View.VISIBLE);
                                StartDownloading.setVisibility(View.GONE);

                                /*String visual_pdf= Custom_Variables_And_Method.getInstance().getDataFrom_FMCG_PREFRENCE(context,"VISUALAIDPDFYN");
                                String ext=".jpg";
                                if(visual_pdf.equals("Y")){
                                    ext=".pdf";
                                }*/
                                File f = new File(Environment.getExternalStorageDirectory(), "cbo/product");
                                f.deleteOnExit();
                                f.mkdir();
                                visualAdsDownloadAdaptor =
                                        new VisualAdsDownloadAdaptor(context, visualAds, (view, position, isLongClick) -> {
                                            /*Intent i = new Intent(context, CustomWebView.class);
                                            i.putExtra("A_TP", mApprovalRemainders.get(position).getADD_URL());
                                            i.putExtra("Title",  mApprovalRemainders.get(position).getPARICULARS());
                                            startActivity(i);*/
                                        });

                                visualAdsDownloadAdaptor.setListener(new VisualAdsDownloadAdaptor.DownloadListener() {
                                    @Override
                                    public void onSucess(int filesDownloaded, int totalFiles) {
                                        pd.setProgress((filesDownloaded * 100) / totalFiles);

                                        percent.setText(filesDownloaded + "/" + totalFiles + " downloaded");
                                        //msg.setText(msg_text);
                                        if (filesDownloaded == totalFiles) {
                                            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", ""));
                                            AppAlert.getInstance().Alert(context, "Download Complete",
                                                    "Visual Ads Downloaded Successfully....", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            finish();
                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onError(String message) {

                                    }
                                });
                                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                                listView.setLayoutManager(mLayoutManager);
                                listView.setItemAnimator(new DefaultItemAnimator());
                                listView.setAdapter(visualAdsDownloadAdaptor);
                                ViewCompat.setNestedScrollingEnabled(
                                        listView, false);
                            }

                            @Override
                            public void onResponse(Bundle response) {
                                parser_worktype(response);
                            }

                            @Override
                            public void onError(String s, String s1) {

                            }


                        })
                );
    }

    private void parser_worktype(Bundle result) {
        if (result != null) {

            try {

                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);
                visualAds.clear();
                ArrayList<up_down_ftp.mFTPFile> directoryFiles1 = null;
                while (directoryFiles1 == null) {
                    directoryFiles1 = new up_down_ftp().getDirectoryFiles(context, "");
                }
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject c = jsonArray1.getJSONObject(i);
                    mVisualAds visualAd = new mVisualAds(c.getString("ITEM_NAME"), c.getString("FILE_NAME"), c.getString("FOLDER_YN").equals("Y"));
                    visualAd.setDirectory("/visualaid");

                    if (visualAd.isFolderYN()) {
                        ArrayList<up_down_ftp.mFTPFile> directoryFiles = null;
                        while (directoryFiles == null) {
                            directoryFiles = new up_down_ftp().getDirectoryFiles(context, "/" + visualAd.getItemName());
                        }
                        //if (directoryFiles != null) {
                        for (up_down_ftp.mFTPFile directoryFile : directoryFiles) {
                            mVisualAds DvisualAd = new mVisualAds(directoryFile.getFileName(), directoryFile.getFileName(), false);
                            DvisualAd.setDirectory(directoryFile.getDirectory());
                            visualAds.add(DvisualAd);
                            // }
                        }
                    } else {
                        for (up_down_ftp.mFTPFile directoryFile : directoryFiles1) {

                            if (directoryFile.getFileName().contains(".")
                                    && visualAd.getFileName().equalsIgnoreCase(directoryFile.getFileName().substring(0, directoryFile.getFileName().lastIndexOf(".")))) {
                                mVisualAds visualAd1 = new mVisualAds(visualAd.getItemName(), visualAd.getFileName(), visualAd.isFolderYN());
                                visualAd1.setDirectory(visualAd.getDirectory());
                                visualAd1.setFileName(directoryFile.getFileName());
                                visualAds.add(visualAd1);

                            } else if (directoryFile.getFileName().contains("_")
                                    && visualAd.getFileName().equalsIgnoreCase(directoryFile.getFileName().substring(0, directoryFile.getFileName().lastIndexOf("_")))) {
                                {
                                    mVisualAds visualAd1 = new mVisualAds(visualAd.getItemName(), visualAd.getFileName(), visualAd.isFolderYN());
                                    visualAd1.setDirectory(visualAd.getDirectory());
                                    visualAd1.setFileName(directoryFile.getFileName());
                                    visualAds.add(visualAd1);
                                }
                            }
                        }
                    }
//                    if (visualAds.size()>0)
//                        break;
                }

            } catch (JSONException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Log.d("MYAPP", "objects are: " + e.toString());
                        AppAlert.getInstance().getAlert(context, "Missing field error", getResources().getString(R.string.service_unavilable) + e.toString());
                        e.printStackTrace();
                    }
                });

            }


        }


    }
}
