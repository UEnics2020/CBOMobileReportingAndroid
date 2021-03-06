package utils_new;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.databaseHelper.Call.Db.MainDB;
import com.cbo.cbomobilereporting.databaseHelper.Call.Local.DayPlanAlertDB;
import com.cbo.cbomobilereporting.databaseHelper.Controls;
import com.cbo.cbomobilereporting.databaseHelper.User.mUser;
import com.cbo.cbomobilereporting.emp_tracking.MyCustomMethod;
import com.cbo.cbomobilereporting.ui.LoginMain;
import com.cbo.cbomobilereporting.ui_new.CustomActivity;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.GetDCR;
import com.cbo.cbomobilereporting.ui_new.utilities_activities.VisualAdsDownload.VisualAdsDownloadActivity;
import com.uenics.javed.CBOLibrary.CBOException;
import com.uenics.javed.CBOLibrary.CBOServices;
import com.uenics.javed.CBOLibrary.Response;
import com.uenics.javed.CBOLibrary.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import services.CboServices;
import services.MyAPIService;

import com.cbo.cbomobilereporting.MyCustumApplication;

/**
 * Created by pc24 on 21/12/2017.
 */

public class Service_Call_From_Multiple_Classes {
    Integer response_code;
    Handler mHandler;
    ProgressDialog progress1;
    Context context;
    Custom_Variables_And_Method customVariablesAndMethod;
    CBO_DB_Helper cbo_helper;

    private static final int MESSAGE_INTERNET_SEND_FCM_ = 2;

    public Service_Call_From_Multiple_Classes() {
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        cbo_helper = new CBO_DB_Helper(MyCustumApplication.getInstance());
    }


    public void SendFCMOnCall(Context context, Handler mHandler, final Integer response_code, String DocType, String Id, String latlong) {


        this.response_code = response_code;
        this.mHandler = mHandler;
        this.context = context;
        progress1 = new ProgressDialog(context);
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        cbo_helper = new CBO_DB_Helper(context);

        if (!customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "FCMHITCALLYN", "").contains(DocType)) {
            threadMsg("Ok");
            return;
        }
        if (latlong.equals("")) {
            latlong = cbo_helper.getLatLong(DocType, Id);
        }

