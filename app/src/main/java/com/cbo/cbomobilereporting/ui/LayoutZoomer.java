package com.cbo.cbomobilereporting.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.ui_new.Model.mSPO;
import com.uenics.javed.CBOLibrary.CBOServices;
import com.uenics.javed.CBOLibrary.ResponseBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import pl.polidea.view.ZoomView;
import services.MyAPIService;
import services.ServiceHandler;
import utils.MyConnection;
import utils.adapterutils.SpoModel;
import utils.adapterutils.SpoRptAdapter;
import utils_new.Custom_Variables_And_Method;

/**
 * Created by RAM on 9/1/15.
 */
public class LayoutZoomer extends AppCompatActivity {

    LinearLayout mainContainer;
    Context context;
    ProgressDialog pd;
    ServiceHandler myService;
    MyConnection myCon;
    Custom_Variables_And_Method customVariablesAndMethod;
    CBO_DB_Helper myCbo_help;
    mSPO _mSPO = null;
    //String extraPID;
    //public String extraFrom,extraTo,CurrencyType;
    ArrayList<Map<String, String>> data = new ArrayList<Map<String, String>>();
    SpoRptAdapter spoRptAdapter;
    ArrayList<SpoModel> dataList = new ArrayList<SpoModel>();
    SimpleAdapter simpleAdapter;
    LinkedHashMap<String, ArrayList<String>> data1;
    ArrayList<String> Consignee, Sales_Amount, Sales_Return, Breakage_Expiry, Credit_Note_Other, Net_Sales, Secondary_Sales, Receipt, Outstanding, Stock_Amount;
    ListView listView;
    private ZoomView zoomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.zoomer_layout);


        androidx.appcompat.widget.Toolbar toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_hadder);
        TextView hader_text = (TextView) findViewById(R.id.hadder_text_1);

        SpoRptAdapter.clickCount = 0;
        hader_text.setText("SPO Consignee Wise Report");
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }

        context = LayoutZoomer.this;
        pd = new ProgressDialog(context);
        myService = new ServiceHandler(context);
        myCon = new MyConnection(context);
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        myCbo_help = new CBO_DB_Helper(context);
        Intent intent = getIntent();
        _mSPO = (mSPO) intent.getSerializableExtra("mSPO");
        _mSPO.setType(mSPO.eSPO.CONSIGNEE);

        /*extraPID = intent.getStringExtra("uid");
        extraFrom = intent.getStringExtra("mIdFrom");
        extraTo = intent.getStringExtra("mIdTo");
        CurrencyType = intent.getStringExtra("CurrencyType");*/

        //spoRptAdapter = new SpoRptAdapter(context,dataList);


        View v = ((LayoutInflater) getSystemService(context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.spo_report_details,
                null, false);

        v.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT));

        zoomView = new ZoomView(this);
        zoomView.addView(v);

        mainContainer = (LinearLayout) findViewById(R.id.main_container_zoom);

        mainContainer.addView(zoomView);
        listView = (ListView) v.findViewById(R.id.spo_cnf_list);


