package com.cbo.cbomobilereporting.ui_new.utilities_activities.newDocPhoto;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.flurry.android.FlurryAgent;
import com.uenics.javed.CBOLibrary.Response;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.adapterutils.DocSampleModel;
import utils_new.AppAlert;
import utils_new.Custom_Variables_And_Method;
import utils_new.Service_Call_From_Multiple_Classes;

public class NewDocPhoto extends AppCompatActivity implements IRecyclerViewList {
    Custom_Variables_And_Method customVariablesAndMethod;
    Context context;
    int PA_ID = 0;
    String id = "";
    String Dr_Id = "";
    CBO_DB_Helper cbohelp;
    String rowid = "";
    NewLazyAdapter adapter1;
    ArrayList<String> sample_id = new ArrayList<String>();
    ArrayList<String> checkId = new ArrayList<String>();
    ArrayList<String> checkId1 = new ArrayList<String>();
    ArrayList<String> sample_name = new ArrayList<String>();
    ArrayList<DocSampleModel> listA = new ArrayList<DocSampleModel>();
    AlertDialog.Builder builder1;
    AlertDialog dialog;
    int who = 1;
    private String sample_name_Stored = "", sample_pob = "", sample_sample = "";
    private Button btContinue;
    private RecyclerView recyclerView;


    public void onCreate(Bundle b) {
        super.onCreate(b);
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.activity_new_doc_photo);
        FlurryAgent.logEvent("Doc Photos");

        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_hadder);
        TextView hader_text = (TextView) findViewById(R.id.hadder_text_1);
        setSupportActionBar(toolbar);
        hader_text.setText("Visual Ads");

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        btContinue = (Button) findViewById(R.id.btContinue);
        context = this;
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        cbohelp = new CBO_DB_Helper(getApplicationContext());
        PA_ID = Custom_Variables_And_Method.PA_ID;
        Dr_Id = getIntent().getStringExtra("dr_id");
        who = getIntent().getIntExtra("who", 1);
        if (who == 0) {
            Bundle getExtra = getIntent().getExtras();
            if (getExtra != null) {
                sample_name_Stored = getExtra.getString("sample_name");
                sample_pob = getExtra.getString("sample_pob");
                sample_sample = getExtra.getString("sample_sample");
            }
        }


        String splcode;
        splcode = "";
        Cursor c = cbohelp.getDoctorSpecialityCodeByDrId(Dr_Id);
        if (c.moveToFirst()) {
            do {
                splcode = c.getString(c.getColumnIndex("remark"));
            } while (c.moveToNext());
        }
        Custom_Variables_And_Method.pub_doctor_spl_code = splcode;
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter1 = new NewLazyAdapter(this, bindVisualAidA(), "0", checkId1, sample_id);
        recyclerView.setAdapter(adapter1);

        adapter1.setRecycleViewOnItemClickListener(this);


        if (adapter1.getItemCount() != 0) {
        } else {
            builder1 = new AlertDialog.Builder(NewDocPhoto.this);
            builder1.setTitle("Empty List..");
            builder1.setMessage(" No Data In List.." + "\n" + "Please Download Data .....");
            builder1.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK)
                        onBackPressed();
                    return false;
                }
            });
            builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub

                    //Intent i=new Intent(getApplicationContext(),MyUtil.class);
                    //startActivity(i);
                    //finish();
					/*NetworkUtil networkUtil = new NetworkUtil(getApplicationContext());
					if (!networkUtil.internetConneted(getApplicationContext())) {
						customVariablesAndMethod.Connect_to_Internet_Msg(context);
					} else {
						startActivityForResult(new Intent(getApplicationContext(), VisualAid_Download.class),1);

					}*/

                    new Service_Call_From_Multiple_Classes().getListForLocal(context, new Response() {
                        @Override
                        public void onSuccess(Bundle bundle) {
                            customVariablesAndMethod.msgBox(context, "Data Downloded Sucessfully...");

                        }

                        @Override
                        public void onError(String message, String description) {
                            AppAlert.getInstance().getAlert(context, message, description);
                        }
                    });
                }
            });
            dialog = builder1.create();
            dialog.show();
        }


        btContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(getApplicationContext(), New_Show_Sample.class);
                i.putExtra("samid", sample_id);
                i.putExtra("sam_name", sample_name);
                i.putExtra("dr_id", Dr_Id);
                i.putExtra("myid", rowid);
                i.putExtra("title", "");
                i.putExtra("rowid", 1);
                i.putExtra("who", who);
                i.putExtra("checkId", checkId);
                i.putExtra("listData", (Serializable) listA);
                i.putExtra("sample_name_Stored", sample_name_Stored);
                i.putExtra("sample_pob", sample_pob);
                i.putExtra("sample_sample", sample_sample);
                startActivityForResult(i, 7);

            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case 1:
                startActivity(new Intent(NewDocPhoto.this, NewDocPhoto.class));
                break;
            case 7:
                Intent i = new Intent();
                i.putExtra("data", data);
                setResult(RESULT_OK, i);
                finish();
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
        }
    }

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

    @Override
    public void onBackPressed() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        if (who == 0) {
            Intent i = new Intent();
            i.putExtra("val", "");
            i.putExtra("val2", "");
            i.putExtra("val3", "");
            i.putExtra("resultpob", "0");
            setResult(RESULT_OK, i);
        }