        //Start of call to service

        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", cbo_helper.getCompanyCode());
        request.put("sPA_ID", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "work_with_id", "0,") + "0");
        request.put("sMessage", "[{\"msgtyp\":\"CALL\"},{\"tilte\":\"" + Id + "\"},{\"msg\":\"" + DocType + "\"},{\"url\":\"" + latlong + "\"},{\"logo\":\"\"}]");
        request.put("iDESIG_ID", "");
        request.put("iKEY", "");


        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);

        progress1.setMessage("Please Wait..");
        progress1.setCancelable(false);
        progress1.show();

        new CboServices(context, hh).customMethodForAllServices(request, "GCM_MessagePush_Domain", MESSAGE_INTERNET_SEND_FCM_, tables);
        //End of call to service

    }


    private void threadMsg(String msg) {
        Message msgObj = mHandler.obtainMessage(response_code);
        Bundle b = new Bundle();
        b.putString("msg", msg);
        msgObj.setData(b);
        mHandler.sendMessage(msgObj);
    }


    private final Handler hh = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case MESSAGE_INTERNET_SEND_FCM_:

                    if ((null != msg.getData())) {

                        parser_FCM(msg.getData());

                    }
                    break;
                case 99:
                    if (progress1 != null && progress1.isShowing()) {
                        progress1.dismiss();
                    }
                    if ((null != msg.getData())) {
                        customVariablesAndMethod.msgBox(context, msg.getData().getString("Error"));
                    }
                    break;
                default:
                    if (progress1 != null && progress1.isShowing()) {
                        progress1.dismiss();
                    }

            }
        }
    };


    public void parser_DCRCOMMIT_DOWNLOADALL(Context context, Bundle result, Response listener) {

        if (result != null) {

            try {
                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);

                if (jsonArray1.length() > 0) {
                    JSONObject one = jsonArray1.getJSONObject(0);

                    String MyDaType = one.getString("DA_TYPE");
                    String da_val = "0";
                    Float rate = Float.parseFloat(one.getString("FARE_RATE"));
                    Float kms = Float.parseFloat(one.getString("KM"));

                    if (MyDaType.equals("L")) {
                        da_val = one.getString("DA_L_RATE");
                    } else if (MyDaType.equals("EX") || MyDaType.equals("EXS")) {
                        da_val = one.getString("DA_EX_RATE");
                    } else if (MyDaType.equals("NSD") || MyDaType.equals("NS")) {
                        da_val = one.getString("DA_NS_RATE");
                    }
                    String distance_val = "0";
                    if (MyDaType.equals("EX") || MyDaType.equals("NSD")) {
                        distance_val = "" + (kms * rate * 2);

                    } else {
                        distance_val = "" + (kms * rate);
                    }

                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DA_TYPE", MyDaType);
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "da_val", da_val);
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "distance_val", distance_val);
                }


                String table1 = result.getString("Tables1");
                cbo_helper.delete_phdoctor(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCR_MULTIPLE_ROUTEYN", "N").equalsIgnoreCase("Y"));

                JSONArray jsonArray2 = new JSONArray(table1);
                for (int i = 0; i < jsonArray2.length(); i++) {
                    JSONObject c = jsonArray2.getJSONObject(i);
                    cbo_helper.insert_phdoctor(c.getInt("DR_ID"), c.getString("DR_NAME"), "", "", c.getInt("SPL_ID"), c.getString("LASTCALL"),
                            c.getString("CLASS"), c.getString("PANE_TYPE"), c.getString("POTENCY_AMT"),
                            c.getString("ITEM_NAME"), c.getString("ITEM_POB"), c.getString("ITEM_SALE"), c.getString("AREA"), c.getString("DR_LAT_LONG")
                            , c.getString("FREQ"), c.getString("NO_VISITED"), c.getString("DR_LAT_LONG2"), c.getString("DR_LAT_LONG3"), c.getString("COLORYN")
                            , c.getString("CRM_COUNT"), c.getString("DRCAPM_GROUP"), c.getString("SHOWYN"), c.getInt("MAX_REG"), c.getString("RXGENYN")
                            , c.getString("APP_PENDING_YN"), c.getString("DRLAST_PRODUCT"));

                }

                String table2 = result.getString("Tables2");
                cbo_helper.deleteChemist();
                JSONArray jsonArray3 = new JSONArray(table2);
                for (int i = 0; i < jsonArray3.length(); i++) {
                    JSONObject c = jsonArray3.getJSONObject(i);
                    cbo_helper.insert_Chemist(c.getInt("CHEM_ID"), c.getString("CHEM_NAME"),
                            c.getString("AREA"), "", c.getString("LAST_VISIT_DATE"), c.getString("DR_LAT_LONG"),
                            c.getString("DR_LAT_LONG2"), c.getString("DR_LAT_LONG3"), c.getString("SHOWYN"));

                }

                String table3 = result.getString("Tables3");
                JSONArray jsonArray4 = new JSONArray(table3);
                cbo_helper.deleteDcrAppraisal();
                for (int i = 0; i < jsonArray4.length(); i++) {
                    JSONObject c = jsonArray4.getJSONObject(i);
                    cbo_helper.setDcrAppraisal(c.getString("PA_ID"), c.getString("PA_NAME"), c.getString("DR_CALL"), c.getString("DR_AVG"), c.getString("CHEM_CALL"), c.getString("CHEM_AVG"), "0", "", "", "", "", "", "");

                }

                String table4 = result.getString("Tables4");
                JSONArray jsonArray5 = new JSONArray(table4);
                cbo_helper.delete_phdoctoritem();
                for (int b = 0; b < jsonArray5.length(); b++) {
                    JSONObject jasonObj2 = jsonArray5.getJSONObject(b);
                    cbo_helper.insertDoctorData(jasonObj2.getInt("DR_ID"), jasonObj2.getInt("ITEM_ID"), jasonObj2.getString("ITEM_NAME"));
                }

                String table5 = result.getString("Tables5");
                JSONArray jsonArray6 = new JSONArray(table5);
                cbo_helper.delete_Doctor_Call_Remark();
                for (int b = 0; b < jsonArray6.length(); b++) {
                    JSONObject jasonObj2 = jsonArray6.getJSONObject(b);
                    cbo_helper.insertDoctorCallRemark(jasonObj2.getString("PA_ID"), jasonObj2.getString("PA_NAME"), "R");
                }


                String table6 = result.getString("Tables6");
                JSONArray jsonArray7 = new JSONArray(table6);
                cbo_helper.delete_phparty();
                for (int b = 0; b < jsonArray7.length(); b++) {
                    JSONObject jasonObj2 = jsonArray7.getJSONObject(b);
                    cbo_helper.insert_phparty(jasonObj2.getInt("PA_ID"), jasonObj2.getString("PA_NAME"),
                            jasonObj2.getInt("DESIG_ID"), jasonObj2.getString("CATEGORY"),
                            jasonObj2.getInt("HQ_ID"), jasonObj2.getString("PA_LAT_LONG"),
                            jasonObj2.getString("PA_LAT_LONG2"), jasonObj2.getString("PA_LAT_LONG3")
                            , jasonObj2.getString("SHOWYN"));
                }

                String table7 = result.getString("Tables7");
                JSONArray jsonArray8 = new JSONArray(table7);
                cbo_helper.delete_phdairy();
                for (int b = 0; b < jsonArray8.length(); b++) {
                    JSONObject jasonObj2 = jsonArray8.getJSONObject(b);
                    cbo_helper.insert_phdairy(jasonObj2.getInt("ID"), jasonObj2.getString("DAIRY_NAME"), jasonObj2.getString("DOC_TYPE"),
                            "", jasonObj2.getString("DAIRY_LAT_LONG"), jasonObj2.getString("DAIRY_LAT_LONG2"), jasonObj2.getString("DAIRY_LAT_LONG3"));
                }


                String table8 = result.getString("Tables8");
                JSONArray jsonArray9 = new JSONArray(table8);
                cbo_helper.delete_phdairy_person();
                for (int b = 0; b < jsonArray9.length(); b++) {
                    JSONObject jasonObj2 = jsonArray9.getJSONObject(b);
                    cbo_helper.insert_phdairy_person(jasonObj2.getInt("DAIRY_ID"), jasonObj2.getInt("PERSON_ID"), jasonObj2.getString("PERSON_NAME"));
                }

                String table9 = result.getString("Tables9");
                JSONArray jsonArray10 = new JSONArray(table9);
                cbo_helper.delete_phdairy_reason();
                for (int b = 0; b < jsonArray10.length(); b++) {
                    JSONObject jasonObj2 = jsonArray10.getJSONObject(b);
                    cbo_helper.insert_phdairy_reason(jasonObj2.getInt("PA_ID"), jasonObj2.getString("PA_NAME"));
                }

                String table10 = result.getString("Tables10");
                JSONArray jsonArray11 = new JSONArray(table10);
                cbo_helper.delete_Item_Stock();
                for (int b = 0; b < jsonArray11.length(); b++) {
                    JSONObject jasonObj2 = jsonArray11.getJSONObject(b);
                    cbo_helper.insert_Item_Stock(jasonObj2.getString("ITEM_ID"), jasonObj2.getInt("STOCK_QTY"));
                }

                String table11 = result.getString("Tables11");
                JSONArray jsonArray12 = new JSONArray(table11);
                cbo_helper.delete_STk_Item();
                for (int b = 0; b < jsonArray12.length(); b++) {
                    JSONObject jasonObj2 = jsonArray12.getJSONObject(b);
                    cbo_helper.insert_STk_Item(jasonObj2.getString("STK_ID"), jasonObj2.getString("ITEM_ID"), jasonObj2.getString("RATE"));
                }

                String table12 = result.getString("Tables12");
                JSONArray jsonArray13 = new JSONArray(table12);
                for (int b = 0; b < jsonArray13.length(); b++) {
                    JSONObject jasonObj2 = jsonArray13.getJSONObject(b);
                    cbo_helper.insertDoctorCallRemark(jasonObj2.getString("PA_ID"), jasonObj2.getString("PA_NAME"), "S");
                }
                /*switch (work_type_Selected){
                    case "w":
                        finish();
                        break;
                    case "l":
                        Intent intent = new Intent(getApplicationContext(), FinalSubmitDcr_new.class);
                        startActivity(intent);
                        break;
                    case "n":
                        setReultForNonWork();
                        break;
                }
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"work_type_Selected",work_type_Selected);*/

                //priyesh

                String table13 = result.getString("Tables13");
                JSONArray jsonArrayC = new JSONArray(table13);

                ArrayList<String> arrCATID = new ArrayList<String>();
                ArrayList<String> arrCATNAME = new ArrayList<String>();
                for (int i = 0; i < jsonArrayC.length(); i++) {
                    JSONObject c = jsonArrayC.getJSONObject(i);
                    arrCATID.add(c.getString("ID"));
                    arrCATNAME.add(c.getString("FIELD_NAME"));
                }

                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_CAT_ID", "" + arrCATID);
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_CAT_NAME", "" + arrCATNAME);

                //rout
                String table14 = result.getString("Tables14");
                JSONArray jsonArray14 = new JSONArray(table14);

                ArrayList<String> arrRoutID = new ArrayList<String>();
                StringBuilder strNameBuilder = new StringBuilder();
                StringBuilder strIDBuilder = new StringBuilder();
                StringBuilder strPAIDBuilder = new StringBuilder();
                ArrayList<String> arrRoutNAME = new ArrayList<String>();
                ArrayList<String> arrPAIDL = new ArrayList<String>();

                for (int i = 0; i < jsonArray14.length(); i++) {
                    String seperater = "~";
                    JSONObject c = jsonArray14.getJSONObject(i);
                    arrRoutID.add(c.getString("DISTANCE_ID"));
                    arrRoutNAME.add(c.getString("ROUTE_NAME") + "~");
                    arrRoutNAME.add(c.getString("ROUTE_NAME"));
                    arrPAIDL.add(c.getString("PA_ID"));
                    if (c.getString("DISTANCE_ID").equals("")) {
                        seperater = "";
                    }
                    strNameBuilder.append(seperater).append(c.getString("ROUTE_NAME"));
                    strIDBuilder.append(seperater).append(c.getString("DISTANCE_ID"));
                    strPAIDBuilder.append(seperater).append(c.getString("PA_ID"));

                }
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_ROUT_ID", "" + strIDBuilder);
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_ROUT_NAME", "" + strNameBuilder);
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_PA_ID_L", "" + strPAIDBuilder);


                //mr
                String table15 = result.getString("Tables15");
                JSONArray jsonArray15 = new JSONArray(table15);

                ArrayList<String> arrMRID = new ArrayList<String>();
                ArrayList<String> arrMRNAME = new ArrayList<String>();
                ArrayList<String> arrMRDES = new ArrayList<String>();
                for (int i = 0; i < jsonArray15.length(); i++) {
                    JSONObject c = jsonArray15.getJSONObject(i);
                    arrMRNAME.add(c.getString("PA_NAME"));
                    arrMRID.add(c.getString("PA_ID"));
                }
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_MR_ID", "" + arrMRID);
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("RETAILERADD_MR_NAME", "" + arrMRNAME);


                if (mHandler != null)
                    threadMsg("OK");
            } catch (JSONException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (listener != null)
                            listener.onError("Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());

                        Log.d("MYAPP", "objects are: " + e.toString());
                        //AppAlert.getInstance().getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
                        e.printStackTrace();
                    }
                });
            }

        }
        //if(progress1 != null && progress1.isShowing()){ progress1.dismiss();}
        //Log.d("MYAPP", "objects are1: " + result);


    }


    public void parser_FCM(Bundle result) {

        if (result != null) {

            try {
                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);

                JSONObject one = jsonArray1.getJSONObject(0);
                String MyDaType = one.getString("DCRID");
                threadMsg("OK");
            } catch (JSONException e) {
                Log.d("MYAPP", "objects are: " + e.toString());
                CboServices.getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
                e.printStackTrace();
            }

        }
        if (progress1 != null && progress1.isShowing()) {
            progress1.dismiss();
        }
        //Log.d("MYAPP", "objects are1: " + result);


    }

    public void DCR_COMMIT_AREA(Context context, HashMap<String, String> request, Response listener) {
        DCR_COMMIT(context, "DCR_COMMIT_7", request, listener);
    }

    public void DCR_COMMIT_ROUTE(Context context, HashMap<String, String> request, Response listener) {
        DCR_COMMIT(context, "DCR_COMMIT_ROUTE_9", request, listener);
    }

    public void DCR_COMMIT(Context context, String Method, HashMap<String, String> request, Response listener) {

        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);

        new MyAPIService(context)
                .execute(new ResponseBuilder(Method, request)
                        .setTables(tables)
                        .setDescription("Please Wait..\n" +
                                " Fetching your Utilitis for the day").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle response) {
                                /*if (listener != null)
                                    listener.onSuccess(message);*/

                                parser_submit_for_working(context, response, listener);

                            }

                            @Override
                            public void onResponse(Bundle response) {
                                // parser_submit_for_working(context,response,listener);
                            }

                            @Override
                            public void onError(String s, String s1) {
                                if (listener != null)
                                    listener.onError(s, s1);
                            }


                        })
                );
    }


    public void parser_submit_for_working(Context context, Bundle result, Response listener) {


        if (result != null) {

            try {
                String table0 = result.getString("Tables0");
                JSONArray jsonArray1 = new JSONArray(table0);
                for (int i = 0; i < jsonArray1.length(); i++) {
                    JSONObject c = jsonArray1.getJSONObject(i);
                    Custom_Variables_And_Method.DCR_ID = c.getString("DCRID");

                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "FCMHITCALLYN", c.getString("FCMHITCALLYN"));
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "FARMERREGISTERYN", c.getString("FARMERREGISTERYN"));
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DIVERTLOCKYN", c.getString("DIVERTLOCKYN"));


                    if (!c.getString("FCMHITCALLYN").equals("") && !c.getString("FCMHITCALLYN").equals("N")) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "MOBILEDATAYN", "Y");
                    }

                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "APPROVAL_MSG", "");
                    if (Custom_Variables_And_Method.DCR_ID.equals("0") && c.getString("DCRID") != null) {
                        Alert(context, "Alert !!!", c.getString("MSG"), listener);
                    } else if (c.getString("DIVERTLOCKYN").toUpperCase().equals("Y")) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "APPROVAL_MSG", c.getString("MSG"));
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DCR_ID", c.getString("DCRID"));
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DcrPlanTime_server", c.getString("IN_TIME"));
                        Alert(context, "Alert !!!", c.getString("MSG"), listener);
                    } else if (c.getString("FARMERREGISTERYN").equals("Y")) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "APPROVAL_MSG", c.getString("MSG"));
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DCR_ID", c.getString("DCRID"));
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DcrPlanTime_server", c.getString("IN_TIME"));
                        Alert(context, "Alert !!!",
                                "Today You have an activity for " +
                                        cbo_helper.getMenu("DCR", "D_FAR").get("D_FAR"), listener);
                    } else {

                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DCR_ID", c.getString("DCRID"));
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DcrPlanTime_server", c.getString("IN_TIME"));
                        DownloadAllAfterDayPlan(context, listener);
                    }
                }


            } catch (JSONException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (listener != null)
                            listener.onError("Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());

                        Log.d("MYAPP", "objects are: " + e.toString());
                        //AppAlert.getInstance().getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
                        e.printStackTrace();
                    }
                });
            }

        }


    }


    private void Alert(Context context, String title, String description, Response listener) {
        AppAlert.getInstance().Alert(context, title, description, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadAllAfterDayPlan(context, listener);
            }
        });
    }

    public void DownloadAllAfterDayPlan(Context context, Response listener) {
//        if (!(Custom_Variables_And_Method.DCR_ID.equals("0"))) {
//
//
//            cbo_helper.deletedcrFromSqlite();
//            cbo_helper.deleteUtils();
//            cbo_helper.deleteDCRDetails();
//
//            new CustomTextToSpeech().setTextToSpeech("");
//
//            cbo_helper.putDcrId(Custom_Variables_And_Method.DCR_ID);
//            long val = cbo_helper.insertUtils(Custom_Variables_And_Method.pub_area);
//            long val2 = cbo_helper.insertDcrDetails(Custom_Variables_And_Method.DCR_ID, Custom_Variables_And_Method.pub_area);
//
//
//            if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "dcr_date_real").equals("")){
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "OveAllKm", "0.0");
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DayPlanLatLong", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON));
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DcrPlantimestamp", customVariablesAndMethod.get_currentTimeStamp());
//            }
//
//            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"working_head", work_val);
//            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"working_code", work_type_code);
//
//            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"BackDateReason", late_remark.getText().toString());
//            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"sDivert_Remark", divert_remark.getText().toString());
//
//
//            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"dcr_date_real", real_date);
//            cbo_helper.putDcrId(Custom_Variables_And_Method.DCR_ID);
//            Custom_Variables_And_Method.GCMToken=customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"GCMToken");
//
//            DownloadAll(context,listener);
//
//            if ((fmcg_Live_Km.equalsIgnoreCase("Y")) || (fmcg_Live_Km.equalsIgnoreCase("5"))||(fmcg_Live_Km.equalsIgnoreCase("Y5"))) {
//                String lat, lon, time, km;
//                customVariablesAndMethod.deleteFmcg_ByKey(context,"myKm1");
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"Tracking", "Y");
//                lat = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"shareLat");
//                lon = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"shareLon");
//                time =customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,"shareMyTime");
//                km = "0.0";
//                customMethod.insertDataInOnces_Minute(lat, lon, km, time);
//
//                new Thread(r1).start();
//                new Thread(r2).start();
//            }
//
//
//            if(intent.getStringExtra("plan_type").equals("p")) {
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"Final_submit","N");
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"ACTUALFAREYN","");
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"ACTUALFARE","");
//                cbo_helper.deleteAllRecord10();
//                cbo_helper.delete_DCR_Item(null,null,null,null);
//                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "Dcr_Planed_Date", customVariablesAndMethod.currentDate());
//            }
//            //startActivity(new Intent(getApplicationContext(), ViewPager_2016.class));
//        }
    }


    public void DownloadAll(Context context, Response listener) {
        Custom_Variables_And_Method.GLOBAL_LATLON = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON);


        //Start of call to service

        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", MyCustumApplication.getInstance().getUser().getCompanyCode());
        request.put("iPA_ID", MyCustumApplication.getInstance().getUser().getID());
        request.put("sDcrId", MyCustumApplication.getInstance().getDCR().getId());
        request.put("sRouteYn", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "root_needed"));
        request.put("sGCM_TOKEN", MyCustumApplication.getInstance().getUser().getGCMToken());
        request.put("sMobileId", MyCustumApplication.getInstance().getUser().getIMEI());
        request.put("sVersion", MyCustumApplication.getInstance().getUser().getAppVersion());

        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);
        tables.add(1);
        tables.add(2);
        tables.add(3);
        tables.add(4);
        tables.add(5);
        tables.add(6);
        tables.add(7);
        tables.add(8);
        tables.add(9);
        tables.add(10);
        tables.add(11);
        tables.add(12);
        tables.add(13);
        tables.add(14);
        tables.add(15);

        new MyAPIService(context)
                .execute(new ResponseBuilder("DCRCOMMIT_DOWNLOADALL", request)
                        .setTables(tables)
                        .setDescription("Please Wait..\n" +
                                " Fetching your Utilitis for the day").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle message) {
                                if (listener != null)
                                    listener.onSuccess(message);

                            }

                            @Override
                            public void onResponse(Bundle response) {
                                parser_DCRCOMMIT_DOWNLOADALL(context, response, listener);
                            }

                            @Override
                            public void onError(String s, String s1) {
                                if (listener != null)
                                    listener.onError(s, s1);
                            }


                        })
                );
    }

    public void getListForLocal(Context context, Response listener) {


        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", cbo_helper.getCompanyCode());
        request.put("iPaId", "" + Custom_Variables_And_Method.PA_ID);

        new MyAPIService(context)
                .execute(new ResponseBuilder("GetItemListForLocal", request)
                        .setDescription("Please Wait....\nDownloading Miscellaneous data..").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle message) {
                                new DayPlanTextToSpeech().setTextToSpeech(context, "", "", null);

                                if (listener != null)
                                    listener.onSuccess(message);

                            }

                            @Override
                            public void onResponse(Bundle response) throws JSONException {
                                parser_utilites(context, response, listener);
                            }

                            @Override
                            public void onError(String s, String s1) {
                                if (listener != null)
                                    listener.onError(s, s1);
                            }


                        })
                );
    }


    public void resetDCR(Context context, Response listener) {
        mUser user = MyCustumApplication.getInstance().getUser();

        if (user.getLoggedInAsSupport()) {


            resetDCRNow(context);

            if (listener != null)
                listener.onSuccess(null);


            Intent i = new Intent(context, LoginMain.class);
            ((CustomActivity) context).stopLoctionService();
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(i);
            ((CustomActivity) context).finish();

            return;
        }


        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", user.getCompanyCode());
        request.put("DCRID", user.getDCRId());

        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);

        new MyAPIService(context)
                .execute(new ResponseBuilder("DcrReset_1", request)
                        .setDescription("Please Wait....").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle message) {
                                if (listener != null)
                                    listener.onSuccess(message);

                                Intent i = new Intent(context, LoginMain.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                ((CustomActivity) context).stopLoctionService();
                                context.startActivity(i);
                                ((CustomActivity) context).finish();

                            }

                            @Override
                            public void onResponse(Bundle response) throws Exception {
                                parser_resetDCR(context, response, listener);
                            }

                            @Override
                            public void onError(String s, String s1) {
                                if (listener != null)
                                    listener.onError(s, s1);
                            }


                        })
                );
    }


    private void parser_resetDCR(Context context, Bundle result, Response listener) throws Exception {
        if (result != null) {

            String table0 = result.getString("Tables0");
            JSONArray jsonArray1 = new JSONArray(table0);

            JSONObject c = jsonArray1.getJSONObject(0);

            if (c.getString("DCRID").equals("RESET")) {
                resetDCRNow(context);
            } else {
                throw new CBOException("Please Day plan First......");
            }

        }

    }

    public void resetDCRNow(Context context) {
        //customVariablesAndMethod.msgBox(context,"Dcr Day Successfully Reset ");
        MyCustomMethod customMethod;
        customMethod = new MyCustomMethod(context);

        customMethod.stopAlarm10Minute();
        customMethod.stopAlarm10Sec();
        customMethod.stopDOB_DOA_Remainder();
        new CustomTextToSpeech().stopTextToSpeech();
        new DayPlanTextToSpeech().stopTextToSpeech();

        new MainDB().delete(null);
        //MyCustumApplication.getInstance().updateUser();
        Custom_Variables_And_Method.DCR_ID = "0";
        MyCustumApplication.getInstance().clearApplicationData();

        cbo_helper.DropDatabase(context);

                    /*Intent i = new Intent(context, LoginMain.class);
                    stopLoctionService();
                    startActivity(i);
                    finish();*/
    }


    private void parser_utilites(Context context, Bundle result, Response listener) throws JSONException {
        if (result != null) {
/*
            try {*/

            // table 0-11 for getitemlistforlocal
            // table 12-13 for fmgcddl_2


            //getItemforLocal


            JSONArray jsonArray11 = new JSONArray(result.getString("Tables0"));
            JSONArray jsonArray12 = new JSONArray(result.getString("Tables1"));
            JSONArray jsonArray13 = new JSONArray(result.getString("Tables2"));
            JSONArray jsonArray14 = new JSONArray(result.getString("Tables3"));
            JSONArray jsonArray15 = new JSONArray(result.getString("Tables4"));
            JSONArray jsonArray16 = new JSONArray(result.getString("Tables5"));
            JSONArray jsonArray17 = new JSONArray(result.getString("Tables6"));
            JSONArray jsonArray18 = new JSONArray(result.getString("Tables7"));
            JSONArray jsonArray19 = new JSONArray(result.getString("Tables8"));
            JSONArray jsonArray20 = new JSONArray(result.getString("Tables9"));
            JSONArray jsonArray22 = new JSONArray(result.getString("Tables11"));

            cbo_helper.delete_phitem();
            for (int a = 0; a < jsonArray11.length(); a++) {
                JSONObject jasonObj1 = jsonArray11.getJSONObject(a);
                cbo_helper.insertProductsNew(jasonObj1.getString("ITEM_ID"), jasonObj1.getString("ITEM_NAME"),
                        Double.parseDouble(jasonObj1.getString("STK_RATE")), Double.parseDouble(jasonObj1.getString("CHEM_RATE")),
                        Double.parseDouble(jasonObj1.getString("DR_RATE")), jasonObj1.getString("GIFT_TYPE"),
                        jasonObj1.getString("SHOW_ON_TOP"), jasonObj1.getString("SHOW_YN"),
                        jasonObj1.getInt("SPL_ID"), jasonObj1.getString("GENERIC_NAME"), jasonObj1.getString("GIFT_CAMP"), jasonObj1.getString("BRAND_ID"), jasonObj1.getString("BRAND_NAME"));

                Log.e("%%%%%%%%%%%%%%%", "item insert");

            }
                                /*for (int b = 0; b<jsonArray2.length();b++){
                                    JSONObject jasonObj2 = jsonArray2.getJSONObject(b);
                                    val=cbohelper.insertDoctorData(jasonObj2.getString("DR_ID"), jasonObj2.getString("ITEM_ID"),jasonObj2.getString("item_name"));
                                    Log.e("%%%%%%%%%%%%%%%", "doctor insert");

                                }*/
            cbo_helper.delete_phallmst();
            for (int c = 0; c < jsonArray14.length(); c++) {

                JSONObject jsonObject3 = jsonArray14.getJSONObject(c);
                cbo_helper.insert_phallmst(jsonObject3.getInt("ID"), jsonObject3.getString("TABLE_NAME"), jsonObject3.getString("FIELD_NAME"), jsonObject3.getString("REMARK"));
                Log.e("%%%%%%%%%%%%%%%", "allmst_insert");
            }

                /*cbo_helper.delete_phparty();
                for (int d = 0; d < jsonArray15.length(); d++) {

                    JSONObject jsonObject4 = jsonArray15.getJSONObject(d);
                    cbo_helper.insert_phparty(jsonObject4.getInt("PA_ID"), jsonObject4.getString("PA_NAME"),
                            jsonObject4.getInt("DESIG_ID"), jsonObject4.getString("CATEGORY"), jsonObject4.getInt("HQ_ID")
                            , jsonObject4.getString("PA_LAT_LONG"), jsonObject4.getString("PA_LAT_LONG2"),
                            jsonObject4.getString("PA_LAT_LONG3"), jsonObject4.getString("SHOWYN"));
                    Log.e("%%%%%%%%%%%%%%%", "party_insert");

                }*/

            cbo_helper.delete_phrelation();
            for (int e = 0; e < jsonArray16.length(); e++) {

                JSONObject jsonObject5 = jsonArray16.getJSONObject(e);
                cbo_helper.insert_phrelation(jsonObject5.getInt("PA_ID"), jsonObject5.getInt("UNDER_ID"), jsonObject5.getInt("RANK"));
                Log.e("%%%%%%%%%%%%%%%", "relation_insert");

            }

            cbo_helper.delete_phitemspl();
            for (int f = 0; f < jsonArray17.length(); f++) {

                JSONObject jsonObject6 = jsonArray17.getJSONObject(f);
                cbo_helper.insert_phitempl(jsonObject6.getString("ITEM_ID"), jsonObject6.getString("DR_SPL_ID"), jsonObject6.getInt("SRNO"));
                Log.e("%%%%%%%%%%%%%%%", "" + jsonObject6.getInt("SRNO"));


            }

            cbo_helper.deleteFTPTABLE();
            for (int f = 0; f < jsonArray18.length(); f++) {

                JSONObject jsonObject7 = jsonArray18.getJSONObject(f);

                cbo_helper.insert_FtpData(jsonObject7.getString("WEB_IP"), jsonObject7.getString("WEB_USER"), jsonObject7.getString("WEB_PWD"), jsonObject7.getString("WEB_PORT"), jsonObject7.getString("WEB_ROOT_PATH"),
                        jsonObject7.getString("WEB_IP_DOWNLOAD"), jsonObject7.getString("WEB_USER_DOWNLOAD"), jsonObject7.getString("WEB_PWD_DOWNLOAD"), jsonObject7.getString("WEB_PORT_DOWNLOAD"));
                Log.e("%%%%%%%%%%%%%%%", "ftp_insert");
            }
                /*for (int g = 0; g < jsonArray20.length(); g++) {
                    JSONObject jsonObject9 = jsonArray20.getJSONObject(g);
                    count = jsonObject9.getInt("NO_DR");
                    chem_count = jsonObject9.getInt("NO_CHEM");
                }
*/
            JSONObject jsonObjectLoginUrl = jsonArray22.getJSONObject(0);
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "Login_Url", jsonObjectLoginUrl.getString("LOGIN_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DR_ADDNEW_URL", jsonObjectLoginUrl.getString("DR_ADDNEW_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CHEM_ADDNEW_URL", jsonObjectLoginUrl.getString("CHEM_ADDNEW_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DRSALE_ADDNEW_URL", jsonObjectLoginUrl.getString("DRSALE_ADDNEW_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "TP_ADDNEW_URL", jsonObjectLoginUrl.getString("TP_ADDNEW_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CHALLAN_ACK_URL", jsonObjectLoginUrl.getString("CHALLAN_ACK_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "SECONDARY_SALE_URL", jsonObjectLoginUrl.getString("SECONDARY_SALE_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "TP_APPROVE_URL", jsonObjectLoginUrl.getString("TP_APPROVE_URL"));

            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "PERSONAL_INFORMATION_URL", jsonObjectLoginUrl.getString("PERSONAL_INFORMATION_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CHANGE_PASSWORD_URL", jsonObjectLoginUrl.getString("CHANGE_PASSWORD_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CIRCULAR_URL", jsonObjectLoginUrl.getString("CIRCULAR_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DECLARATION_OF_SAVING_URL", jsonObjectLoginUrl.getString("DECLARATION_OF_SAVING_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "SALARY_SLIP_URL", jsonObjectLoginUrl.getString("SALARY_SLIP_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "FORM16_URL", jsonObjectLoginUrl.getString("FORM16_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "ROUTE_MASTER_URL", jsonObjectLoginUrl.getString("ROUTE_MASTER_URL"));
            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "HOLIDAY_URL", jsonObjectLoginUrl.getString("HOLIDAY_URL"));


            // fmcgddl_2
            parseFMCG(context, new JSONArray(result.getString("Tables12")), new JSONArray(result.getString("Tables13")), new JSONArray(result.getString("Tables14")));


            /*} catch (JSONException e) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        if (listener != null)
                            listener.onError("Missing field error",context.getResources().getString(R.string.service_unavilable) + e.toString());

                        Log.d("MYAPP", "objects are: " + e.toString());
                        //AppAlert.getInstance().getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
                        e.printStackTrace();
                    }
                });
            }*/
        }
    }


    public void parseFMCG(Context context, JSONArray controls, JSONArray menus, JSONArray DayPlanAlert) throws JSONException {
        try {
            // fmcgddl_2
            SharedPreferences.Editor editor = context.getSharedPreferences(Custom_Variables_And_Method.FMCG_PREFRENCE, context.MODE_PRIVATE).edit();

            for (int i = 0; i < controls.length(); i++) {
                JSONObject c = controls.getJSONObject(i);
                editor.putString("fmcg_value", c.getString("FMCG"));
                editor.putString("root_needed", c.getString("ROUTE"));
                editor.putString("gps_needed", c.getString("GPRSYN"));
                editor.putString("version", c.getString("VER"));
                editor.putString("doryn", c.getString("DORYN"));
                editor.putString("dosyn", c.getString("DOSYN"));
                editor.putString("internet", c.getString("INTERNET_RQD"));
                editor.putString("live_km", c.getString("LIVE_KM"));
                editor.putString("leave_yn", c.getString("LEAVEYN"));
                editor.putString("WEBSERVICE_URL", c.getString("WEBSERVICE_URL"));
                editor.putString("WEBSERVICE_URL_ALTERNATE", c.getString("WEBSERVICE_URL_ALTERNATE"));
                editor.putString("FLASHYN", c.getString("FLASHYN"));
                //editor.putString("FLASHYN", c.getString("FLASHYN"));
                editor.putString("DCR_REMARK_NA", c.getString("DCR_REMARK_NA"));
                editor.putString("DCR_DR_REMARKYN", c.getString("DCR_DR_REMARKYN"));
                editor.putString("ROUTEDIVERTYN", c.getString("ROUTEDIVERTYN"));
                editor.putString("DCR_ADDAREANA", c.getString("DCR_ADDAREANA"));
                editor.putString("VISUALAIDPDFYN", c.getString("VISUALAIDPDFYN"));
                editor.putString("SAMPLE_POB_MANDATORY", c.getString("SAMPLE_POB_MANDATORY"));
                editor.putString("REMARK_WW_MANDATORY", c.getString("REMARK_WW_MANDATORY"));
                editor.putString("SAMPLE_POB_INPUT_MANDATORY", c.getString("SAMPLE_POB_INPUT_MANDATORY"));
                editor.putString("MISSED_CALL_OPTION", c.getString("MISSED_CALL_OPTION"));
                editor.putString("APPRAISALMANDATORY", c.getString("APPRAISALMANDATORY"));
                editor.putString("USER_NAME", c.getString("USER_NAME"));
                editor.putString("PASSWORD", c.getString("PASSWORD"));
                editor.putString("VISUALAID_DRSELITEMYN", c.getString("VISUALAID_DRSELITEMYN"));
                editor.putString("DOB_REMINDER_HOUR", c.getString("DOB_REMINDER_HOUR"));
                editor.putString("SYNCDRITEMYN", c.getString("SYNCDRITEMYN"));
                editor.putString("GEO_FANCING_KM", c.getString("GEO_FANCING_KM"));
                editor.putString("FIRST_CALL_LOCK_TIME", c.getString("FIRST_CALL_LOCK_TIME"));
                editor.putString("mark", c.getString("FLASH_MESSAGE"));
                editor.putString("NOC_HEAD", c.getString("NOC_HEAD"));
                editor.putString("USER_PIC", c.getString("USER_PIC"));
                editor.putString("DCR_LETREMARK_LENGTH", c.getString("DCR_LETREMARK_LENGTH"));
                editor.putString("SAMPLEMAXQTY", c.getString("SAMPLEMAXQTY"));
                editor.putString("POBMAXQTY", c.getString("POBMAXQTY"));
                editor.putString("ASKUPDATEYN", c.getString("ASKUPDATEYN"));
                editor.putString("MOBILEDATAYN", c.getString("MOBILEDATAYN"));
                editor.putString("CALLWAITINGTIME", c.getString("CALLWAITINGTIME"));
                editor.putString("COMPANY_PIC", c.getString("COMPANY_PIC"));
                editor.putString("COMPANY_PICYN", c.getString("COMPANY_PICYN"));
                editor.putString("RE_REG_KM", c.getString("RE_REG_KM"));
                editor.putString("ERROR_EMAIL", c.getString("ERROR_EMAIL"));
                editor.putString("DIVERT_REMARKYN", c.getString("DIVERT_REMARKYN"));
                editor.putString("NLC_PIC_YN", c.getString("NLC_PIC_YN"));
                editor.putString("RX_MAX_QTY", c.getString("RX_MAX_QTY"));
                editor.putString("SHOW_ADD_REGYN", c.getString("SHOW_ADD_REGYN"));
                editor.putString("EXP_ATCH_YN", c.getString("EXP_ATCH_YN"));
                editor.putString("FARMERADDFIELDYN", c.getString("FARMERADDFIELDYN"));
                editor.putString("NO_DR_CALL_REQ", c.getString("NO_DR_CALL_REQ"));
                editor.putString("DR_RX_ENTRY_YN", c.getString("DR_RX_ENTRY_YN"));
                editor.putString("RETAILERCHAINYN", c.getString("RETAILERCHAINYN"));
                editor.putString("DCR_SUBMIT_TIME", c.getString("DCR_SUBMIT_TIME"));
                editor.putString("DCR_SUBMIT_SPEACH", c.getString("DCR_SUBMIT_SPEACH"));
                editor.putString("ALLOWED_APP", c.getString("ALLOWED_APP"));
                editor.putString("DCRGIFT_QTY_VALIDATE", c.getString("DCRGIFT_QTY_VALIDATE"));
                editor.putString("SAMPLE_BTN_CAPTION", c.getString("SAMPLE_BTN_CAPTION"));
                editor.putString("GIFT_BTN_CAPTION", c.getString("GIFT_BTN_CAPTION"));
                editor.putString("DIVERTWWYN", c.getString("DIVERTWWYN"));
                editor.putString("PIN_ALLOWED_MSG", c.getString("PIN_ALLOWED_MSG"));
                editor.putString("CMC3_GALLERY_REQ", c.getString("CMC3_GALLERY_REQ"));
                editor.putString("DR_COLOR", c.getString("DR_COLOR"));
                editor.putString("DCRPPNA", c.getString("DCRPPNA"));
                editor.putString("DR_SALE_URL", c.getString("DR_SALE_URL"));
                editor.putString("REG_ADDRESS_KM", c.getString("REG_ADDRESS_KM"));
                editor.putString("DR_DIVISION_FILTER_YN", c.getString("DR_DIVISION_FILTER_YN"));
                editor.putString("DR_RXGEN_VALIDATE", c.getString("DR_RXGEN_VALIDATE"));
                editor.putString("FIREBASE_SYNCYN", c.getString("FIREBASE_SYNCYN"));


                editor.putString("DCRDRADDAREA_APP_MSG", c.getString("DCRDRADDAREA_APP_MSG"));
                editor.putString("DRGIFTMANDATORY", c.getString("DRGIFTMANDATORY"));
                editor.putString("DCR_MULTIPLE_ROUTEYN", c.getString("DCR_MULTIPLE_ROUTEYN"));
                editor.putString("DCR_LEAD_ENTRY_YN", c.getString("DCR_LEAD_ENTRY_YN"));
                editor.putString("DCR_CALL_STATUS_YN", c.getString("DCR_CALL_STATUS_YN"));
                editor.putString("FY_FDATE", c.getString("FY_FDATE"));
                editor.putString("FY_TDATE", c.getString("FY_TDATE"));
                editor.putString("ORD_DISC_TYPE", c.getString("ORD_DISC_TYPE"));
                editor.putString("ORD_DISC_EDITCOLS", c.getString("ORD_DISC_EDITCOLS"));
                editor.putString("SALE_ORDER_REMARKYN", c.getString("SALE_ORDER_REMARKYN"));
                editor.putString("SALE_ORDER_REMARK_TITLE", c.getString("SALE_ORDER_REMARK_TITLE"));
                editor.putString("INDEPENDENT_WORKING", c.getString("INDEPENDENT_WORKING"));
                editor.putString("RXQTYYN", c.getString("RXQTYYN"));
                editor.putString("GIFTSHOW_STOCKONLYYN", c.getString("GIFTSHOW_STOCKONLYYN"));
                editor.putString("DCRCALL_ANYTIMEYN", c.getString("DCRCALL_ANYTIMEYN"));
                editor.putString("CENTROID_METER", c.getString("CENTROID_METER"));
                editor.putString("DCRCALL_ANYTIMEYN", c.getString("DCRCALL_ANYTIMEYN"));
                editor.putString("CENTROID_METER", c.getString("CENTROID_METER"));

                editor.putString("DRSALEMSG_FINALSUBMITYN", c.getString("DRSALEMSG_FINALSUBMITYN"));

                editor.putString("DASHBOARD_TYPE", c.getString("DASHBOARD_TYPE"));
                editor.putString("DAYPLAN_REMINDER_FTIME", c.getString("DAYPLAN_REMINDER_FTIME"));
                editor.putString("VISUALAID_SELECTEDONLY", c.getString("VISUALAID_SELECTEDONLY"));
                editor.putString("DR_SIGNATURE_YN", c.getString("DR_SIGNATURE_YN"));
                editor.putString("DR_SIGNATURE_MANDATORYYN", c.getString("DR_SIGNATURE_MANDATORYYN"));
                editor.putString("REGBYPICYN", c.getString("REGBYPICYN"));
                editor.putString("ATTENDANCE_UPLOAD_DIRECTORY", c.getString("ATTENDANCE_UPLOAD_DIRECTORY"));
                editor.putString("DCRDRREG_RESETYN", c.getString("DCRDRREG_RESETYN"));
                editor.putString("DCRSAMPLE_AFTERVISUALAIDYN", c.getString("DCRSAMPLE_AFTERVISUALAIDYN"));
                editor.putString("DRCALLPRODUCT_MANDATORYYN", c.getString("DRCALLPRODUCT_MANDATORYYN"));
                editor.putString("API_URL", c.getString("API_URL"));
                editor.putString("API_TOKEN", c.getString("API_TOKEN"));
                editor.putString("API_URL_MOBILE", c.getString("API_URL_MOBILE"));
                editor.putString("CHEMIST_OFFLINEYN", c.getString("CHEMIST_OFFLINEYN"));
                editor.putString("VISUALAID_VERSION", c.getString("VISUALAID_VERSION"));
                editor.putString("EXPTA_MGRMANUALYN", c.getString("EXPTA_MGRMANUALYN"));
                editor.putString("GEO_FANCING_KM_FOR", c.getString("GEO_FANCING_KM_FOR"));
                editor.putString("DCRSUMMARYONFINALSUBMIT", c.getString("DCRSUMMARYONFINALSUBMIT"));
                editor.putString("GOOGLEMAPAPI_KEY", c.getString("GOOGLEMAPAPI_KEY"));

                editor.apply();

                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("DASHBOARD_DATE", "");

                Controls.getInstance().setGiftCampaignWiseReqd(c.getString("GIFTCAMP_WISEYN"));
                Controls.getInstance().setGpsRequired(c.getString("GPRSYN"));
                Controls.getInstance().setRouteWise(c.getString("ROUTE"));
                Controls.getInstance().setOfflineCallAllowed(c.getString("OFFLINE_CALLYN"));
                Controls.getInstance().setSampleGiftAllowedInDcrCall(c.getString("DCRCALLGIFT_SAMPLEYN"));

            }

            cbo_helper.deleteMenu();

            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("MENU_SYNC_FAILED", "Y");
            for (int i = 0; i < menus.length(); i++) {
                JSONObject object = menus.getJSONObject(i);
                String menu = object.getString("MAIN_MENU");
                String menu_code = object.getString("MENU_CODE");
                String menu_name = object.getString("MENU_NAME");
                String menu_url = object.getString("URL");
                String main_menu_srno = object.getString("MAIN_MENU_SRNO");
                cbo_helper.insertMenu(menu, menu_code, menu_name, menu_url, main_menu_srno);
            }
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("MENU_SYNC_FAILED", "N");

            if (controls.length() > 0) {
                DayPlanAlertDB dayPlanAlertDB = new DayPlanAlertDB(context);
                dayPlanAlertDB.delete();
                JSONObject control = controls.getJSONObject(0);
                for (int i = 0; i < DayPlanAlert.length(); i++) {
                    JSONObject object = DayPlanAlert.getJSONObject(i);
                    String DAYPLAN_REMINDER_VOICE = control.getString("DAYPLAN_REMINDER_VOICE");
                    String DAYPLAN_REMINDER_INTERVAL = control.getString("DAYPLAN_REMINDER_INTERVAL");
                    String DAYPLAN_REMINDER_TTIME = control.getString("DAYPLAN_REMINDER_TTIME");
                    String DAYPLAN_REMINDER_FTIME = control.getString("DAYPLAN_REMINDER_FTIME");
                    String DCR_DATE = object.getString("DCR_DATE");
                    dayPlanAlertDB.insert(DCR_DATE, DAYPLAN_REMINDER_FTIME, DAYPLAN_REMINDER_TTIME, DAYPLAN_REMINDER_INTERVAL, DAYPLAN_REMINDER_VOICE);
                }
            }


//            if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("DCRSAMPLE_AFTERVISUALAIDYN", "N").equalsIgnoreCase("Y")) {
//                if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", "").equalsIgnoreCase("")) {
//                    Intent intent = new Intent(context, VisualAdsDownloadActivity.class);
//                    intent.putExtra("V_DOWNLOAD", "Y");
//                    context.startActivity(intent);
////                    MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", ""));
//                } else if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", "").equalsIgnoreCase(MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", ""))) {
//                    Intent intent = new Intent(context, VisualAdsDownloadActivity.class);
//                    intent.putExtra("V_DOWNLOAD", "Y");
//                    context.startActivity(intent);
//
//                }
//            }


            //return true;
        } catch (JSONException e) {
            throw e;
            /*progress1.dismiss();
            Log.d("MYAPP", "objects are: " + e.toString());
            CboServices.getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
            e.printStackTrace();*/
            //return false;
        }


    }


    public void CheckIfCallsUnlocked(Context context, String type) {


        //Start of call to service

        HashMap<String, String> request = new HashMap<>();
        request.put("sCompanyFolder", cbo_helper.getCompanyCode());
        request.put("iPA_ID", "" + Custom_Variables_And_Method.PA_ID);
        request.put("iDCR_ID", "" + Custom_Variables_And_Method.DCR_ID);
        request.put("sTYPE", type);
        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);

        new MyAPIService(context)
                .execute(new ResponseBuilder("CallUnlockStatus", request)
                        .setTables(tables)
                        .setDescription("Please Wait....\nChecking your DCR Status..").setResponse(new CBOServices.APIResponse() {
                            @Override
                            public void onComplete(Bundle message) {
                                parser_is_call_unlocked(context, type, message);

                            }

                            @Override
                            public void onResponse(Bundle response) throws JSONException {
                            }

                            @Override
                            public void onError(String title, String description) {
                                AppAlert.getInstance().getAlert(context, title, description);
                            }


                        })
                );


        //End of call to service
    }

    private void parser_is_call_unlocked(Context context, String CheckType, Bundle result) {

        if (result != null) {

            try {
                String table0 = result.getString("Tables0");
                JSONArray jsonArray0 = new JSONArray(table0);
                if (CheckType.equals("ADDAREA")) {
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").equalsIgnoreCase("Y")) {

                        cbo_helper.doctorApproved("0");

                        AppAlert.getInstance().getAlert(context, "Approved !!!", "Your Additional Area have been Approved \nYou can please proceed");
                    } else {
                        if (jsonArray0.getJSONObject(0).getString("TITLE").isEmpty()) {
                            AppAlert.getInstance().getAlert(context, "Approval Pending !!!", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,
                                    "DCRDRADDAREA_APP_MSG", "Your Additional Area Approval is Pending... \nYou Additional Area must be approved first !!!\n" +
                                            "Please contact your Head-Office for APPROVAL"));

                        } else {
                            AppAlert.getInstance().Alert(context, jsonArray0.getJSONObject(0).getString("TITLE"),
                                    jsonArray0.getJSONObject(0).getString("MSG"),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                if (jsonArray0.getJSONObject(0).getString("CODE").equalsIgnoreCase("REPLAN")) {
                                                    context.startActivity(new Intent(context, GetDCR.class));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }


                    }
                } else if (CheckType.equals("A")) {
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").contains("[DIVERT_UNLOCK]")) {

                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DIVERTLOCKYN", "");
                        AppAlert.getInstance().getAlert(context, "Approved !!!", "Your Calls have been Approved \nYou can please proceed");
                    } else {

                        if (jsonArray0.getJSONObject(0).getString("TITLE").isEmpty()) {
                            AppAlert.getInstance().getAlert(context, "Approval !!!", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,
                                    "APPROVAL_MSG", "Your Route Approval is Pending... \nYou Route must be approved first !!!\n" +
                                            "Please contact your Head-Office for APPROVAL"));

                        } else {
                            AppAlert.getInstance().Alert(context, jsonArray0.getJSONObject(0).getString("TITLE"),
                                    jsonArray0.getJSONObject(0).getString("MSG"),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                if (jsonArray0.getJSONObject(0).getString("CODE").equalsIgnoreCase("REPLAN")) {
                                                    context.startActivity(new Intent(context, GetDCR.class));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
// customVariablesAndMethod.getAlert(context,"CALL LOCKED","Your Calls has not been Unlocked yet \nPlease contact your administrator to proceed");
                    }
                } else {
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").contains("[CALL_UNLOCK]")) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CALL_UNLOCK_STATUS", "[CALL_UNLOCK]");
                        AppAlert.getInstance().getAlert(context, "CALL UNLOCKED", "Your Calls have been Unlocked \nYou can please proceed");
                    } else {

                        if (jsonArray0.getJSONObject(0).getString("TITLE").isEmpty()) {
                            Float FIRST_CALL_LOCK_TIME = Float.valueOf(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "FIRST_CALL_LOCK_TIME", "0"));
                            AppAlert.getInstance().getAlert(context, "CALL LOCKED", "Your Calls has been Locked... \nYou must have made your first Call before " + FIRST_CALL_LOCK_TIME + " O'clock\n" +
                                    "Please contact your Head-Office to UNLOCK");
// customVariablesAndMethod.getAlert(context,"CALL LOCKED","Your Calls has not been Unlocked yet \nPlease contact your administrator to proceed");

                        } else {
                            AppAlert.getInstance().Alert(context, jsonArray0.getJSONObject(0).getString("TITLE"),
                                    jsonArray0.getJSONObject(0).getString("MSG"),
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            try {
                                                if (jsonArray0.getJSONObject(0).getString("CODE").equalsIgnoreCase("REPLAN")) {
                                                    context.startActivity(new Intent(context, GetDCR.class));
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }


                    }
                }

//progress1.dismiss();
            } catch (JSONException e) {
                Log.d("MYAPP", "objects are: " + e.toString());
                AppAlert.getInstance().getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString());
                e.printStackTrace();
            }

        }
//Log.d("MYAPP", "objects are1: " + result);
//progress1.dismiss();

    }

   /* private void parser_is_call_unlocked(Context context,String CheckType,Bundle result) {

        if (result!=null ) {

            try {
                String table0 = result.getString("Tables0");
                JSONArray jsonArray0 = new JSONArray(table0);
                if (CheckType.equals("ADDAREA")){
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").equalsIgnoreCase("Y")) {

                        cbo_helper.doctorApproved("0");

                        AppAlert.getInstance().getAlert(context, "Approved !!!", "Your Additional Area have been Approved \nYou can please proceed");
                    } else {

                        AppAlert.getInstance().getAlert(context, "Approval Pending !!!", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,
                                "DCRDRADDAREA_APP_MSG","Your Additional Area Approval is Pending... \nYou Additional Area must be approved first !!!\n" +
                                        "Please contact your Head-Office for APPROVAL"));

                    }
                }else if (CheckType.equals("A")){
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").contains("[DIVERT_UNLOCK]")) {

                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "DIVERTLOCKYN","" );
                        AppAlert.getInstance().getAlert(context, "Approved !!!", "Your Calls have been Approved \nYou can please proceed");
                    } else {

                        AppAlert.getInstance().getAlert(context, "Approval !!!", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context,
                                "APPROVAL_MSG","Your Route Approval is Pending... \nYou Route must be approved first !!!\n" +
                                        "Please contact your Head-Office for APPROVAL"));
                        // customVariablesAndMethod.getAlert(context,"CALL LOCKED","Your Calls has not been Unlocked yet \nPlease contact your administrator to proceed");
                    }
                }else  {
                    if (jsonArray0.length() > 0 && jsonArray0.getJSONObject(0).getString("CALL_UNLOCK").contains("[CALL_UNLOCK]")) {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "CALL_UNLOCK_STATUS", "[CALL_UNLOCK]");
                        AppAlert.getInstance().getAlert(context, "CALL UNLOCKED", "Your Calls have been Unlocked \nYou can please proceed");
                    } else {
                        Float FIRST_CALL_LOCK_TIME = Float.valueOf(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "FIRST_CALL_LOCK_TIME", "0"));
                        AppAlert.getInstance().getAlert(context, "CALL LOCKED", "Your Calls has been Locked... \nYou must have made your first Call before " + FIRST_CALL_LOCK_TIME + " O'clock\n" +
                                "Please contact your Head-Office to UNLOCK");
                        // customVariablesAndMethod.getAlert(context,"CALL LOCKED","Your Calls has not been Unlocked yet \nPlease contact your administrator to proceed");
                    }
                }

                //progress1.dismiss();
            } catch (JSONException e) {
                Log.d("MYAPP", "objects are: " + e.toString());
                AppAlert.getInstance().getAlert(context,"Missing field error",context.getResources().getString(R.string.service_unavilable) +e.toString());
                e.printStackTrace();
            }

        }
        //Log.d("MYAPP", "objects are1: " + result);
        //progress1.dismiss();

    }*/

}