//        new SpoReportBackgroundTask().execute();
        getSPOData(_mSPO);

    }

    public void getSPOData(mSPO spo) {

        HashMap<String, String> request = new HashMap<>();

        request.put("sCompanyFolder", MyCustumApplication.getInstance().getUser().getCompanyCode());
        request.put("iPA_ID", MyCustumApplication.getInstance().getUser().getID());
        request.put("sFDATE", spo.getFDate());
        request.put("sTDATE", spo.getTDate());
        request.put("sType", spo.getType().getValue());
        request.put("iCompanyId", spo.getConsigneeId());
        request.put("iHqId", spo.getHqId());
        request.put("sCurrencyType", spo.getCurrencyType());
        request.put("iSTK_ID", spo.getStkId());
        request.put("iSPL_ID", spo.getDiId());


        ArrayList<Integer> tables = new ArrayList<>();
        tables.add(0);

        new MyAPIService(context).execute(new ResponseBuilder("SpoCNFGrid_3", request)
                .setMultiTable(false)
                .setTables(tables)
                .setDescription("Please Wait...")
                .setResponse(new CBOServices.APIResponse() {
                    @Override
                    public void onComplete(Bundle bundle) throws Exception {

                        if (dataList.size() == 2 && Custom_Variables_And_Method.pub_desig_id.equalsIgnoreCase("1")) {

                            String spoIdFromList = dataList.get(0).getId();

                            if (!spoIdFromList.equals("0")) {
                                if (SpoRptAdapter.clickCount < 3) {
                                    SpoRptAdapter.clickCount = SpoRptAdapter.clickCount + 1;
                                }

                                Intent spoHeadquarterWise = new Intent(context, SpoHeadquarterWise.class);
                                spoHeadquarterWise.putExtra("spoId", spoIdFromList);
                                _mSPO.setType(mSPO.eSPO.HEADQUATER);
                                _mSPO.setConsigneeId(spoIdFromList);
                                //_mSPO.setHqId("0");
                                _mSPO.setStkId("0");
                                spoHeadquarterWise.putExtra("mSPO", _mSPO);
                                context.startActivity(spoHeadquarterWise);
                            }
                            finish();
                        } else {
                            spoRptAdapter = new SpoRptAdapter(context, dataList, _mSPO);
                            listView.setAdapter(spoRptAdapter);
                        }
                    }

                    @Override
                    public void onResponse(Bundle result) throws Exception {

                        parseJson(result);
                    }

                    @Override
                    public void onError(String s, String s1) {

                    }
                })
        );

    }

    private void parseJson(Bundle result) throws Exception {

        data.clear();
        dataList.clear();
        Consignee = new ArrayList<String>();
        Sales_Amount = new ArrayList<String>();
        Sales_Return = new ArrayList<String>();
        Breakage_Expiry = new ArrayList<String>();
        Credit_Note_Other = new ArrayList<String>();
        Net_Sales = new ArrayList<String>();
        Secondary_Sales = new ArrayList<String>();
        Receipt = new ArrayList<String>();
        Outstanding = new ArrayList<String>();
        Stock_Amount = new ArrayList<String>();
        String table = result.getString("Tables0");
        JSONArray jsonArray = new JSONArray(table);

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject object = jsonArray.getJSONObject(i);

            SpoModel spoModel = new SpoModel();

            String id = object.getString("ID");
            spoModel.setId(id);

            String con = object.getString("COMPANY_NAME");
            spoModel.setConsignee(con);
            Consignee.add(con);

            String salAmt = object.getString("SALE_AMT");
            spoModel.setSalAmt(salAmt);
            Sales_Amount.add(salAmt);

            String salReturn = object.getString("SALER_AMT");
            spoModel.setSaleReturn(salReturn);
            Sales_Return.add(salReturn);

            String breakExp = object.getString("SALER_BR_AMT");
            spoModel.setBreageExpiry(breakExp);
            Breakage_Expiry.add(breakExp);

            String cridtOther = object.getString("CN_AMT");
            Credit_Note_Other.add(cridtOther);
            spoModel.setCreditNotOrther(cridtOther);

            String netSales = object.getString("NET_SALE_AMT");
            spoModel.setNetSales(netSales);
            Net_Sales.add(netSales);

            String secSales = object.getString("SEC_AMT");
            spoModel.setSecSales(secSales);
            Secondary_Sales.add(secSales);

            String recipict = object.getString("RCPT_AMT");
            spoModel.setRecipt(recipict);
            Receipt.add(recipict);

            String outStanding = object.getString("OUTST_AMT");
            spoModel.setOutStanding(outStanding);
            Outstanding.add(outStanding);

            String stkAmt = object.getString("STOCK_AMT");
            spoModel.setStockAmt(stkAmt);
            Stock_Amount.add(stkAmt);

            dataList.add(spoModel);


        }
        data1 = new LinkedHashMap<String, ArrayList<String>>();
        data1.put("Consignee", Consignee);
        data1.put("Sales Amount", Sales_Amount);
        data1.put("Sales Return", Sales_Return);
        data1.put("Breakage Expiry", Breakage_Expiry);
        data1.put("Credit Note Other", Credit_Note_Other);
        data1.put("Net Sales", Net_Sales);
        data1.put("Secondary Sales", Secondary_Sales);
        data1.put("Receipt", Receipt);
        data1.put("Outstanding", Outstanding);
        data1.put("Stock Amount", Stock_Amount);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.share) {
            myCon.create_xml(data1, "SPO Consignee Wise Report");
        } else {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private class SpoReportBackgroundTask extends AsyncTask<String, String, String> {


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("CBO");
            pd.setMessage("Processing Please Wait.....");
            pd.setCancelable(false);
            pd.setCanceledOnTouchOutside(false);
            pd.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if ((s != null) || (s.contains("ERROR"))) {

                try {
                    data.clear();
                    dataList.clear();
                    Consignee = new ArrayList<String>();
                    Sales_Amount = new ArrayList<String>();
                    Sales_Return = new ArrayList<String>();
                    Breakage_Expiry = new ArrayList<String>();
                    Credit_Note_Other = new ArrayList<String>();
                    Net_Sales = new ArrayList<String>();
                    Secondary_Sales = new ArrayList<String>();
                    Receipt = new ArrayList<String>();
                    Outstanding = new ArrayList<String>();
                    Stock_Amount = new ArrayList<String>();
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray jsonArray = jsonObject.getJSONArray("Tables0");

                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject object = jsonArray.getJSONObject(i);

                        SpoModel spoModel = new SpoModel();

                        String id = object.getString("ID");
                        spoModel.setId(id);

                        String con = object.getString("COMPANY_NAME");
                        spoModel.setConsignee(con);
                        Consignee.add(con);

                        String salAmt = object.getString("SALE_AMT");
                        spoModel.setSalAmt(salAmt);
                        Sales_Amount.add(salAmt);

                        String salReturn = object.getString("SALER_AMT");
                        spoModel.setSaleReturn(salReturn);
                        Sales_Return.add(salReturn);

                        String breakExp = object.getString("SALER_BR_AMT");
                        spoModel.setBreageExpiry(breakExp);
                        Breakage_Expiry.add(breakExp);

                        String cridtOther = object.getString("CN_AMT");
                        Credit_Note_Other.add(cridtOther);
                        spoModel.setCreditNotOrther(cridtOther);

                        String netSales = object.getString("NET_SALE_AMT");
                        spoModel.setNetSales(netSales);
                        Net_Sales.add(netSales);

                        String secSales = object.getString("SEC_AMT");
                        spoModel.setSecSales(secSales);
                        Secondary_Sales.add(secSales);

                        String recipict = object.getString("RCPT_AMT");
                        spoModel.setRecipt(recipict);
                        Receipt.add(recipict);

                        String outStanding = object.getString("OUTST_AMT");
                        spoModel.setOutStanding(outStanding);
                        Outstanding.add(outStanding);

                        String stkAmt = object.getString("STOCK_AMT");
                        spoModel.setStockAmt(stkAmt);
                        Stock_Amount.add(stkAmt);

                        dataList.add(spoModel);


                    }
                    data1 = new LinkedHashMap<String, ArrayList<String>>();
                    data1.put("Consignee", Consignee);
                    data1.put("Sales Amount", Sales_Amount);
                    data1.put("Sales Return", Sales_Return);
                    data1.put("Breakage Expiry", Breakage_Expiry);
                    data1.put("Credit Note Other", Credit_Note_Other);
                    data1.put("Net Sales", Net_Sales);
                    data1.put("Secondary Sales", Secondary_Sales);
                    data1.put("Receipt", Receipt);
                    data1.put("Outstanding", Outstanding);
                    data1.put("Stock Amount", Stock_Amount);

                    pd.dismiss();
                    if (dataList.size() == 2 && Custom_Variables_And_Method.pub_desig_id.equalsIgnoreCase("1")) {

                        String spoIdFromList = dataList.get(0).getId();
                        if (SpoRptAdapter.clickCount < 3) {
                            SpoRptAdapter.clickCount = SpoRptAdapter.clickCount + 1;
                        }
                        if (!spoIdFromList.equals("0")) {
                            Intent spoHeadquarterWise = new Intent(context, SpoHeadquarterWise.class);
                            spoHeadquarterWise.putExtra("spoId", spoIdFromList);
                            _mSPO.setType(mSPO.eSPO.HEADQUATER);
                            _mSPO.setConsigneeId(spoIdFromList);
                            //_mSPO.setHqId("0");
                            _mSPO.setStkId("0");
                            spoHeadquarterWise.putExtra("mSPO", _mSPO);
                            context.startActivity(spoHeadquarterWise);
                        }
                        finish();
                    } else {
                        spoRptAdapter = new SpoRptAdapter(context, dataList, _mSPO);
                        listView.setAdapter(spoRptAdapter);
                    }


                } catch (JSONException jsonE) {
                    pd.dismiss();
                    customVariablesAndMethod.msgBox(context, "Json Error" + jsonE);
                }


            } else {
                pd.dismiss();
                customVariablesAndMethod.msgBox(context, "Error Found...");
            }


        }

        @Override
        protected String doInBackground(String... params) {

            String responseFromSpo = myService.getResponse_SpoCNFGrid(myCbo_help.getCompanyCode(),
                    "" + Custom_Variables_And_Method.PA_ID,
                    _mSPO.getFDate(), _mSPO.getTDate(), _mSPO.getType().getValue(), _mSPO.getConsigneeId(),
                    _mSPO.getHqId(), _mSPO.getCurrencyType(), _mSPO.getStkId());

            return responseFromSpo;
        }


    }


}
