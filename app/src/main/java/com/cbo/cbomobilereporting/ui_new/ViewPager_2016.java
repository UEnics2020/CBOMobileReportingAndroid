package com.cbo.cbomobilereporting.ui_new;


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.ui.Contact_Us;
import com.cbo.cbomobilereporting.ui_new.for_all_activities.FCustomWebView;
import com.cbo.cbomobilereporting.ui_new.report_activities.Dashboard.FDashboard;
import com.cbo.cbomobilereporting.ui_new.utilities_activities.VisualAdsDownload.VisualAdsDownloadActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.uenics.javed.CBOLibrary.Response;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import bill.Dashboard.FBillDashboard;
import de.hdodenhof.circleimageview.CircleImageView;
import services.Sync_service;
import utils.networkUtil.AppPrefrences;
import utils.networkUtil.NetworkUtil;
import utils_new.AppAlert;
import utils_new.CustomDatePicker;
import utils_new.Custom_Variables_And_Method;
import utils_new.Service_Call_From_Multiple_Classes;
import utils_new.multipledownload.CBOZipDownload;

public class ViewPager_2016 extends CustomActivity implements NavigationView.OnNavigationItemSelectedListener {

    final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private ViewPager viewPager;
    DrawerLayout drawer;
    Toolbar toolbar_;
    Custom_Variables_And_Method customVariablesAndMethod;
    FragmentManager fm;
    TextView hadder_text;
    CBO_DB_Helper cbo_db_helper;
    TextView maruee_text, emplyoyee_name, company_name, emp_desig, emp_hq, app_version;
    Bundle bundle;
    ArrayList<String> tabs;
    NetworkUtil networkUtil;
    TabLayout tabLayout;
    private Bitmap bmImg;
    private ImageView company_pic;
    private CircleImageView user_pic;
    private File USER_PIC, COMPANY_PIC;
    Boolean showProfile = false;
    LinearLayout Sync;
    public ProgressDialog progress1;

