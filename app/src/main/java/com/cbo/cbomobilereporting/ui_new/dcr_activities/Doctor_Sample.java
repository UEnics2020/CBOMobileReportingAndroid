package com.cbo.cbomobilereporting.ui_new.dcr_activities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.databaseHelper.Call.Db.DrCallDB;
import com.cbo.cbomobilereporting.databaseHelper.Call.mDrCall;
import com.cbo.cbomobilereporting.databaseHelper.Location.LocationDB;
import com.cbo.cbomobilereporting.ui.PrescribeNew;
import com.cbo.cbomobilereporting.ui_new.AttachImage;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.Enum.CallType;
import com.cbo.cbomobilereporting.ui_new.utilities_activities.DocPhotos;
import com.cbo.cbomobilereporting.ui_new.utilities_activities.newDocPhoto.NewDocPhoto;
import com.cbo.myattachment.AttachFile;
import com.flurry.android.FlurryAgent;
import com.uenics.javed.CBOLibrary.CboProgressDialog;

import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import utils.adapterutils.ExpandableListAdapter;
import utils.adapterutils.SpinAdapter;
import utils.adapterutils.SpinnerModel;
import utils_new.AppAlert;
import utils_new.Custom_Variables_And_Method;
import utils_new.Dr_Gift_Dialog;
import utils_new.Dr_Sample_Dialog;
import utils_new.Service_Call_From_Multiple_Classes;
import utils_new.cboUtils.CBOImageView;
import utils_new.up_down_ftp;


public class Doctor_Sample extends AppCompatActivity implements up_down_ftp.AdapterCallback {
    private static final int PRODUCT_DILOG = 5, GIFT_DILOG = 6, MESSAGE_INTERNET_SEND_FCM = 0;
    Spinner dr_name;
    Button sample, gift, submit, later, pics, prescribed, btn_remark, dr_sale;
    Custom_Variables_And_Method customVariablesAndMethod;
    SpinAdapter adapter;
    String DCR_ID = "", dr_id = "";
    ResultSet rs;
    int PA_ID;
    String doc_name = "";
    CBO_DB_Helper cbohelp;
    double result, res_main;
    String name3 = "", name4 = "", name5 = "", name6 = "", name7 = "";
    String name = "", name2 = "", MyDoctor = "", resultList = "";
    ArrayList<String> dr_name1;
    ArrayList<SpinnerModel> mylist = new ArrayList<SpinnerModel>();
    HashMap<String, ArrayList<String>> doctor_list = new HashMap<>();
    Context context;
    TableLayout stk, gift_layout;
    LinearLayout call_layout;
    LinearLayout dr_remarkLayout;
    ExpandableListView summary_layout;
    Button tab_call, tab_summary;
    HashMap<String, HashMap<String, ArrayList<String>>> summary_list = new HashMap<>();
    HashMap<String, ArrayList<String>> doctor_list_summary = new HashMap<>();
    ExpandableListAdapter listAdapter;
    String sample_name = "", sample_pob = "", sample_sample = "", sample_noc = "", sample_rate;
    String gift_name = "", gift_qty = "", call = "N";
    String workwith1, workwith2, workwith34, loc, time;
    Boolean sample_added = false;
    Boolean gift_added = false;
    TextView remark;
    EditText dr_remark;
    ArrayList<String> remark_list;
    ImageView remark_img;
    AlertDialog myalertDialog = null;
    String Dr_sale_url = "", campaign = "";