//        finish();
        super.onBackPressed();

    }

    ArrayList<String> selecedBrandPre = new ArrayList<>();
    Set<String> selected = new HashSet<>();


    private List<DocSampleModel> sortList(List<DocSampleModel> list) {
        int pointer = 0;
		/*sample_id.clear();
		sample_name.clear();*/
        if (!MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_SELECTEDONLY", "").equalsIgnoreCase("Y")) {
            for (int index = 0; index < list.size(); index++) {
                DocSampleModel sampleModel = list.get(index);
                if (sampleModel.isHighlighted()) {
                    pointer++;
                } else if (sampleModel.get_Checked() && index != pointer) {
                    list.add(pointer, sampleModel);
                    sample_id.add(pointer, sampleModel.getId());
                    sample_name.add(pointer, sampleModel.getName());
                    pointer++;
                    list.remove(index + 1);
                    sample_id.remove(index + 1);
                    sample_name.remove(index + 1);
                }


            }
        } else {
            for (int index = 0; index < list.size(); index++) {
                DocSampleModel sampleModel = list.get(index);
                if (!(sampleModel.isHighlighted() || sampleModel.get_Checked())) {
                    list.remove(index);
                    sample_id.remove(index);
                    sample_name.remove(index--);
                }
            }

        }

        return list;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            if (who == 0) {
                Intent i = new Intent();
                i.putExtra("val", "");
                i.putExtra("val2", "");
                i.putExtra("val3", "");
                i.putExtra("resultpob", "0");
                setResult(RESULT_OK, i);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onResume() {
        super.onResume();

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter1 = new NewLazyAdapter(this, bindVisualAidA(), "0", checkId1, sample_id);
        recyclerView.setAdapter(adapter1);
        adapter1.setRecycleViewOnItemClickListener(this);

    }


    //new list builder

    private ArrayList<DocSampleModel> bindVisualAidA() {
        listA.clear();
        String ItemIdNotIn = "'0'";

        String path = Environment.getExternalStorageDirectory().toString() + "/cbo/product";
        File directory = new File(path);
        File[] files = directory.listFiles();


        cbohelp = new CBO_DB_Helper(getApplicationContext());


        selecedBrandPre = cbohelp.getDoctorVisualIdByBrand(Dr_Id);

        for (int i = 0; i < selecedBrandPre.size(); i++) {
            checkId1.add(selecedBrandPre.get(i));
        }

        int cnt = 0;

        int cnt3 = 0;
        Custom_Variables_And_Method.DOCTOR_SPL_ID = 1;
        Cursor c3 = cbohelp.getphitemSplByBrand();
        int cnt2 = 0;
        Cursor c1 = cbohelp.getSelectedFromDrByBrand(Dr_Id);
        //ArrayList<String> Dr_Item_list=new ArrayList<>();
        if (c1.moveToFirst()) {
            do {
                //Dr_Item_list.add(""+c1.getInt(c1.getColumnIndex("item_id")));
                listA.add(new DocSampleModel(c1.getString(c1.getColumnIndex("BRAND_NAME")),
                        "" + c1.getInt(c1.getColumnIndex("BRAND_ID")), "" + cnt2, true));
                sample_id.add("" + c1.getInt(c1.getColumnIndex("BRAND_ID")));
                sample_name.add(c1.getString(c1.getColumnIndex("BRAND_NAME")));
                ItemIdNotIn = ItemIdNotIn + ",'" + c1.getString(c1.getColumnIndex("BRAND_ID")) + "'";
                cnt2 = cnt2 + 1;
            } while (c1.moveToNext());

        } else if (c3.moveToFirst() && who == 0) {
            listA.clear();
            do {
                listA.add(new DocSampleModel(c3.getString(c3.getColumnIndex("BRAND_NAME")),
                        c3.getString(c3.getColumnIndex("BRAND_ID")), "" + cnt3, false));
                sample_id.add(c3.getString(c3.getColumnIndex("BRAND_ID")));
                sample_name.add(c3.getString(c3.getColumnIndex("BRAND_NAME")));
//                ItemIdNotIn=ItemIdNotIn+"," + c3.getString(c3.getColumnIndex("item_id"));
                cnt3 = cnt3 + 1;
            } while (c3.moveToNext());

        } else {


            //cbohelp = new CBO_DB_Helper(getApplicationContext());
            cnt = 0;
            Cursor c = cbohelp.getAllVisualAddByBrand(ItemIdNotIn, "N");
            //Cursor c=myitem.getSelected();
            //rs=stmt.getResultSet();
            if (c.moveToFirst()) {
                do {
                    //Boolean isHighlited=Dr_Item_list.contains(c.getString(c.getColumnIndex("item_id")));
                    listA.add(new DocSampleModel(c.getString(c.getColumnIndex("BRAND_NAME")),
                            c.getString(c.getColumnIndex("BRAND_ID")), "" + cnt, false));  //Dr_Item_list.contains(c.getString(c.getColumnIndex("item_id")))
                    sample_id.add(c.getString(c.getColumnIndex("BRAND_ID")));
                    sample_name.add(c.getString(c.getColumnIndex("BRAND_NAME")));
                    cnt = cnt + 1;
                } while (c.moveToNext());
            }

            c.close();

        }
        c3.close();
        c1.close();

        if (files != null) {
            for (File file1 : files) {
                String file = file1.toString();
                if (file.contains(".")) {
                    String file_name = file.substring(file.lastIndexOf("/") + 1,
                            file.lastIndexOf("."));
                    if (file.contains(".") && sample_id.contains(file_name)) {
                        for (int j = 0; j < sample_id.size(); j++) {
                            if (sample_id.get(j).equals(file_name)) {
                                listA.get(j).set_file_ext(file.substring(file.lastIndexOf(".")));
                                break;
                            }
                        }
                    }
                } else {
                    if (sample_name.contains(file1.getName())) {
                        for (int j = 0; j < sample_name.size(); j++) {
                            if (sample_name.get(j).equals(file1.getName())) {
                                listA.get(j).set_file_ext(".html");
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (who == 0) {
            String[] sample_name1 = sample_name_Stored.split(",");
			/*String[] sample_qty1 = sample_sample.split(",");
			String[] sample_pob1 = sample_pob.split(",");*/

            for (int i = 0; i < sample_name1.length; i++) {
                for (int j = 0; j < listA.size(); j++) {
                    if (sample_name1[i].equals(listA.get(j).getName())) {
                        listA.get(j).set_Checked(true);
                    }
                }
            }
            sortList(listA);
        }
        return listA;
    }


    @Override
    public void onClickData(int position, CheckBox btnCheckBox, String id) {

        if (btnCheckBox.isChecked()) {
            checkId.add(id);
        } else {
            checkId.remove(id);
        }

    }
}