    String DashboardType = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_pager);
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
        bundle = savedInstanceState;
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout1);
        toolbar_ = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar_);
        networkUtil = new NetworkUtil(this);


        context = this;
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();

        androidx.appcompat.app.ActionBarDrawerToggle actionBarDrawerToggle = new androidx.appcompat.app.ActionBarDrawerToggle(this, drawer, toolbar_,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //Checking play service is available or not
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(getApplicationContext());

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        Sync = (LinearLayout) findViewById(R.id.Sync);
        progress1 = new ProgressDialog(this);

        //if play service is not available
        if (ConnectionResult.SUCCESS != resultCode) {
            //If play service is supported but not installed
            if (googleAPI.isUserResolvableError(resultCode)) {
                //Displaying message that play service is not installed
                customVariablesAndMethod.msgBox(context, "Google Play Service is not install/enabled in this device!");
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST, new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                finish();
                            }
                        }).show();

                //If play service is not supported
                //Displaying an error message
            } else {
                customVariablesAndMethod.msgBox(context, "This device does not support for Google Play Service!");
            }

            //If play service is available
        } else {
            Log.w("MainActivity", "hello");
        }

        cbo_db_helper = new CBO_DB_Helper(ViewPager_2016.this);

        CheckMenuPic(0);


    }


    private void CheckMenuPic(int times) {
        USER_PIC = new File(Environment.getExternalStorageDirectory() + "/cbo/profile/" + Custom_Variables_And_Method.PA_ID + ".jpg");
        COMPANY_PIC = new File(Environment.getExternalStorageDirectory() + "/cbo/profile/company.png");
        if (times == 0 && !customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "USER_PIC", "").isEmpty() && !USER_PIC.exists()) {
            if (!networkUtil.internetConneted(ViewPager_2016.this)) {
                customVariablesAndMethod.getAlert(this, "Turn ON your Internet", "Profile Pic download Pending...");
                initiate();
            } else {
                new GetProfilePicture().execute();
            }
        } else if (times == 0 && !customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "COMPANY_PIC", "").isEmpty() && !COMPANY_PIC.exists()) {
            if (!networkUtil.internetConneted(ViewPager_2016.this)) {
                customVariablesAndMethod.getAlert(this, "Turn ON your Internet", "Company Logo download Pending...");
                initiate();
            } else {
                new GetProfilePicture().execute();
            }
        } else if (cbo_db_helper.getmenu_count(CBO_DB_Helper.MenuControl) <= 0 ||
                cbo_db_helper.getMenu("UTILITY", "U_UPDOWN").size() == 0 ||
                MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("MENU_SYNC_FAILED", "N").equalsIgnoreCase("Y")) {
            //fmcg and menu not available
            //get fmcg and menu

            if (!networkUtil.internetConneted(ViewPager_2016.this)) {
                customVariablesAndMethod.getAlert(this, "Turn ON your Internet", "Menu not found. Turn NO your INTERNET and TRY AGAIN");
            } else {
                //new GetFMCGandMENU().execute();
                new Service_Call_From_Multiple_Classes().getListForLocal(context, new Response() {
                    @Override
                    public void onSuccess(Bundle bundle) {
                        initiate();
                    }

                    @Override
                    public void onError(String s, String s1) {

                    }
                });
            }

        } else {

            //fmcg and menu available
            initiate();
        }
    }

    private void initiate() {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        hadder_text = (TextView) findViewById(R.id.hadder_text);

        hadder_text.setText(cbo_db_helper.getCOMP_NAME());
        user_pic = navigationView.getHeaderView(0).findViewById(R.id.user_pic);
        company_pic = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.company_pic);
        emplyoyee_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emp_name_drawer);
        company_name = (TextView) navigationView.getHeaderView(0).findViewById(R.id.company_name_drawer);
        emp_desig = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emp_desig);
        emp_hq = (TextView) navigationView.getHeaderView(0).findViewById(R.id.emp_hq);
        app_version = (TextView) navigationView.getHeaderView(0).findViewById(R.id.app_version);
        maruee_text = (TextView) findViewById(R.id.viewpager_marquee);

        emplyoyee_name.setText(MyCustumApplication.getInstance().getUser().getName());
        company_name.setText(cbo_db_helper.getCOMP_NAME());
        emp_desig.setText(MyCustumApplication.getInstance().getUser().getDesgination());
        emp_hq.setText(MyCustumApplication.getInstance().getUser().getHQ());
        app_version.setText("Version : " + MyCustumApplication.getInstance().getUser().getAppVersion());

        if (USER_PIC.exists()) {
            Bitmap b = BitmapFactory.decodeFile(USER_PIC.getPath());
            user_pic.setImageBitmap(b);
        }

        if (COMPANY_PIC.exists()) {
            Bitmap b = BitmapFactory.decodeFile(COMPANY_PIC.getPath());
            company_pic.setImageBitmap(b);
        }

        user_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile = true;
                new GetProfilePicture().execute();
            }
        });
        company_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfile = true;
                new GetProfilePicture().execute();
            }
        });


        Sync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Custom_Variables_And_Method.internetConneted(context)) {
                    LocalBroadcastManager.getInstance(context).registerReceiver(mMessageReceiver, new IntentFilter("SyncComplete"));
                    Sync_service.ReplyYN = "Y";
                    progress1.setMessage("Please Wait..\n" +
                            " Fetching data");
                    progress1.setCancelable(false);
                    progress1.show();
                    Sync.setVisibility(View.GONE);
                    startService(new Intent(context, Sync_service.class));
                } else {
                    customVariablesAndMethod.Connect_to_Internet_Msg(context);
                }
            }
        });

        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "final_km", "");

        DashboardType = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DASHBOARD_TYPE", "");

        Menu menu = navigationView.getMenu();
        menu.removeGroup(R.id.group1);
        tabs = cbo_db_helper.getTab();
        if (DashboardType.equalsIgnoreCase("R")) {
            tabs.add(1, "BILL_DASHBOARD");
        } else if (DashboardType.equalsIgnoreCase("O")) {
            tabs.add(1, "DASHBOARD");
        }
        MenuItem menuItem;
        menuItem = menu.add(R.id.group1, 0, 0, "Home");
        menuItem.setIcon(R.drawable.ic_home_icon);
        /*if (menuItem.getIcon() != null) {
            menuItem.getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
        }*/
        for (int i = 1; i <= tabs.size(); i++) {
            switch (tabs.get(i - 1)) {
                case "DASHBOARD":
                    menuItem = menu.add(R.id.group1, i, i, "DASHBOARD");
                    menuItem.setIcon(R.drawable.ic_manager);
                    break;
                case "BILL_DASHBOARD":
                    menuItem = menu.add(R.id.group1, i, i, "DASHBOARD");
                    menuItem.setIcon(R.drawable.ic_manager);
                    break;
                case "DCR":
                    menuItem = menu.add(R.id.group1, i, i, "DCR");
                    menuItem.setIcon(R.drawable.ic_manager);
                    break;
                case "MAIL":
                    menuItem = menu.add(R.id.group1, i, i, "Mails");
                    menuItem.setIcon(R.drawable.ic_mail_icon);
                    break;
                case "TRANSACTION":
                    menuItem = menu.add(R.id.group1, i, i, "Transaction");
                    menuItem.setIcon(R.drawable.ic_transaction_icon);

                    break;
                case "REPORTS":
                    menuItem = menu.add(R.id.group1, i, i, "Reports");
                    menuItem.setIcon(R.drawable.ic_reports_icon);
                    break;
                case "UTILITY":
                    menuItem = menu.add(R.id.group1, i, i, "Utility");
                    menuItem.setIcon(R.drawable.ic_upload_download_icon);
                    break;
                case "PERSONAL_INFO":
                    menuItem = menu.add(R.id.group1, i, i, "Personal Info");
                    menuItem.setIcon(R.drawable.ic_userinfo_icon);
                    break;
                case "APPROVAL":
                    menuItem = menu.add(R.id.group1, i, i, "Approval");
                    menuItem.setIcon(R.drawable.ic_approved_icon);
                    break;

            }

            /*if (menuItem.getIcon() != null) {
                menuItem.getIcon().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
            }*/
        }

        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(1);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(true);
            }
        }

        SharedPreferences preft = getSharedPreferences(Custom_Variables_And_Method.FMCG_PREFRENCE, MODE_PRIVATE);
        String marQueeeText = preft.getString("mark", null);
        if (marQueeeText != null) {
            maruee_text.setVisibility(View.VISIBLE);
            maruee_text.setText(marQueeeText);

            maruee_text.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            maruee_text.setSelected(true);
            maruee_text.setSingleLine(true);
        } else {

            maruee_text.setVisibility(View.GONE);
        }

        fm = getSupportFragmentManager();
        setupViewPager(viewPager);


        Bundle b = getIntent().getExtras();
        if (b != null) {
            int id = b.getInt("Id");
            viewPager.setCurrentItem(id);
        } /*else if (DashboardType.isEmpty() || DashboardType.equalsIgnoreCase("N")
                || ((DashboardType.equalsIgnoreCase("R") || DashboardType.equalsIgnoreCase("O"))
                && !MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DASHBOARD_DATE", "")
                .equalsIgnoreCase(CustomDatePicker.currentDate()))) {
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("DASHBOARD_DATE", CustomDatePicker.currentDate());
            viewPager.setCurrentItem(1);
        } */ else {
            viewPager.setCurrentItem(0);
        }

        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //createTabIcons();
    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ArrayList<String> tabs = cbo_db_helper.getTab();
        if (DashboardType.equalsIgnoreCase("R")) {
            tabs.add(1, "BILL_DASHBOARD");
        } else if (DashboardType.equalsIgnoreCase("O")) {
            tabs.add(1, "DASHBOARD");
        }

        String url = "";
        for (int i = 0; i < tabs.size(); i++) {
            switch (tabs.get(i)) {
                case "DASHBOARD":
//                    adapter.addFragment(new FDashboard(),  "DASHBOARD");
//                    adapter.addFragment(new FCustomWebView(),  "DASHBOARD");

                    url = cbo_db_helper.getMenuUrl("REPORTS", "R_DASH");
                    if (url.isEmpty()) {
                        adapter.addFragment(new FDashboard(), "DASHBOARD");
                    } else {
                        FCustomWebView myFragment2 = new FCustomWebView();
                        Bundle data2 = new Bundle();//Use bundle to pass data
                        data2.putString("URL", url);
                        myFragment2.setArguments(data2);
                        adapter.addFragment(myFragment2, "DASHBOARD");
                    }
                    break;
                case "BILL_DASHBOARD":
                    url = cbo_db_helper.getMenuUrl("REPORTS", "R_DASHBOARD_SALES");
                    if (url.isEmpty()) {
                        adapter.addFragment(new FBillDashboard(), "DASHBOARD");
                    } else {
                        FCustomWebView myFragment2 = new FCustomWebView();
                        Bundle data2 = new Bundle();//Use bundle to pass data
                        data2.putString("URL", url);
                        myFragment2.setArguments(data2);
                        adapter.addFragment(myFragment2, "DASHBOARD");
                    }


                    break;
                case "DCR":
                    adapter.addFragment(new DcrmenuInGrid(), "DCR");
                    break;
                case "MAIL":
                    adapter.addFragment(new Mail_Screen(), "Mails");
                    break;
                /*case "TRANSACTION":
                    adapter.addFragment(new TransactionMenuInGrid(), "Transaction");
                    break;*/
                case "REPORTS":
                    adapter.addFragment(new ReportMenuInGrid(), "Reports");
                    break;
                case "UTILITY":
                    adapter.addFragment(new UtilitiesMenuInGrid(), "Utility");
                    break;
                case "PERSONAL_INFO":
                    adapter.addFragment(new PersonalMenuInGrid(), "PERSONAL INFO");
                    break;
                case "APPROVAL":
                    adapter.addFragment(new ApprovalMenuInGrid(), "APPROVAL");
                    break;
                default:
                    TransactionMenuInGrid myFragment = new TransactionMenuInGrid();
                    Bundle data = new Bundle();//Use bundle to pass data
                    data.putString("Code", tabs.get(i));
                    myFragment.setArguments(data);
                    adapter.addFragment(myFragment, tabs.get(i));

            }
        }

        viewPager.setAdapter(adapter);

    }

    public int getTabIndex() {

        return viewPager.getCurrentItem();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_view_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            logout();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {

        int itemId = menuItem.getItemId();
        switch (itemId) {


            case (R.id.nav_share): {
//            mycon.msgBox("Clicked On Share");
                String message = "https://play.google.com/store/apps/details?id=com.cbo.cbomobilereporting&hl=en";
                Intent share = new Intent(Intent.ACTION_SEND);
//             final List pakageAppList = getPackageManager().queryIntentActivities(share,0);
                share.setType("text/plain");
                share.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(share, "Share Your Application"));

                break;
            }
            case (R.id.nav_contact_us): {
                // mycon.msgBox("Clicked On Contact Us");
                startActivity(new Intent(getApplicationContext(), Contact_Us.class));
                break;
            }
            default: {
                if (itemId > 0)
                    itemId -= 1;
                Custom_Variables_And_Method.CURRENTTAB = itemId;
                viewPager.setCurrentItem(itemId);
                break;
            }
        }


        DrawerLayout drl = (DrawerLayout) findViewById(R.id.drawer_layout1);
        drl.closeDrawer(GravityCompat.START);


        return false;
    }

    @Override
    public void onBackPressed() {
        logout();
    }

    private void logout() {
        AppAlert.getInstance().setPositiveTxt("Yes").setNagativeTxt("No").DecisionAlert(context, "Exit!!!", "Are you sure to Exit?", new AppAlert.OnClickListener() {
            @Override
            public void onPositiveClicked(View item, String result) {
                CBOZipDownload.deleteByTag(MyCustumApplication.getInstance().getUser().getCompanyCode());
                MyCustumApplication.getInstance().Logout(context);
            }

            @Override
            public void onNegativeClicked(View item, String result) {

            }
        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
    }

    class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
            //return null;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        //startLoctionService();
        IsSyncReqd();
    }


    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            //String message = intent.getStringExtra("message");
            //Log.d("receiver", "Got message: " + message);
            IsSyncReqd();
        }
    };

    private void IsSyncReqd() {
        if (progress1 != null) {
            progress1.dismiss();
        }

        if (cbo_db_helper.get_Lat_Long_Reg("0").size() > 0) {
            //customVariablesAndMethod.getAlert(context,"Sync Reqd..","Data Avilable for sync");
            Sync.setVisibility(View.VISIBLE);
        } else {
            Sync.setVisibility(View.GONE);
        }
    }


    @Override
    protected void onDestroy() {
        // Unregister since the activity is about to be closed.
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        //stopLoctionService(false);
        super.onDestroy();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public String exists(String URLName) {
        int code = 0;
        HttpURLConnection connection = null;
        try {
            URL myurl = new URL(URLName);
            connection = (HttpURLConnection) myurl.openConnection();
            //Set request to header to reduce load as Subirkumarsao said.
            connection.setRequestMethod("HEAD");
            code = code + connection.getResponseCode();
            System.out.println("" + code);
        } catch (Exception e) {
            e.printStackTrace();
            code = code + 404;
        }
        return "" + code;
    }


    public void downloadToFolder(String fileUrl) {

        URL myFileUrl = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            myFileUrl = new URL(fileUrl);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        try {
            HttpURLConnection conn = (HttpURLConnection) myFileUrl
                    .openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            Log.i("im connected", fileUrl);
            bmImg = BitmapFactory.decodeStream(is);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.d("javed pdf test", e.toString());
        }
    }

    void saveImage(String url, File filename) {
        if (!exists(url).equals("404")) {
            try {
                //File filename;
                downloadToFolder(url);

                String path = Environment.getExternalStorageDirectory().toString();
                Log.i("in save()", "after mkdir");
                new File(path + "/cbo").mkdir();
                new File(path + "/cbo/profile").mkdir();
                //filename = new File(path + "/cbo/profile/" + Custom_Variables_And_Method.PA_ID + ".jpg");
                Log.i("in save()", "after file");
                //FileOutputStream out = new FileOutputStream(filename);
                Log.i("in save()", "after outputstream");
                FileOutputStream fo = new FileOutputStream(filename);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                if (filename.getAbsolutePath().contains(".png")) {
                    bmImg.compress(Bitmap.CompressFormat.PNG, 90, bytes);
                } else {
                    bmImg.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
                }
                fo.write(bytes.toByteArray());
                fo.flush();
                fo.close();
                Log.i("in save()", "after outputstream closed");
                MediaStore.Images.Media.insertImage(getContentResolver(),
                        filename.getAbsolutePath(), filename.getName(),
                        filename.getName());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private class GetProfilePicture extends AsyncTask<Void, Void, String> {
        ProgressDialog commitDialog;


        @Override
        protected String doInBackground(Void... voids) {
            new AppPrefrences(ViewPager_2016.this).setDataForFMCG();
            if (!customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "USER_PIC", "").isEmpty() && !USER_PIC.exists()) {
                saveImage(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "USER_PIC", ""), USER_PIC);
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "USER_PIC", "");
            }
            if (!customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "COMPANY_PIC", "").isEmpty() && !COMPANY_PIC.exists()) {
                saveImage(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "COMPANY_PIC", ""), COMPANY_PIC);
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "COMPANY_PIC", "");
            }


            return "done";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                commitDialog = new ProgressDialog(ViewPager_2016.this);
                commitDialog.setMessage("Please Wait..\nDownloading profile Picture...");
                commitDialog.setCanceledOnTouchOutside(false);
                commitDialog.setCancelable(false);
                commitDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (commitDialog != null && commitDialog.isShowing()) {
                commitDialog.dismiss();
            }
            CheckMenuPic(1);
            if (USER_PIC.exists()) {
                PreviewProfileImage(USER_PIC);
            }
            if (COMPANY_PIC.exists()) {
                PreviewProfileImage(COMPANY_PIC);
            }
        }
    }

    private void PreviewProfileImage(File file) {
        Bitmap b = BitmapFactory.decodeFile(file.getPath());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        // builder.setPositiveButton("OK", null);
        final AlertDialog dialog = builder.create();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.attachment_pop_up, null);
        ImageView attach_img = (ImageView) dialogLayout.findViewById(R.id.attach_img);
        ImageView close = (ImageView) dialogLayout.findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        if (file.exists()) {
            attach_img.setImageBitmap(b);
        } else {
            attach_img.setImageResource(R.drawable.cbo);
        }
        dialog.setView(dialogLayout);
        dialog.show();
        showProfile = false;
    }

    class GetFMCGandMENU extends AsyncTask<Void, Void, String> {
        ProgressDialog commitDialog;


        @Override
        protected String doInBackground(Void... voids) {
            new AppPrefrences(ViewPager_2016.this).setDataForFMCG();
            return "done";
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                commitDialog = new ProgressDialog(ViewPager_2016.this);
                commitDialog.setMessage("Please Wait..");
                commitDialog.setCanceledOnTouchOutside(false);
                commitDialog.setCancelable(false);
                commitDialog.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            commitDialog.dismiss();
            initiate();
        }
    }

}