    ///firebase DB
    mDrCall mdrCall;
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle b1;
            switch (msg.what) {
                case PRODUCT_DILOG:
                    b1 = msg.getData();
                    sample_added = true;
                    name = b1.getString("val");
                    //b1.getString("val");
                    name2 = b1.getString("val2");//pob qty
                    name7 = b1.getString("val3");//sample qty
                    result = b1.getDouble("resultpob");
                    resultList = b1.getString("resultList");
                    DecimalFormat f = new DecimalFormat("#.00");
                    String result3 = f.format(result);
                    res_main = Double.parseDouble(result3);

                    doctor_list = cbohelp.getCallDetail("tempdr", Custom_Variables_And_Method.DR_ID, "0");

                    if (!doctor_list.get("sample_name").get(0).equals("")) {
                        String[] sample_name1 = doctor_list.get("sample_name").get(0).split(",");
                        String[] sample_qty1 = doctor_list.get("sample_qty").get(0).split(",");
                        String[] sample_pob1 = doctor_list.get("sample_pob").get(0).split(",");
                        String[] sample_noc1 = doctor_list.get("sample_noc").get(0).split(",");

                        sample_name = doctor_list.get("sample_name").get(0);
                        sample_sample = doctor_list.get("sample_qty").get(0);
                        sample_pob = doctor_list.get("sample_pob").get(0);
                        sample_noc = doctor_list.get("sample_noc").get(0);

                        init(sample_name1, sample_qty1, sample_pob1, sample_noc1, null);
                    } else {
                        sample_added = false;
                        sample_name = "";
                        sample_pob = "";
                        sample_sample = "";
                        sample_noc = "";
                        stk.removeAllViews();
                    }

                    if (!doctor_list.get("gift_name").get(0).equals("")) {
                        gift_added = true;
                        String[] gift_name1 = doctor_list.get("gift_name").get(0).split(",");
                        String[] gift_qty1 = doctor_list.get("gift_qty").get(0).split(",");

                        gift_name = doctor_list.get("gift_name").get(0);
                        gift_qty = doctor_list.get("gift_qty").get(0);

                        init_gift(gift_layout, gift_name1, gift_qty1);
                    } else {
                        gift_added = false;
                        gift_name = "";
                        gift_qty = "";
                        gift_layout.removeAllViews();
                    }

                    mdrCall.setGift_name_Arr(gift_name)
                            .setGift_qty_Arr(gift_qty)
                            .setSample_name_Arr(sample_name)
                            .setSample_pob_Arr(sample_pob)
                            .setSample_qty_Arr(sample_sample);

                    mdrCall.setSample_noc_Arr(sample_noc);

                    break;
                case GIFT_DILOG:
                    b1 = msg.getData();
                    name3 = b1.getString("giftid");
                    name4 = b1.getString("giftqan");

                    doctor_list = cbohelp.getCallDetail("tempdr", Custom_Variables_And_Method.DR_ID, "0");

                    if (!doctor_list.get("sample_name").get(0).equals("")) {
                        sample_added = true;
                        String[] sample_name1 = doctor_list.get("sample_name").get(0).split(",");
                        String[] sample_qty1 = doctor_list.get("sample_qty").get(0).split(",");
                        String[] sample_pob1 = doctor_list.get("sample_pob").get(0).split(",");
                        String[] sample_noc1 = doctor_list.get("sample_noc").get(0).split(",");

                        sample_name = doctor_list.get("sample_name").get(0);
                        sample_sample = doctor_list.get("sample_qty").get(0);
                        sample_pob = doctor_list.get("sample_pob").get(0);
                        sample_noc = doctor_list.get("sample_noc").get(0);

                        init(sample_name1, sample_qty1, sample_pob1, sample_noc1, null);
                    } else {
                        sample_added = false;
                        sample_name = "";
                        sample_pob = "";
                        sample_sample = "";
                        stk.removeAllViews();
                    }


                    if (!doctor_list.get("gift_name").get(0).equals("")) {
                        gift_added = true;
                        String[] gift_name1 = doctor_list.get("gift_name").get(0).split(",");
                        String[] gift_qty1 = doctor_list.get("gift_qty").get(0).split(",");

                        gift_name = doctor_list.get("gift_name").get(0);
                        gift_qty = doctor_list.get("gift_qty").get(0);

                        init_gift(gift_layout, gift_name1, gift_qty1);
                    } else {
                        gift_added = false;
                        gift_name = "";
                        gift_qty = "";
                        gift_layout.removeAllViews();
                    }

                    mdrCall.setGift_name_Arr(gift_name)
                            .setGift_qty_Arr(gift_qty)
                            .setSample_name_Arr(sample_name)
                            .setSample_pob_Arr(sample_pob)
                            .setSample_qty_Arr(sample_sample);

                    mdrCall.setSample_noc_Arr(sample_noc);


                    break;
                case MESSAGE_INTERNET_SEND_FCM:
                    customVariablesAndMethod.msgBox(context, "Sample Saved Sucessfully....");
                    /*Intent intent = new Intent(getApplicationContext(), LoginFake.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();*/
                    MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
                    break;
                case 99:
                    if ((null != msg.getData())) {
                        customVariablesAndMethod.msgBox(context, msg.getData().getString("Error"));
                        //Toast.makeText(getApplicationContext(),msg.getData().getString("Error"),Toast.LENGTH_SHORT).show();
                    }
                    break;


            }
        }
    };
    DrCallDB drCallDB;
    LocationDB locationDB;
    CBOImageView attachment;
    CboProgressDialog cboProgressDialog = null;

    public int getSplId(String id) {
        int splid = 0;

        Cursor c = cbohelp.getDoctorSpecialityIdByDrId(id);
        if (c.moveToFirst()) {
            do {
                splid = c.getInt((c.getColumnIndex("spl_id")));
            } while (c.moveToNext());
        }
        cbohelp.close();


        return splid;
    }

    public void onCreate(Bundle b) {
        super.onCreate(b);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));

        setContentView(R.layout.doctor_sample);
        FlurryAgent.logEvent("Doctor_Sample");

        androidx.appcompat.widget.Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_hadder);
        TextView hader_text = (TextView) findViewById(R.id.hadder_text_1);
        setSupportActionBar(toolbar);
        hader_text.setText("Doctor Sample");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }
        context = this;
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();

        prescribed = (Button) findViewById(R.id.bt_prescribed);
        dr_sale = findViewById(R.id.bt_Dr_Sale);
        dr_name = (Spinner) findViewById(R.id.drname_sample);
        sample = (Button) findViewById(R.id.sample);
        gift = (Button) findViewById(R.id.gift);
        cbohelp = new CBO_DB_Helper(getApplicationContext());
        pics = (Button) findViewById(R.id.pics);
        submit = (Button) findViewById(R.id.dr_submit_sample);
        later = (Button) findViewById(R.id.later);
        remark = (TextView) findViewById(R.id.remak);

        stk = (TableLayout) findViewById(R.id.promotion);
        gift_layout = (TableLayout) findViewById(R.id.gift_layout);
        dr_remark = (EditText) findViewById(R.id.dr_remark_edit);
        dr_remarkLayout = (LinearLayout) findViewById(R.id.dr_remark_layout);
        dr_remarkLayout.setVisibility(View.GONE);
        dr_remark.setVisibility(View.GONE);

        btn_remark = (Button) findViewById(R.id.remark);
        btn_remark.setText("---Select Remak---");

        attachment = findViewById(R.id.attachment);


        attachment.setTitle("Signature");
        attachment.setMaxAttachment(1);
        if (!MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DR_SIGNATURE_YN", "N").equalsIgnoreCase("Y")) {
            attachment.setVisibility(View.GONE);
        }


        attachment.setListener(new CBOImageView.iCBOImageView() {
            @Override
            public void OnAddClicked() {
                attachment.addAttachment((AppCompatActivity) context, AttachImage.ChooseFrom.Signature);

            }

            @Override
            public void OnAdded() {
                OnUpdated(attachment.getDataList());
            }

            @Override
            public void OnDeleted(String file) {
                OnUpdated(attachment.getDataList());
            }

            @Override
            public void OnUpdated(ArrayList<String> files) {
                //later.setVisibility(files.size()>0?View.GONE:View.VISIBLE);
                cbohelp.updateDrFile(Custom_Variables_And_Method.DR_ID, attachment.getAttachmentStr());

            }
        });
        drCallDB = new DrCallDB();
        locationDB = new LocationDB();

        DCR_ID = Custom_Variables_And_Method.DCR_ID;
        PA_ID = Custom_Variables_And_Method.PA_ID;

        if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCR_DR_REMARKYN").equalsIgnoreCase("y")) {
            dr_remarkLayout.setVisibility(View.VISIBLE);
        }

        if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "SAMPLE_POB_MANDATORY").equals("N")) {
            Custom_Variables_And_Method.SAMPLE_POB_MANDATORY = "N";
        } else {
            Custom_Variables_And_Method.SAMPLE_POB_MANDATORY = "Y";
            later.setVisibility(View.GONE);
        }

        if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCRSAMPLE_AFTERVISUALAIDYN").equalsIgnoreCase("Y")) {
            later.setVisibility(View.GONE);
        } else {
            later.setVisibility(View.VISIBLE);

        }

        prescribed.setVisibility(View.GONE);
        Dr_sale_url = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DR_SALE_URL", "");
        if (Dr_sale_url.trim().isEmpty()) {
            dr_sale.setVisibility(View.GONE);
        }

        call_layout = (LinearLayout) findViewById(R.id.call_layout);
        summary_layout = (ExpandableListView) findViewById(R.id.summary_layout);
        tab_call = (Button) findViewById(R.id.call);
        tab_summary = (Button) findViewById(R.id.summary);

        Custom_Variables_And_Method.VISUAL_REQUIRED = cbohelp.getVisualDetail();


        if (!Custom_Variables_And_Method.VISUAL_REQUIRED.equals("Y")) {
            pics.setVisibility(View.GONE);
        }


        getDoctorList();
        remark_list = cbohelp.get_Doctor_Call_Remark();

        Intent intent = getIntent();
        remark.setText("");
        if (intent.getStringExtra("id") != null) {
            call = "Y";
            for (int i = 0; i < mylist.size(); i++) {
                if (mylist.get(i).getId().equals(intent.getStringExtra("id").trim())) {
                    dr_name.setSelection(i);
                    if (intent.getStringExtra("remark") != null) {
                        remark.setText(intent.getStringExtra("remark"));
                    }
                }
            }
        }

        ImageView imgSpin = (ImageView) findViewById(R.id.spinner_img_doctor_sample);
        imgSpin.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dr_name.performClick();
            }
        });


        btn_remark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDrCallRemark();
            }
        });
        remark_img = (ImageView) findViewById(R.id.remark_img);
        remark_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickDrCallRemark();
            }
        });
        dr_name.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {

                dr_id = ((TextView) arg1.findViewById(R.id.spin_id)).getText().toString();
                doc_name = ((TextView) arg1.findViewById(R.id.spin_name)).getText().toString();
                String dr[] = doc_name.split(",");


                mdrCall = (mDrCall) new mDrCall()
                        .setId(dr_id)
                        .setName(doc_name)
                        .setDcr_id(MyCustumApplication.getInstance().getUser().getDCRId())
                        .setDcr_date(MyCustumApplication.getInstance().getUser().getDCRDate());


                String dr1 = dr[0];
                Custom_Variables_And_Method.DR_ID = dr_id.trim();
                Custom_Variables_And_Method.DR_NAME = dr1;
                Custom_Variables_And_Method.DOCTOR_SPL_ID = getSplId(Custom_Variables_And_Method.DR_ID);
                if (remAdded().contains(Custom_Variables_And_Method.DR_ID)) {
                    doctor_list = cbohelp.getCallDetail("tempdr", Custom_Variables_And_Method.DR_ID, "0");

                    submit.setText("Update");

                   /* if (!doctor_list.get("remark").get(0).equals("")) {
                        dr_remark.setText(doctor_list.get("remark").get(0));
                    } else {
                        dr_remark.setText("");
                    }*/

                    campaign = doctor_list.get("dr_camp_group").get(0);
                    if (!doctor_list.get("remark").get(0).equals("")) {
                        String remark = doctor_list.get("remark").get(0);
                        if (remark.contains("\u20B9")) {
                            if (remark.split("\n").length > 1) {
                                remark = remark.split("\n")[1];
                                if (remark_list.contains(remark)) {
                                    // btn_remark.setText(remark);
                                    dr_remark.setVisibility(View.GONE);
                                } else {
                                    remark = "";
                                    btn_remark.setText(remark_list.get(remark_list.size() - 1));
                                    dr_remark.setVisibility(View.VISIBLE);
                                }

                            } else {
                                remark = "";
                                btn_remark.setText("---Select Remak---");
                                dr_remark.setVisibility(View.GONE);
                            }

                        }
                        dr_remark.setText(remark);

                    } else {
                        dr_remark.setText("");
                        btn_remark.setText("---Select Remak---");
                        dr_remark.setVisibility(View.GONE);
                    }

                    if (!doctor_list.get("sample_name").get(0).equals("")) {
                        sample_added = true;
                        String[] sample_name1 = doctor_list.get("sample_name").get(0).split(",");
                        String[] sample_qty1 = doctor_list.get("sample_qty").get(0).split(",");
                        String[] sample_pob1 = doctor_list.get("sample_pob").get(0).split(",");
                        String[] sample_noc1 = doctor_list.get("sample_noc").get(0).split(",");
                        String[] sample_rate1 = doctor_list.get("sample_rate").get(0).split(",");

                        sample_name = doctor_list.get("sample_name").get(0);
                        sample_sample = doctor_list.get("sample_qty").get(0);
                        sample_pob = doctor_list.get("sample_pob").get(0);
                        sample_noc = doctor_list.get("sample_noc").get(0);
                        String[] sample_rate = null;
                       /* if (childText.containsKey("sample_rate")) {
                            sample_rate = childText.get("sample_rate").get(childPosition).isEmpty()? null : childText.get("sample_rate").get(childPosition).split(",");
                        }*/
                        init(sample_name1, sample_qty1, sample_pob1, sample_noc1, null);
                    } else {
                        sample_added = false;
                        sample_name = "";
                        sample_sample = "";
                        sample_pob = "";
                        sample_noc = "";

                        stk.removeAllViews();
                    }
                    if (!doctor_list.get("gift_name").get(0).equals("")) {
                        gift_added = true;

                        String[] gift_name1 = doctor_list.get("gift_name").get(0).split(",");
                        String[] gift_qty1 = doctor_list.get("gift_qty").get(0).split(",");

                        gift_name = doctor_list.get("gift_name").get(0);
                        gift_qty = doctor_list.get("gift_qty").get(0);

                        init_gift(gift_layout, gift_name1, gift_qty1);
                    } else {
                        gift_added = false;
                        gift_name = "";
                        gift_qty = "";
                        gift_layout.removeAllViews();
                    }
                    attachment.setAttachment(cbohelp.getFILE_Doctor(dr_id));
                } else {
                    sample_added = false;
                    gift_added = false;
                    sample_name = "";
                    sample_sample = "";
                    sample_pob = "";
                    sample_noc = "";

                    gift_name = "";
                    gift_qty = "";
                    campaign = "";

                    stk.removeAllViews();
                    gift_layout.removeAllViews();

                    attachment.setAttachment("");
                }

                mdrCall.setGift_name_Arr(gift_name)
                        .setGift_qty_Arr(gift_qty)
                        .setSample_name_Arr(sample_name)
                        .setSample_pob_Arr(sample_pob)
                        .setSample_qty_Arr(sample_sample);

                mdrCall.setSample_noc_Arr(sample_noc);

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });


        pics.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (mylist.isEmpty()) {
                    customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                }
                if (dr_id.equals("0")) {
                    customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                } else {
//                    Intent i = new Intent(Doctor_Sample.this, DocPhotos.class);

                    if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DCRSAMPLE_AFTERVISUALAIDYN", "N").equalsIgnoreCase("Y")) {
                        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("utility", "");
                        Intent i = new Intent(Doctor_Sample.this, NewDocPhoto.class);
                        i.putExtra("who", 0);
                        i.putExtra("dr_id", dr_id);
                        i.putExtra("sample_name", sample_name);
                        i.putExtra("sample_pob", sample_pob);
                        i.putExtra("sample_sample", sample_sample);
                        // startActivity(i);
                        startActivityForResult(i, 0);
//                    startActivity(i);
                    } else {
                        Intent i = new Intent(Doctor_Sample.this, DocPhotos.class);
                        i.putExtra("who", 0);
                        i.putExtra("dr_id", dr_id);
                        i.putExtra("sample_name", sample_name);
                        i.putExtra("sample_pob", sample_pob);
                        i.putExtra("sample_sample", sample_sample);
                        // startActivity(i);
                        startActivityForResult(i, 0);
//                    startActivity(i);
                    }


                }
            }
        });

        if (customVariablesAndMethod.IsProductEntryReq(context, CallType.DOCTOR) ||
                customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DRGIFTMANDATORY").contains("D")) {
            String GiftCaption = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "GIFT_BTN_CAPTION", "");
            if (!GiftCaption.isEmpty())
                gift.setText(GiftCaption);

            gift.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub

                    if (mylist.isEmpty()) {
                        customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                    }
                    if (dr_id.equals("0")) {
                        customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                    } else {

                   /* Intent i = new Intent(Doctor_Sample.this, Dr_Gift.class);
                    i.putExtra("intent_fromRcpaCAll","dr");
                    i.putExtra("gift_name",gift_name);
                    i.putExtra("gift_qty",gift_qty);
                      startActivityForResult(i, 1);*/

                        Bundle b = new Bundle();
                        b.putString("intent_fromRcpaCAll", "dr");
                        b.putString("title", gift.getText().toString());
                        b.putString("gift_name", gift_name);
                        b.putString("gift_qty", gift_qty);
                        b.putString("campaign", campaign);
                        new Dr_Gift_Dialog(context, mHandler, b, GIFT_DILOG).Show();
                    }
                }
            });
        } else {
            gift.setVisibility(View.GONE);
        }

        if (customVariablesAndMethod.IsProductEntryReq(context, CallType.DOCTOR)) {

            String ProductCaption = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "SAMPLE_BTN_CAPTION", "");
            if (!ProductCaption.isEmpty())
                sample.setText(ProductCaption);

            sample.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    if (mylist.isEmpty()) {
                        customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                    }
                    if (dr_id.equals("0")) {
                        customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                    } else {
/*
                    Custom_Variables_And_Method.DR_ID=dr_id;
                    Intent i = new Intent(Doctor_Sample.this, Dr_Sample.class);
                    i.putExtra("intent_fromRcpaCAll","dr");
                    i.putExtra("sample_name",sample_name);
                    i.putExtra("sample_pob",sample_pob);
                    i.putExtra("sample_sample",sample_sample);
                    i.putExtra("sample_noc",sample_noc);
                  startActivityForResult(i, 0);

*/

                        Bundle b = new Bundle();
                        b.putString("intent_fromRcpaCAll", "dr");
                        b.putString("sample_name", sample_name);
                        b.putString("sample_pob", sample_pob);
                        b.putString("sample_sample", sample_sample);
                        b.putString("sample_noc", sample_noc);
                        new Dr_Sample_Dialog(context, mHandler, b, PRODUCT_DILOG).Show();
                    }


                }
            });
        } else {
            sample.setVisibility(View.GONE);
        }


        prescribed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mylist.isEmpty()) {
                    customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                }
                if (dr_id.equals("")) {
                    customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                } else {

                    Intent prescribed = new Intent(getApplicationContext(), PrescribeNew.class);
                    startActivity(prescribed);
                }
            }
        });

        dr_sale.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mylist.isEmpty()) {
                    customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                } else if (dr_id.equals("")) {
                    customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                } else {

                    /*Intent i = new Intent(context, CustomWebView.class);
                    i.putExtra("A_TP", Dr_sale_url+"&DR_ID="+dr_id+"&MONTH="+customVariablesAndMethod.currentDate());
                    i.putExtra("Title", doc_name);
                    startActivity(i);*/
                    MyCustumApplication.getInstance().LoadURL(doc_name, Dr_sale_url + "&DR_ID=" + dr_id + "&MONTH=" + customVariablesAndMethod.currentDate());
                }
            }
        });

        later.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent(getApplicationContext(), LoginFake.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();*/
                MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
            }
        });

        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                if (mylist.isEmpty()) {
                    customVariablesAndMethod.msgBox(context, "No Doctor in List...");
                }
                if (dr_id.equals("0")) {
                    customVariablesAndMethod.msgBox(context, "Select Doctor from List...");
                } else if (Custom_Variables_And_Method.getInstance().isCheckForProduct("D")) {
                    if (!sample_added && customVariablesAndMethod.IsProductEntryReq(context, CallType.DOCTOR)) {

                        AppAlert.getInstance().DecisionAlert(context, "ALERT !!!",
                                "Please select atleast one promoted product",
                                new AppAlert.OnClickListener() {
                                    @Override
                                    public void onPositiveClicked(View item, String result) {
                                        sample.performClick();
                                        // gift_added=true;
                                        //(1);
                                    }

                                    @Override
                                    public void onNegativeClicked(View item, String result) {
                                        // no_prescription.setChecked(false);
                                    }
                                });
                        // customVariablesAndMethod.msgBox(context,"Please select atleast one promoted product");
                    } else if (!gift_added && customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DRGIFTMANDATORY").contains("D")) {
                        AppAlert.getInstance().DecisionAlert(context, "ALERT !!!",
                                "Please give atleast  one Gift...",
                                new AppAlert.OnClickListener() {
                                    @Override
                                    public void onPositiveClicked(View item, String result) {
                                        gift.performClick();
                                        // gift_added=true;
                                        //(1);
                                    }

                                    @Override
                                    public void onNegativeClicked(View item, String result) {
                                        // no_prescription.setChecked(false);
                                    }
                                });
                        // customVariablesAndMethod.msgBox(context,"Please selct  gift");
                    } else if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "REMARK_WW_MANDATORY").contains("D") && dr_remark.getText().toString().equals("")) {
                        customVariablesAndMethod.msgBox(context, "Please enter remak");
                    } else if (attachment.getAttachmentStr().isEmpty() &&
                            MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DR_SIGNATURE_MANDATORYYN", "N")
                                    .equalsIgnoreCase("Y")) {
                        customVariablesAndMethod.msgBox(context, "Please Add " + attachment.getTitle());
                    } else if (attachment.filesToUpload().length > 0) {
                        cboProgressDialog = new CboProgressDialog(context, "Please Wait..\nuploading Image");
                        cboProgressDialog.show();
                        new up_down_ftp().uploadFile(attachment.filesToUpload(), context);
                    } else {

//                    mdrCall.setRemark(dr_remark.getText().toString());
//                    drCallDB.insert(mdrCall);
//                    locationDB.insert(mdrCall);
//
//                    cbohelp.updateRemark_TempDrInLocal(dr_id, dr_remark.getText().toString());
//
//                    if (!call.equals("Y")) {
//                        new Service_Call_From_Multiple_Classes().SendFCMOnCall(context, mHandler, MESSAGE_INTERNET_SEND_FCM, "D", dr_id, "");
//                    } else {
//                        customVariablesAndMethod.msgBox(context, "Sample Saved Sucessfully....");
//                        /*Intent intent = new Intent(getApplicationContext(), LoginFake.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.putExtra("EXIT", true);
//                        startActivity(intent);
//                        finish();*/
//                        MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
//                    }

                        submitFinal();
                    }

                } else if (!gift_added && customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DRGIFTMANDATORY").contains("D")) {
                    AppAlert.getInstance().DecisionAlert(context, "ALERT !!!",
                            "Please give atleast  one Gift...",
                            new AppAlert.OnClickListener() {
                                @Override
                                public void onPositiveClicked(View item, String result) {
                                    gift.performClick();
                                    // gift_added=true;
                                    //(1);
                                }

                                @Override
                                public void onNegativeClicked(View item, String result) {
                                    // no_prescription.setChecked(false);
                                }
                            });
                    // customVariablesAndMethod.msgBox(context,"Please selct  gift");
                } else if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "REMARK_WW_MANDATORY").contains("D") && dr_remark.getText().toString().equals("")) {
                    customVariablesAndMethod.msgBox(context, "Please enter remak");
                } else if (attachment.getAttachmentStr().isEmpty() &&
                        MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DR_SIGNATURE_MANDATORYYN", "N")
                                .equalsIgnoreCase("Y")) {
                    customVariablesAndMethod.msgBox(context, "Please Add " + attachment.getTitle());
                } else if (attachment.filesToUpload().length > 0) {
                    cboProgressDialog = new CboProgressDialog(context, "Please Wait..\nuploading Image");
                    cboProgressDialog.show();
                    new up_down_ftp().uploadFile(attachment.filesToUpload(), context);
                } else {

//                    mdrCall.setRemark(dr_remark.getText().toString());
//                    drCallDB.insert(mdrCall);
//                    locationDB.insert(mdrCall);
//
//                    cbohelp.updateRemark_TempDrInLocal(dr_id, dr_remark.getText().toString());
//
//                    if (!call.equals("Y")) {
//                        new Service_Call_From_Multiple_Classes().SendFCMOnCall(context, mHandler, MESSAGE_INTERNET_SEND_FCM, "D", dr_id, "");
//                    } else {
//                        customVariablesAndMethod.msgBox(context, "Sample Saved Sucessfully....");
//                        /*Intent intent = new Intent(getApplicationContext(), LoginFake.class);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                        intent.putExtra("EXIT", true);
//                        startActivity(intent);
//                        finish();*/
//                        MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
//                    }

                    submitFinal();
                }
            }
        });


        doctor_list_summary = cbohelp.getCallDetail("tempdr", "", "0");
        //expense_list=new CBO_DB_Helper(context).getCallDetail("tempdr");

        summary_list = new LinkedHashMap<>();
        summary_list.put("Doctor", doctor_list_summary);

        final ArrayList<String> header_title = new ArrayList<>();
        //final List<Integer> visible_status=new ArrayList<>();
        for (String main_menu : summary_list.keySet()) {
            header_title.add(main_menu);
            //visible_status.add(0);
        }


        listAdapter = new ExpandableListAdapter(summary_layout, this, header_title, summary_list);

        // setting list adapter
        summary_layout.setAdapter(listAdapter);
        summary_layout.setGroupIndicator(null);
        for (int i = 0; i < listAdapter.getGroupCount(); i++)
            summary_layout.expandGroup(i);
        //doctor.expandGroup(1);

        summary_layout.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {

                summary_list.get(header_title.get(groupPosition)).get("visible_status").get(childPosition);
                if (summary_list.get(header_title.get(groupPosition)).get("visible_status").get(childPosition).equals("1")) {
                    summary_list.get(header_title.get(groupPosition)).get("visible_status").set(childPosition, "0");
                } else {
                    summary_list.get(header_title.get(groupPosition)).get("visible_status").set(childPosition, "1");
                }
                listAdapter.notifyDataSetChanged();
                return false;
            }
        });

        tab_call.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                call_layout.setVisibility(View.VISIBLE);
                summary_layout.setVisibility(View.GONE);
                tab_call.setBackgroundResource(R.drawable.tab_selected);
                tab_summary.setBackgroundResource(R.drawable.tab_deselected);
            }
        });

        tab_summary.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                summary_layout.setVisibility(View.VISIBLE);
                call_layout.setVisibility(View.GONE);
                tab_call.setBackgroundResource(R.drawable.tab_deselected);
                tab_summary.setBackgroundResource(R.drawable.tab_selected);
            }
        });

    }

    public ArrayList<String> remAdded() {
        ArrayList<String> drlist = new ArrayList<String>();
        drlist.clear();
        Cursor c = cbohelp.getDoctorListLocal(null, "1");  //getDoctorName1();
        if (c.moveToFirst()) {
            do {
                drlist.add(c.getString(c.getColumnIndex("dr_id")));
            } while (c.moveToNext());
        }
        return drlist;
    }

    private void init(String[] sample_name, String[] sample_qty, String[] sample_pob, String[] sample_noc, String[] sample_rate) {

        Boolean showNOC = true;
        if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "NOC_HEAD", "").isEmpty()) {
            showNOC = false;
        }
        //TableLayout stk= (TableLayout) findViewById(R.id.promotion);
        //ArrayList<String> sample_name= childText.get("sample_name").get(childPosition).toString();
        TableRow tbrow0 = new TableRow(context);
        tbrow0.setBackgroundColor(0xff125688);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        TextView tv0 = new TextView(context);
        tv0.setText("Product");
        tv0.setPadding(5, 5, 5, 0);
        tv0.setTextColor(Color.WHITE);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setLayoutParams(params);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(context);
        tv1.setText(" Sample ");
        tv1.setPadding(5, 5, 5, 0);
        tv1.setTextColor(Color.WHITE);
        tv1.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tv1);
        TextView tv2 = new TextView(context);
        tv2.setPadding(5, 5, 5, 0);
        tv2.setText(" POB ");
        tv2.setTextColor(Color.WHITE);
        tv2.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tv2);
        if (showNOC) {
            TextView tv3 = new TextView(context);
            tv3.setPadding(5, 5, 5, 0);
            tv3.setText(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "NOC_HEAD"));
            tv3.setTextColor(Color.WHITE);
            tv3.setTypeface(null, Typeface.BOLD);
            tbrow0.addView(tv3);
        }

        if (sample_rate != null) {
            TextView tv4 = new TextView(context);
            tv4.setPadding(5, 5, 5, 0);
            tv4.setText("Amt.");
            tv4.setTextColor(Color.WHITE);
            tv4.setTypeface(null, Typeface.BOLD);
            tbrow0.addView(tv4);
        }

        stk.removeAllViews();
        stk.addView(tbrow0);
        for (int i = 0; i < sample_name.length; i++) {
            TableRow tbrow = new TableRow(context);
            TextView t1v = new TextView(context);
            t1v.setText(sample_name[i]);
            t1v.setPadding(5, 5, 5, 0);
            t1v.setTextColor(Color.BLACK);
            t1v.setLayoutParams(params);
            tbrow.addView(t1v);
            TextView t2v = new TextView(context);
            t2v.setText(sample_qty[i]);
            t2v.setPadding(5, 5, 5, 0);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            TextView t3v = new TextView(context);
            t3v.setText(sample_pob[i]);
            t3v.setPadding(5, 5, 5, 0);
            t3v.setTextColor(Color.BLACK);
            t3v.setGravity(Gravity.CENTER);
            tbrow.addView(t3v);
            if (showNOC) {
                TextView t4v = new TextView(context);
                t4v.setText(sample_noc[i]);
                t4v.setPadding(5, 5, 5, 0);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);
            }
            if (sample_rate != null) {
                TextView t4v = new TextView(context);
                t4v.setText("" + String.format("%.2f", (Double.parseDouble(sample_pob[i]) * Double.parseDouble(sample_rate[i]))));
                t4v.setPadding(5, 5, 5, 0);
                t4v.setTextColor(Color.BLACK);
                t4v.setGravity(Gravity.CENTER);
                tbrow.addView(t4v);
            }
            stk.addView(tbrow);
        }
    }

    private void init_gift(TableLayout stk1, String[] gift_name, String[] gift_qty) {
        //ArrayList<String> sample_name= childText.get("sample_name").get(childPosition).toString();
        TableRow tbrow0 = new TableRow(context);
        tbrow0.setBackgroundColor(0xff125688);
        TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableLayout.LayoutParams.WRAP_CONTENT, 1f);
        TextView tv0 = new TextView(context);
        tv0.setText("Gift");
        tv0.setPadding(5, 5, 5, 0);
        tv0.setTextColor(Color.WHITE);
        tv0.setTypeface(null, Typeface.BOLD);
        tv0.setLayoutParams(params);
        tbrow0.addView(tv0);
        TextView tv1 = new TextView(context);
        tv1.setText(" Qty. ");
        tv1.setPadding(5, 5, 5, 0);
        tv1.setTextColor(Color.WHITE);
        tv1.setTypeface(null, Typeface.BOLD);
        tbrow0.addView(tv1);
        stk1.removeAllViews();
        stk1.addView(tbrow0);
        for (int i = 0; i < gift_name.length; i++) {
            TableRow tbrow = new TableRow(context);
            TextView t1v = new TextView(context);
            t1v.setText(gift_name[i]);
            t1v.setPadding(5, 5, 5, 0);
            t1v.setTextColor(Color.BLACK);
            t1v.setLayoutParams(params);
            tbrow.addView(t1v);
            TextView t2v = new TextView(context);
            t2v.setText(gift_qty[i]);
            t2v.setPadding(5, 5, 5, 0);
            t2v.setTextColor(Color.BLACK);
            t2v.setGravity(Gravity.CENTER);
            tbrow.addView(t2v);
            stk1.addView(tbrow);
        }
        if (gift_name.length == 1 && gift_name[0].equals("0")) {
            stk1.removeAllViews();
        }

    }

    //==================================================onCreate end========================================================================

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "M3GXGNKRRC8F9VPNYYY4");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }

    //====================================================gift=========================================================================


//====================================================================tez===============================================================

    protected void onActivityResult(int reqcode, int rescode, Intent iob) {

        if (iob == null) {
            return;
        }

        Bundle b1 = iob.getExtras();

        switch (reqcode) {
            case 0:
                if (rescode == RESULT_OK) {
                    sample_added = true;
                    name = b1.getString("val");
                    //b1.getString("val");
                    name2 = b1.getString("val2");//pob qty
                    name7 = b1.getString("val3");//sample qty
                    result = b1.getDouble("resultpob");
                    resultList = b1.getString("resultList");
                    DecimalFormat f = new DecimalFormat("#.00");
                    String result3 = f.format(result);
                    res_main = Double.parseDouble(result3);

                    doctor_list = cbohelp.getCallDetail("tempdr", Custom_Variables_And_Method.DR_ID, "0");

                    if (!doctor_list.get("sample_name").get(0).equals("")) {
                        String[] sample_name1 = doctor_list.get("sample_name").get(0).split(",");
                        String[] sample_qty1 = doctor_list.get("sample_qty").get(0).split(",");
                        String[] sample_pob1 = doctor_list.get("sample_pob").get(0).split(",");
                        String[] sample_noc1 = doctor_list.get("sample_noc").get(0).split(",");

                        sample_name = doctor_list.get("sample_name").get(0);
                        sample_sample = doctor_list.get("sample_qty").get(0);
                        sample_pob = doctor_list.get("sample_pob").get(0);
                        sample_noc = doctor_list.get("sample_noc").get(0);

                        init(sample_name1, sample_qty1, sample_pob1, sample_noc1, null);
                    }
                    if (!doctor_list.get("gift_name").get(0).equals("")) {
                        String[] gift_name1 = doctor_list.get("gift_name").get(0).split(",");
                        String[] gift_qty1 = doctor_list.get("gift_qty").get(0).split(",");

                        gift_name = doctor_list.get("gift_name").get(0);
                        gift_qty = doctor_list.get("gift_qty").get(0);

                        init_gift(gift_layout, gift_name1, gift_qty1);
                    }
                }
                break;

            case 1:
                if (rescode == RESULT_OK) {
                    name3 = b1.getString("giftid");
                    name4 = b1.getString("giftqan");

                    doctor_list = cbohelp.getCallDetail("tempdr", Custom_Variables_And_Method.DR_ID, "0");

                    if (!doctor_list.get("sample_name").get(0).equals("")) {
                        String[] sample_name1 = doctor_list.get("sample_name").get(0).split(",");
                        String[] sample_qty1 = doctor_list.get("sample_qty").get(0).split(",");
                        String[] sample_pob1 = doctor_list.get("sample_pob").get(0).split(",");
                        String[] sample_noc1 = doctor_list.get("sample_noc").get(0).split(",");

                        sample_name = doctor_list.get("sample_name").get(0);
                        sample_sample = doctor_list.get("sample_qty").get(0);
                        sample_pob = doctor_list.get("sample_pob").get(0);
                        sample_noc = doctor_list.get("sample_noc").get(0);

                        init(sample_name1, sample_qty1, sample_pob1, sample_noc1, null);
                    }
                    if (!doctor_list.get("gift_name").get(0).equals("")) {
                        String[] gift_name1 = doctor_list.get("gift_name").get(0).split(",");
                        String[] gift_qty1 = doctor_list.get("gift_qty").get(0).split(",");

                        gift_name = doctor_list.get("gift_name").get(0);
                        gift_qty = doctor_list.get("gift_qty").get(0);

                        init_gift(gift_layout, gift_name1, gift_qty1);
                    }

                }
                break;
            case CBOImageView.REQUEST_CAMERA:
                if (rescode == RESULT_OK) {
                    attachment.onActivityResult(reqcode, rescode, iob);
                }
                break;
            default:
                super.onActivityResult(reqcode, rescode, iob);

        }


    }

    public void getDoctorList() {
        dr_name1 = new ArrayList<String>();
        Cursor c = cbohelp.getDoctorName();
        mylist.add(new SpinnerModel("--Select--", "0"));
        if (c.moveToFirst()) {
            do {
                mylist.add(new SpinnerModel(c.getString(c.getColumnIndex("dr_name")), c.getString(c.getColumnIndex("dr_id"))));

            } while (c.moveToNext());
        }
        adapter = new SpinAdapter(getApplicationContext(), R.layout.spin_row, mylist);
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
        dr_name.setAdapter(adapter);
        cbohelp.close();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        if (item != null) {
            Custom_Variables_And_Method.DR_ID = null;
            Custom_Variables_And_Method.DR_NAME = null;
            Custom_Variables_And_Method.DOCTOR_SPL_ID = 0;

            if (call.equals("Y")) {
                if (!customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "SAMPLE_POB_MANDATORY").equals("Y") || !sample_name.equals("")) {
                   /* Intent intent = new Intent(getApplicationContext(), LoginFake.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);
                    finish();*/
                    MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
                } else {
                    customVariablesAndMethod.msgBox(context, "Plese enter POB quantity");
                }
            } else {
                finish();
            }

        }

        return super.onOptionsItemSelected(item);
    }

    private void onClickDrCallRemark() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(Doctor_Sample.this);
        final ListView listview = new ListView(Doctor_Sample.this);
        LinearLayout layout = new LinearLayout(Doctor_Sample.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listview);
        myDialog.setView(layout);
        //ArrayAdapter arrayAdapter = new ArrayAdapter(mDrCall.this, R.layout.spin_row, cbohelp.get_Doctor_Call_Remark());
        ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, remark_list);
        listview.setAdapter(arrayAdapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myalertDialog.dismiss();
                btn_remark.setText(remark_list.get(position));
                if (remark_list.get(position).equalsIgnoreCase("other")) {
                    dr_remark.setText("");
                    dr_remark.setVisibility(View.VISIBLE);
                } else {
                    dr_remark.setText(remark_list.get(position));
                    dr_remark.setVisibility(View.GONE);
                }
            }
        });

        myalertDialog = myDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (call.equals("Y")) {
            if (!customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "SAMPLE_POB_MANDATORY").equals("Y") || sample_added) {
               /* Intent intent = new Intent(getApplicationContext(), LoginFake.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.putExtra("EXIT", true);
                startActivity(intent);
                finish();*/
                MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
            } else {
                customVariablesAndMethod.msgBox(context, "Plese enter POB quantity");
            }
        } else {
            finish();
        }
    }

    @Override
    public void started(Integer responseCode, String message, String description) {

    }

    @Override
    public void progess(Integer responseCode, Long FileSize, Float value, String description) {

    }

    @Override
    public void complete(Integer responseCode, String message, String description) {
        cboProgressDialog.dismiss();
        cbohelp.updateDrFileUpdated(dr_id, "1");
        submitFinal();
    }

    @Override
    public void aborted(Integer responseCode, String message, String description) {
        cboProgressDialog.dismiss();
        AppAlert.getInstance().getAlert(context, message, description);
    }

    @Override
    public void failed(Integer responseCode, String message, String description) {
        cboProgressDialog.dismiss();
        AppAlert.getInstance().getAlert(context, message, description);
    }

    private void submitFinal() {

        mdrCall.setRemark(dr_remark.getText().toString());
        drCallDB.insert(mdrCall);
        locationDB.insert(mdrCall);

        cbohelp.updateRemark_TempDrInLocal(dr_id, dr_remark.getText().toString());

        if (!call.equals("Y")) {
            new Service_Call_From_Multiple_Classes().SendFCMOnCall(context, mHandler, MESSAGE_INTERNET_SEND_FCM, "D", dr_id, "");
        } else {
            customVariablesAndMethod.msgBox(context, "Sample Saved Sucessfully....");
                        /*Intent intent = new Intent(getApplicationContext(), LoginFake.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.putExtra("EXIT", true);
                        startActivity(intent);
                        finish();*/
            MyCustumApplication.getInstance().Logout((AppCompatActivity) context);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }
}

