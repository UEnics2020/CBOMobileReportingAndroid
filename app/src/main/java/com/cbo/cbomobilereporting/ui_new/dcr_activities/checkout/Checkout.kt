package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkout

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import async.CBOFinalTask_New
import async.CBOFinalTasks
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.cbo.cbomobilereporting.databaseHelper.Call.Db.MainDB
import com.cbo.cbomobilereporting.emp_tracking.MyCustomMethod
import com.cbo.cbomobilereporting.ui_new.AttachImage
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import com.cbo.cbomobilereporting.ui_new.dcr_activities.GetDCR
import com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin.VmCheckIn
import com.cbo.myattachment.AttachFile
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.CboProgressDialog
import com.uenics.javed.CBOLibrary.ResponseBuilder
import kotlinx.android.synthetic.main.activity_checkout.*
import locationpkg.Const
import org.json.JSONArray
import org.json.JSONException
import services.MyAPIService
import utils.networkUtil.AppPrefrences
import utils.networkUtil.NetworkUtil
import utils_new.*
import utils_new.cboUtils.CBOImageView
import java.util.*


class Checkout : CustomActivity(), ICheckOut, up_down_ftp.AdapterCallback {

    private lateinit var mVmCheckOut: VmCheckOut
    lateinit var remark: EditText
    internal lateinit var Late_Submit_remark: EditText
    internal lateinit var etTime: EditText

    lateinit var networkUtil: NetworkUtil
    lateinit var submit: Button
    var dcr_id = ""
    var address = ""


    var DCR_ID = ""
    var dr_id = ""
    lateinit var cbohelp: CBO_DB_Helper
    lateinit var mRemark: String
    var back_allowed = "Y"
    lateinit var DCR_REMARK_NA: String

    lateinit var service: Service_Call_From_Multiple_Classes
    lateinit var customMethod: MyCustomMethod
    private var currentBestLocation: Location? = null


    private var cboFinalTask_new: CBOFinalTask_New? = null
    internal var dcr_latCommit: Map<String, String> = HashMap()
    internal var dcr_Commititem: Map<String, String> = HashMap()
    internal var dcr_Commit_rx: Map<String, String> = HashMap()
    internal var dcr_CommitDr: Map<String, String> = HashMap()
    internal var dcr_ChemistCommit: Map<String, String> = HashMap()
    internal var dcr_StkCommit: Map<String, String> = HashMap()
    internal var dcr_CommitDr_Reminder: Map<String, String> = HashMap()
    internal var Lat_Long_Reg: Map<String, String> = HashMap()
    internal var dcr_Dairy: Map<String, String> = HashMap()
    internal var dcr_RxCalls: Map<String, String> = HashMap()
    internal var dcr_CommitChem_Reminder: Map<String, String> = HashMap()

    internal var sb_DCRLATCOMMIT_KM: String? = null
    internal var sb_DCRLATCOMMIT_LOC_LAT: String? = null
    internal var sb_sDCRLATCOMMIT_IN_TIME: String? = null
    internal var sDCRLATCOMMIT_ID: String? = null
    internal var sDCRLATCOMMIT_LOC: String? = null
    internal var sDCRITEM_DR_ID: String? = null
    internal var sDCRITEM_ITEMIDIN: String? = null
    internal var sDCRITEM_ITEM_ID_ARR: String? = null
    internal var sDCRITEM_QTY_ARR: String? = null
    internal var sDCRITEM_ITEM_ID_GIFT_ARR: String? = null
    internal var sDCRITEM_QTY_GIFT_ARR: String? = null
    internal var sDCRITEM_POB_QTY: String? = null
    internal var sDCRITEM_POB_VALUE: String? = null
    internal var sDCRITEM_VISUAL_ARR: String? = null
    var sDCRITEM_NOC_ARR: String? = null
    internal var sDCRDR_DR_ID: String? = null
    internal var sDCRDR_WW1: String? = null
    internal var sDCRDR_WW2: String? = null
    internal var sDCRDR_WW3: String? = null
    internal var sDCRDR_LOC: String? = null
    internal var sDCRDR_IN_TIME: String? = null
    internal var sDCRDR_BATTERY_PERCENT: String? = null
    internal var sDCRDR_REMARK: String? = null
    internal var sDCRDR_KM: String? = null
    internal var sDCRDR_SRNO: String? = null
    internal var sDCRDR_FILE: String? = null
    internal var sDCRDR_CALLTYPE: String? = null
    internal var sDR_REF_LAT_LONG: String? = null
    internal var sDCRCHEM_CHEM_ID: String? = null
    internal var sDCRCHEM_POB_QTY: String? = null
    internal var sDCRCHEM_POB_AMT: String? = null
    internal var sDCRCHEM_ITEM_ID_ARR: String? = null
    internal var sDCRCHEM_QTY_ARR: String? = null
    internal var sDCRCHEM_LOC: String? = null
    internal var sDCRCHEM_IN_TIME: String? = null
    internal var sDCRCHEM_SQTY_ARR: String? = null
    internal var sDCRCHEM_ITEM_ID_GIFT_ARR: String? = null
    internal var sDCRCHEM_QTY_GIFT_ARR: String? = null
    internal var sDCRCHEM_BATTERY_PERCENT: String? = null
    internal var sDCRCHEM_KM: String? = null
    internal var sDCRCHEM_SRNO: String? = null
    internal var sDCRCHEM_REMARK: String? = null
    internal var sDCRCHEM_FILE: String? = null
    internal var sCHEM_REF_LAT_LONG: String? = null
    internal var sCHEM_STATUS: String? = null
    internal var sCOMPETITOR_REMARK: String? = null
    internal var sDCRSTK_STK_ID: String? = null
    internal var sDCRSTK_POB_QTY: String? = null
    internal var sDCRSTK_POB_AMT: String? = null
    internal var sDCRSTK_ITEM_ID_ARR: String? = null
    internal var sDCRSTK_QTY_ARR: String? = null
    internal var sDCRSTK_LOC: String? = null
    internal var sDCRSTK_IN_TIME: String? = null
    internal var sDCRSTK_SQTY_ARR: String? = null
    internal var sDCRSTK_ITEM_ID_GIFT_ARR: String? = null
    internal var sDCRSTK_QTY_GIFT_ARR: String? = null
    internal var sDCRSTK_BATTERY_PERCENT: String? = null
    internal var sDCRSTK_KM: String? = null
    internal var sDCRSTK_SRNO: String? = null
    internal var sDCRSTK_REMARK: String? = null
    internal var sDCRSTK_FILE: String? = null
    internal var sSTK_REF_LAT_LONG: String? = null
    internal var sDCRRC_IN_TIME: String? = null
    internal var sDCRRC_LOC: String? = null
    internal var sDCRRC_DR_ID: String? = null
    internal var sDCRRC_KM: String? = null
    internal var sDCRRC_SRNO: String? = null
    internal var sDCRRC_BATTERY_PERCENT: String? = null
    internal var sDCRRC_REMARK: String? = null
    internal var sDCRRC_FILE: String? = null
    internal var sRC_REF_LAT_LONG: String? = null
    internal var sDCR_DR_RX: String? = null
    internal var sDCR_ITM_RX: String? = null
    internal lateinit var sFinalKm: String
    internal var DCS_ID_ARR: String? = null
    internal var LAT_LONG_ARR: String? = null
    internal var DCS_TYPE_ARR: String? = null
    internal var DCS_ADD_ARR: String? = null
    internal var DCS_INDES_ARR: String? = null

    internal var DCRSTK_RATE: String? = null
    internal var DCRDR_RATE: String? = null
    internal var DCRCHEM_RATE: String? = null


    internal var sDAIRY_ID: String? = null
    internal var sSTRDAIRY_CPID: String? = null
    internal var sDCRDAIRY_LOC: String? = null
    internal var sDCRDAIRY_IN_TIME: String? = null
    internal var sDCRDAIRY_BATTERY_PERCENT: String? = null
    internal var sDCRDAIRY_REMARK: String? = null
    internal var sDCRDAIRY_KM: String? = null
    internal var sDCRDAIRY_SRNO: String? = null
    internal var sDAIRY_REF_LAT_LONG: String? = null
    internal var sDCRDAIRYITEM_DAIRY_ID: String? = null
    internal var sDCRDAIRYITEM_ITEM_ID_ARR: String? = null
    internal var sDCRDAIRYITEM_QTY_ARR: String? = null
    internal var sDCRDAIRYITEM_ITEM_ID_GIFT_ARR: String? = null
    internal var sDCRDAIRYITEM_QTY_GIFT_ARR: String? = null
    internal var sDCRDAIRYITEM_POB_QTY: String? = null
    internal var sDAIRY_FILE: String? = null
    internal var sDCRDAIRY_INTERSETEDYN: String? = null

    internal var aDCRDRRX_DOC_DATE: String? = null
    internal var aDCRDRRX_DR_ID: String? = null
    internal var aDCRDRRX_ITEM_ID: String? = null
    internal var aDCRDRRX_QTY: String? = null
    internal var aDCRDRRX_AMOUNT: String? = null

    internal var sDCRRC_CHEM_ID: String? = null
    internal var sDCRRC_LOC_CHEM: String? = null
    internal var sDCRRC_IN_TIME_CHEM: String? = null
    internal var sDCRRC_KM_CHEM: String? = null
    internal var sDCRRC_SRNO_CHEM: String? = null
    internal var sDCRRC_BATTERY_PERCENT_CHEM: String? = null
    internal var sDCRRC_REMARK_CHEM: String? = null
    internal var sDCRRC_FILE_CHEM: String? = null
    internal var sRC_REF_LAT_LONG_CHEM: String? = null

    lateinit var appPrefrences: AppPrefrences

    private val MESSAGE_INTERNET_FINAL_SUBMIT = 0
    private val GPS_TIMMER = 4


    lateinit var attachment: CBOImageView
    internal var cboProgressDialog: CboProgressDialog? = null


    override fun getReferencesById() {
    }

    override fun getActivityTitle(): String {

        return "Out-Time"
    }

    override fun setTile(titleTxt: String) {

        val toolbar = findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            toolbar.setNavigationOnClickListener { view -> onBackPressed() }
        }

        val title = toolbar.findViewById<TextView>(R.id.title)
        title.setText(titleTxt)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        mVmCheckOut = ViewModelProviders.of(this).get(VmCheckOut::class.java)
        mVmCheckOut!!.setView(this, this)

        remark = findViewById<View>(R.id.final_remark) as EditText
        etTime = findViewById<View>(R.id.etTime) as EditText
        attachment = findViewById<View>(R.id.attachment) as CBOImageView
        Late_Submit_remark = findViewById<View>(R.id.Late_Submit_remark) as EditText
        submit = findViewById<View>(R.id.save_final) as Button
        context = this
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance()
        networkUtil = NetworkUtil(this)
        cboFinalTask_new = CBOFinalTask_New(context)

        //get batterry level
        customVariablesAndMethod.getbattrypercentage(context)

        dcr_id = dcr_id + Custom_Variables_And_Method.DCR_ID
        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "MethodCallFinal", "Y")
        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context, "final_km", "0")
        Custom_Variables_And_Method.GLOBAL_LATLON = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON)

        cbohelp = CBO_DB_Helper(context)
        service = Service_Call_From_Multiple_Classes()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        DCR_REMARK_NA = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCR_REMARK_NA")

        etDate.setText(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DATE_NAME"))
        etOffice.setText(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "working_head"))
        //Toast.makeText(this,"javed "+DCR_REMARK_NA,Toast.LENGTH_LONG).show();
        mRemark = remark.getText().toString()
        if (DCR_REMARK_NA.toLowerCase() == "y") {
            remark.setVisibility(View.INVISIBLE)
            mRemark = "Remark Not Required"
        }

        if (!customVariablesAndMethod.IsCallAllowedToday(context)) {
            Late_Submit_remark.setVisibility(View.GONE)
        } else {
            Late_Submit_remark.setVisibility(View.GONE)
        }


        val cal = Calendar.getInstance()
        val time = "" + cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE);
        etTime.setText("" +time)

        customMethod = MyCustomMethod(this)
        appPrefrences = AppPrefrences(this)

//        when (Custom_Variables_And_Method.location_required) {
//            "N" -> final_layout.setVisibility(View.GONE)
//            "Y" -> final_layout.setVisibility(View.VISIBLE)
//            else -> final_layout.setVisibility(View.GONE)
//        }


        attachment.setTitle(MyCustumApplication.getInstance().dcr.attachmentTilte)
        attachment.setMaxAttachment(1)
        if (!MyCustumApplication.getInstance().dcr.getAttachmentMandatory("F")) {
            attachment.setVisibility(View.GONE)
        }


        attachment.setListener(object : CBOImageView.iCBOImageView {
            override fun OnAddClicked() {
                attachment.addAttachment(context as AppCompatActivity, AttachImage.ChooseFrom.frontCamera)
            }

            override fun OnAdded() {
                OnUpdated(attachment.getDataList())
            }

            override fun OnDeleted(file: String) {
                OnUpdated(attachment.getDataList())
            }

            override fun OnUpdated(files: ArrayList<String>) {

            }
        })
        submit.setOnClickListener(View.OnClickListener //@TargetApi(Build.VERSION_CODES.HONEYCOMB)
        {
            // TODO Auto-generated method stub
            if ((!networkUtil.internetConneted(this))!!) {
                customVariablesAndMethod.Connect_to_Internet_Msg(context)

            } else {

                if (Custom_Variables_And_Method.GLOBAL_LATLON.equals("0.0,0.0", ignoreCase = true)) {

                    Custom_Variables_And_Method.GLOBAL_LATLON = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON)
                }
//                setAddressToUI()


                if (DCR_REMARK_NA.toLowerCase() == "y") {
                    mRemark = "Remark Not Required"
                } else {
                    mRemark = remark.getText().toString()
                }

                if (MyCustumApplication.getInstance().dcr.getAttachmentMandatory("F")!! && attachment.getDataList().size == 0) {
                    customVariablesAndMethod.getAlert(context, attachment.getTitle() + "!!!", "Please attach " + attachment.getTitle())
                } else if (mRemark == "") {
                    customVariablesAndMethod.msgBox(context, "Enter Remark....")
                }/*else if(customVariablesAndMethod.IsCallAllowedToday(context) && Late_Submit_remark.equals("") ){
                        customVariablesAndMethod.msgBox(context,"Enter Late Submit Remark....");
                    }*/
                else if (!customVariablesAndMethod.checkIfCallLocationValid(context, false)) {
                    //customVariablesAndMethod.msgBox(context,"Verifing Your Location");
                    LocalBroadcastManager.getInstance(context).registerReceiver(mLocationUpdated,
                            IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE_AVAILABLE))
                } else {

                    GPS_Timmer_Dialog(context, mHandler, "Final Submit in Process...", GPS_TIMMER).show()

                }

            }
        })


    }


    override fun onActivityResult(reqcode: Int, rescode: Int, iob: Intent?) {

        when (reqcode) {

            CBOImageView.REQUEST_CAMERA -> if (rescode == Activity.RESULT_OK) {
                attachment.onActivityResult(reqcode, rescode, iob)
            }
            else -> super.onActivityResult(reqcode, rescode, iob)
        }
    }

    private val mLocationUpdated = object : BroadcastReceiver() {
        override fun onReceive(contex: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(Const.LBM_EVENT_LOCATION_UPDATE)
            LocalBroadcastManager.getInstance(context).unregisterReceiver(this)
            GPS_Timmer_Dialog(context, mHandler, "Final Submit in Process...", GPS_TIMMER).show()

        }
    }

    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_INTERNET_FINAL_SUBMIT -> {
                }
                GPS_TIMMER ->
                    //finalSubmit();
                    finalSubmitNew(false)
                99 -> if (null != msg.data) {
                    customVariablesAndMethod.msgBox(context, msg.data.getString("Error"))
                    //Toast.makeText(getApplicationContext(),msg.getData().getString("Error"),Toast.LENGTH_SHORT).show();
                }
            }//parser6(msg.getData());
            /*Thread thread=new Thread(){
                        @Override
                        public void run() {
                            try{


                                sleep(10);

                            }
                            catch (InterruptedException e){
                                e.printStackTrace();
                            }  finally {

                                finalSubmitNew();
                            }

                        }
                    };
                    thread.start();*/
        }
    }


    private fun finalSubmitNew(skipUpload: Boolean?) {


        if ((!skipUpload!!) && attachment.filesToUpload().size > 0
                && !attachment.getAttachmentStr().equals(MyCustumApplication.getInstance().dcr.attachment, ignoreCase = true)) {
            cboProgressDialog = CboProgressDialog(context, "Please Wait..\nuploading Image")
            cboProgressDialog?.show()
            up_down_ftp().uploadFile(attachment.filesToUpload(), context)
            return
        }
        ///disable submitbtn to be clicked twice
        submit.setEnabled(false)


        val fmcg_Live_Km = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "live_km")

        val routeValue: String
        routeValue = appPrefrences.getRouteValue(context)

        currentBestLocation = customVariablesAndMethod.getObject(context, "currentBestLocation", Location::class.java)

        var locExtra = ""

        if (currentBestLocation != null) {
            locExtra = "Lat_Long " + currentBestLocation?.getLatitude() + "," + currentBestLocation?.getLongitude() + ", Accuracy " + currentBestLocation?.getAccuracy() + ", Time " + currentBestLocation?.getTime() + ", Speed " + currentBestLocation?.getSpeed() + ", Provider " + currentBestLocation?.getProvider()


        }


        if (fmcg_Live_Km.equals("Y", ignoreCase = true) || fmcg_Live_Km.equals("5", ignoreCase = true) || fmcg_Live_Km.equals("Y5", ignoreCase = true)) {
            customMethod.stopAlarm10Sec()
            customMethod.stopAlarm10Minute()
            customMethod.backgroundData()
            dcr_latCommit = customMethod.dataToServer(null)

        }

        CustomTextToSpeech().stopTextToSpeech()

        if (dcr_latCommit.isEmpty() || dcr_latCommit.size == 0) {

            sb_DCRLATCOMMIT_KM = ""
            sb_DCRLATCOMMIT_LOC_LAT = ""
            sb_sDCRLATCOMMIT_IN_TIME = ""
            sDCRLATCOMMIT_ID = ""
            sDCRLATCOMMIT_LOC = ""
        } else {

            sb_DCRLATCOMMIT_KM = dcr_latCommit.get("sb_DCRLATCOMMIT_KM")
            sb_DCRLATCOMMIT_LOC_LAT = dcr_latCommit.get("sb_DCRLATCOMMIT_LOC_LAT")
            sb_sDCRLATCOMMIT_IN_TIME = dcr_latCommit.get("sb_sDCRLATCOMMIT_IN_TIME")
            sDCRLATCOMMIT_ID = dcr_latCommit.get("sDCRLATCOMMIT_ID")
            sDCRLATCOMMIT_LOC = dcr_latCommit.get("sDCRLATCOMMIT_LOC")
        }

        dcr_Commit_rx = cboFinalTask_new!!.drRx_Save(null)
        if (dcr_Commit_rx.isEmpty() || dcr_Commit_rx.size == 0) {

            sDCR_DR_RX = ""
            sDCR_ITM_RX = ""


        } else {

            sDCR_DR_RX = dcr_Commit_rx.get("sDCRRX_DR_ARR")
            sDCR_ITM_RX = dcr_Commit_rx.get("sDCRRX_ITEMID_ARR")


        }


        dcr_Commititem = cboFinalTask_new!!.drItemSave(null)
        if (dcr_Commititem.isEmpty() || dcr_Commititem.size == 0) {

            sDCRITEM_DR_ID = ""
            sDCRITEM_ITEMIDIN = ""
            sDCRITEM_ITEM_ID_ARR = ""
            sDCRITEM_QTY_ARR = ""
            sDCRITEM_ITEM_ID_GIFT_ARR = ""
            sDCRITEM_QTY_GIFT_ARR = ""
            sDCRITEM_POB_QTY = ""
            sDCRITEM_POB_VALUE = ""
            sDCRITEM_VISUAL_ARR = ""
            sDCRITEM_NOC_ARR = ""
            DCRDR_RATE = ""

        } else {
            sDCRITEM_DR_ID = dcr_Commititem.get("sb_sDCRITEM_DR_ID")
            sDCRITEM_ITEMIDIN = dcr_Commititem.get("sb_sDCRITEM_ITEMIDIN")
            sDCRITEM_ITEM_ID_ARR = dcr_Commititem.get("sb_sDCRITEM_ITEM_ID_ARR")
            sDCRITEM_QTY_ARR = dcr_Commititem.get("sb_sDCRITEM_QTY_ARR")
            sDCRITEM_ITEM_ID_GIFT_ARR = dcr_Commititem.get("sb_sDCRITEM_ITEM_ID_GIFT_ARR")
            sDCRITEM_QTY_GIFT_ARR = dcr_Commititem.get("sb_sDCRITEM_QTY_GIFT_ARR")
            sDCRITEM_POB_QTY = dcr_Commititem.get("sb_sDCRITEM_POB_QTY")
            sDCRITEM_POB_VALUE = dcr_Commititem.get("sb_sDCRITEM_POB_VALUE")
            sDCRITEM_VISUAL_ARR = dcr_Commititem.get("sb_sDCRITEM_VISUAL_ARR")
            sDCRITEM_NOC_ARR = dcr_Commititem.get("sb_sDCRITEM_NOC_ARR")
            DCRDR_RATE = dcr_Commititem.get("sb_DCRDR_RATE")

        }
        dcr_CommitDr = cboFinalTask_new!!.dcr_doctorSave(null)
        if (dcr_CommitDr.isEmpty() || dcr_CommitDr.size == 0) {
            sDCRDR_DR_ID = ""
            sDCRDR_WW1 = ""
            sDCRDR_WW2 = ""
            sDCRDR_WW3 = ""
            sDCRDR_LOC = ""
            sDCRDR_IN_TIME = ""
            sDCRDR_BATTERY_PERCENT = ""
            sDCRDR_REMARK = ""
            sDCRDR_KM = ""
            sDCRDR_SRNO = ""
            sDCRDR_FILE = ""
            sDCRDR_CALLTYPE = ""
            sDR_REF_LAT_LONG = ""
        } else {
            sDCRDR_DR_ID = dcr_CommitDr.get("sb_sDCRDR_DR_ID")
            sDCRDR_WW1 = dcr_CommitDr.get("sb_sDCRDR_WW1")
            sDCRDR_WW2 = dcr_CommitDr.get("sb_sDCRDR_WW2")
            sDCRDR_WW3 = dcr_CommitDr.get("sb_sDCRDR_WW3")
            sDCRDR_LOC = dcr_CommitDr.get("sb_sDCRDR_LOC")
            sDCRDR_IN_TIME = dcr_CommitDr.get("sb_sDCRDR_IN_TIME")
            sDCRDR_BATTERY_PERCENT = dcr_CommitDr.get("sb_sDCRDR_BATTERY_PERCENT")
            sDCRDR_REMARK = dcr_CommitDr.get("sb_sDCRDR_Remark")
            sDCRDR_KM = dcr_CommitDr.get("sb_sDCRDR_KM")
            sDCRDR_SRNO = dcr_CommitDr.get("sb_sDCRDR_SRNO")
            sDCRDR_FILE = dcr_CommitDr.get("sb_sDCRDR_FILE")
            sDCRDR_CALLTYPE = dcr_CommitDr.get("sb_sDCRDR_CALLTYPE")
            sDR_REF_LAT_LONG = dcr_CommitDr.get("sb_sDR_REF_LAT_LONG")
        }

        dcr_ChemistCommit = cboFinalTask_new!!.dcr_chemSave(null)
        if (dcr_ChemistCommit.isEmpty() || dcr_ChemistCommit.size == 0) {
            sDCRCHEM_CHEM_ID = ""
            sDCRCHEM_POB_QTY = ""
            sDCRCHEM_POB_AMT = ""
            sDCRCHEM_ITEM_ID_ARR = ""
            sDCRCHEM_QTY_ARR = ""
            sDCRCHEM_LOC = ""
            sDCRCHEM_IN_TIME = ""
            sDCRCHEM_SQTY_ARR = ""
            sDCRCHEM_ITEM_ID_GIFT_ARR = ""
            sDCRCHEM_QTY_GIFT_ARR = ""
            sDCRCHEM_BATTERY_PERCENT = ""
            sDCRCHEM_KM = ""
            sDCRCHEM_SRNO = ""
            sDCRCHEM_REMARK = ""
            sDCRCHEM_FILE = ""
            sCHEM_REF_LAT_LONG = ""
            DCRCHEM_RATE = ""

            sCHEM_STATUS = ""
            sCOMPETITOR_REMARK = ""
        } else {
            sDCRCHEM_CHEM_ID = dcr_ChemistCommit.get("sb_sDCRCHEM_CHEM_ID")
            sDCRCHEM_POB_QTY = dcr_ChemistCommit.get("sb_sDCRCHEM_POB_QTY")
            sDCRCHEM_POB_AMT = dcr_ChemistCommit.get("sb_sDCRCHEM_POB_AMT")
            sDCRCHEM_ITEM_ID_ARR = dcr_ChemistCommit.get("sb_sDCRCHEM_ITEM_ID_ARR")
            sDCRCHEM_QTY_ARR = dcr_ChemistCommit.get("sb_sDCRCHEM_QTY_ARR")
            sDCRCHEM_LOC = dcr_ChemistCommit.get("sb_sDCRCHEM_LOC")
            sDCRCHEM_IN_TIME = dcr_ChemistCommit.get("sb_sDCRCHEM_IN_TIME")
            sDCRCHEM_SQTY_ARR = dcr_ChemistCommit.get("sb_sDCRCHEM_SQTY_ARR")
            sDCRCHEM_ITEM_ID_GIFT_ARR = dcr_ChemistCommit.get("sb_sDCRCHEM_ITEM_ID_GIFT_ARR")
            sDCRCHEM_QTY_GIFT_ARR = dcr_ChemistCommit.get("sb_sDCRCHEM_QTY_GIFT_ARR")
            sDCRCHEM_BATTERY_PERCENT = dcr_ChemistCommit.get("sb_sDCRCHEM_BATTERY_PERCENT")
            sDCRCHEM_KM = dcr_ChemistCommit.get("sb_sDCRCHEM_KM")
            sDCRCHEM_SRNO = dcr_ChemistCommit.get("sb_sDCRCHEM_SRNO")
            sDCRCHEM_REMARK = dcr_ChemistCommit.get("sb_sDCRCHEM_REMARK")
            sDCRCHEM_FILE = dcr_ChemistCommit.get("sb_sDCRCHEM_FILE")
            sCHEM_REF_LAT_LONG = dcr_ChemistCommit.get("sb_sCHEM_REF_LAT_LONG")
            DCRCHEM_RATE = dcr_ChemistCommit.get("sb_DCRCHEM_RATE")


            sCHEM_STATUS = dcr_ChemistCommit.get("sCHEM_STATUS")
            sCOMPETITOR_REMARK = dcr_ChemistCommit.get("sCOMPETITOR_REMARK")
        }


        dcr_StkCommit = cboFinalTask_new!!.dcr_stkSave(null)
        if (dcr_StkCommit.isEmpty() || dcr_StkCommit.size == 0) {

            sDCRSTK_STK_ID = ""

            sDCRSTK_POB_QTY = ""
            sDCRSTK_POB_AMT = ""
            sDCRSTK_ITEM_ID_ARR = ""
            sDCRSTK_QTY_ARR = ""
            sDCRSTK_LOC = ""
            sDCRSTK_IN_TIME = ""
            sDCRSTK_SQTY_ARR = ""
            sDCRSTK_ITEM_ID_GIFT_ARR = ""
            sDCRSTK_QTY_GIFT_ARR = ""
            sDCRSTK_BATTERY_PERCENT = ""
            sDCRSTK_KM = ""
            sDCRSTK_SRNO = ""
            sDCRSTK_REMARK = ""
            sDCRSTK_FILE = ""
            sSTK_REF_LAT_LONG = ""
            DCRSTK_RATE = ""

        } else {
            sDCRSTK_STK_ID = dcr_StkCommit.get("sb_sDCRSTK_STK_ID")
            sDCRSTK_POB_QTY = dcr_StkCommit.get("sb_sDCRSTK_POB_QTY")
            sDCRSTK_POB_AMT = dcr_StkCommit.get("sb_sDCRSTK_POB_AMT")
            sDCRSTK_ITEM_ID_ARR = dcr_StkCommit.get("sb_sDCRSTK_ITEM_ID_ARR")
            sDCRSTK_QTY_ARR = dcr_StkCommit.get("sb_sDCRSTK_QTY_ARR")
            sDCRSTK_LOC = dcr_StkCommit.get("sb_sDCRSTK_LOC")
            sDCRSTK_IN_TIME = dcr_StkCommit.get("sb_sDCRSTK_IN_TIME")
            sDCRSTK_SQTY_ARR = dcr_StkCommit.get("sb_sDCRSTK_SQTY_ARR")
            sDCRSTK_ITEM_ID_GIFT_ARR = dcr_StkCommit.get("sb_sDCRSTK_ITEM_ID_GIFT_ARR")
            sDCRSTK_QTY_GIFT_ARR = dcr_StkCommit.get("sb_sDCRSTK_QTY_GIFT_ARR")
            sDCRSTK_BATTERY_PERCENT = dcr_StkCommit.get("sb_sDCRSTK_BATTERY_PERCENT")
            sDCRSTK_KM = dcr_StkCommit.get("sb_sDCRSTK_KM")
            sDCRSTK_SRNO = dcr_StkCommit.get("sb_sDCRSTK_SRNO")
            sDCRSTK_REMARK = dcr_StkCommit.get("sb_sDCRSTK_REMARK")
            sDCRSTK_FILE = dcr_StkCommit.get("sb_sDCRSTK_FILE")
            sSTK_REF_LAT_LONG = dcr_StkCommit.get("sb_sSTK_REF_LAT_LONG")
            DCRSTK_RATE = dcr_StkCommit.get("sb_DCRSTK_RATE")
        }


        dcr_CommitDr_Reminder = cboFinalTask_new!!.dcr_DrReminder(null)
        if (dcr_CommitDr_Reminder.isEmpty() || dcr_CommitDr_Reminder.size == 0) {

            sDCRRC_IN_TIME = ""
            sDCRRC_LOC = ""
            sDCRRC_DR_ID = ""
            sDCRRC_KM = ""
            sDCRRC_SRNO = ""
            sDCRRC_BATTERY_PERCENT = ""
            sDCRRC_REMARK = ""
            sDCRRC_FILE = ""
            sRC_REF_LAT_LONG = ""
        } else {

            sDCRRC_DR_ID = dcr_CommitDr_Reminder.get("sb_sDCRRC_DR_ID")
            sDCRRC_LOC = dcr_CommitDr_Reminder.get("sb_sDCRRC_LOC")
            sDCRRC_IN_TIME = dcr_CommitDr_Reminder.get("sb_sDCRRC_IN_TIME")
            sDCRRC_KM = dcr_CommitDr_Reminder.get("sb_sDCRRC_KM")
            sDCRRC_SRNO = dcr_CommitDr_Reminder.get("sb_sDCRRC_SRNO")
            sDCRRC_BATTERY_PERCENT = dcr_CommitDr_Reminder.get("sb_sDCRRC_BATTERY_PERCENT")
            sDCRRC_REMARK = dcr_CommitDr_Reminder.get("sb_sDCRRC_REMARK")
            sDCRRC_FILE = dcr_CommitDr_Reminder.get("sb_sDCRRC_FILE")
            sRC_REF_LAT_LONG = dcr_CommitDr_Reminder.get("sb_sRC_REF_LAT_LONG")
        }

        Lat_Long_Reg = cboFinalTask_new!!.get_Lat_Long_Reg("0")
        if (Lat_Long_Reg.isEmpty() || Lat_Long_Reg.size == 0) {

            DCS_ID_ARR = ""
            LAT_LONG_ARR = ""
            DCS_TYPE_ARR = ""
            DCS_ADD_ARR = ""
            DCS_INDES_ARR = ""
        } else {

            DCS_ID_ARR = Lat_Long_Reg.get("DCS_ID_ARR")
            LAT_LONG_ARR = Lat_Long_Reg.get("LAT_LONG_ARR")
            DCS_TYPE_ARR = Lat_Long_Reg.get("DCS_TYPE_ARR")
            DCS_ADD_ARR = Lat_Long_Reg.get("DCS_ADD_ARR")
            DCS_INDES_ARR = Lat_Long_Reg.get("DCS_INDES_ARR")
        }




        dcr_Dairy = cboFinalTask_new!!.get_phdairy_dcr(null)


        if (dcr_Dairy.isEmpty() || dcr_Dairy.size == 0) {

            sDAIRY_ID = ""
            sSTRDAIRY_CPID = ""
            sDCRDAIRY_LOC = ""
            sDCRDAIRY_IN_TIME = ""
            sDCRDAIRY_BATTERY_PERCENT = ""
            sDCRDAIRY_REMARK = ""
            sDCRDAIRY_KM = ""
            sDCRDAIRY_SRNO = ""
            sDCRDAIRYITEM_DAIRY_ID = ""
            sDCRDAIRYITEM_ITEM_ID_ARR = ""
            sDCRDAIRYITEM_QTY_ARR = ""
            sDCRDAIRYITEM_ITEM_ID_GIFT_ARR = ""
            sDCRDAIRYITEM_QTY_GIFT_ARR = ""
            sDCRDAIRYITEM_POB_QTY = ""
            sDAIRY_FILE = ""
            sDCRDAIRY_INTERSETEDYN = ""
            sDAIRY_REF_LAT_LONG = ""
        } else {

            sDAIRY_ID = dcr_Dairy.get("sDAIRY_ID")
            sSTRDAIRY_CPID = dcr_Dairy.get("sSTRDAIRY_CPID")
            sDCRDAIRY_LOC = dcr_Dairy.get("sDCRDAIRY_LOC")
            sDCRDAIRY_IN_TIME = dcr_Dairy.get("sDCRDAIRY_IN_TIME")
            sDCRDAIRY_BATTERY_PERCENT = dcr_Dairy.get("sDCRDAIRY_BATTERY_PERCENT")
            sDCRDAIRY_REMARK = dcr_Dairy.get("sDCRDAIRY_REMARK")
            sDCRDAIRY_KM = dcr_Dairy.get("sDCRDAIRY_KM")
            sDCRDAIRY_SRNO = dcr_Dairy.get("sDCRDAIRY_SRNO")
            sDCRDAIRYITEM_DAIRY_ID = dcr_Dairy.get("sDAIRY_ID")
            sDCRDAIRYITEM_ITEM_ID_ARR = dcr_Dairy.get("sDCRDAIRYITEM_ITEM_ID_ARR")
            sDCRDAIRYITEM_QTY_ARR = dcr_Dairy.get("sDCRDAIRYITEM_QTY_ARR")
            sDCRDAIRYITEM_ITEM_ID_GIFT_ARR = dcr_Dairy.get("sDCRDAIRYITEM_ITEM_ID_GIFT_ARR")
            sDCRDAIRYITEM_QTY_GIFT_ARR = dcr_Dairy.get("sDCRDAIRYITEM_QTY_GIFT_ARR")
            sDCRDAIRYITEM_POB_QTY = dcr_Dairy.get("sDCRDAIRYITEM_POB_QTY")
            sDAIRY_FILE = dcr_Dairy.get("sDAIRY_FILE")
            sDCRDAIRY_INTERSETEDYN = dcr_Dairy.get("sDCRDAIRY_INTERSETEDYN")
            sDAIRY_REF_LAT_LONG = dcr_Dairy.get("sDAIRY_REF_LAT_LONG")
        }



        dcr_RxCalls = cboFinalTask_new!!.get_dcr_RxCalls(null)


        if (dcr_RxCalls.isEmpty() || dcr_RxCalls.size == 0) {

            aDCRDRRX_DOC_DATE = ""
            aDCRDRRX_DR_ID = ""
            aDCRDRRX_ITEM_ID = ""
            aDCRDRRX_QTY = ""
            aDCRDRRX_AMOUNT = ""

        } else {

            aDCRDRRX_DOC_DATE = dcr_RxCalls.get("aDCRDRRX_DOC_DATE")
            aDCRDRRX_DR_ID = dcr_RxCalls.get("aDCRDRRX_DR_ID")
            aDCRDRRX_ITEM_ID = dcr_RxCalls.get("aDCRDRRX_ITEM_ID")
            aDCRDRRX_QTY = dcr_RxCalls.get("aDCRDRRX_QTY")
            aDCRDRRX_AMOUNT = dcr_RxCalls.get("aDCRDRRX_AMOUNT")
        }



        dcr_CommitChem_Reminder = cboFinalTask_new!!.dcr_ChemReminder(null)
        if (dcr_CommitChem_Reminder.isEmpty() || dcr_CommitChem_Reminder.size == 0) {

            sDCRRC_CHEM_ID = ""
            sDCRRC_LOC_CHEM = ""
            sDCRRC_IN_TIME_CHEM = ""
            sDCRRC_KM_CHEM = ""
            sDCRRC_SRNO_CHEM = ""
            sDCRRC_BATTERY_PERCENT_CHEM = ""
            sDCRRC_REMARK_CHEM = ""
            sDCRRC_FILE_CHEM = ""
            sRC_REF_LAT_LONG_CHEM = ""
        } else {

            sDCRRC_CHEM_ID = dcr_CommitChem_Reminder.get("sb_sDCRRC_CHEM_ID")
            sDCRRC_LOC_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_LOC_CHEM")
            sDCRRC_IN_TIME_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_IN_TIME_CHEM")
            sDCRRC_KM_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_KM_CHEM")
            sDCRRC_SRNO_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_SRNO_CHEM")
            sDCRRC_BATTERY_PERCENT_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_BATTERY_PERCENT_CHEM")
            sDCRRC_REMARK_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_REMARK_CHEM")
            sDCRRC_FILE_CHEM = dcr_CommitChem_Reminder.get("sb_sDCRRC_FILE_CHEM")
            sRC_REF_LAT_LONG_CHEM = dcr_CommitChem_Reminder.get("sb_sRC_REF_LAT_LONG_CHEM")
        }


        //customMethod.getDataFromFromAllTables();
        //sFinalKm = mycon.getDataFrom_FMCG_PREFRENCE("final_km");
        // ArrayList<String> array=customMethod.kmWithWayPoint();
        sFinalKm = "0" // array.get(0);
        val sAPI_Pattern = "0"  // array.get(1);

        if (Custom_Variables_And_Method.DCR_ID == "0") {

            Custom_Variables_And_Method.DCR_ID = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCR_ID")
        }

        var ACTUALFARE = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "ACTUALFARE")
        if (ACTUALFARE == "")
            ACTUALFARE = "" + 0


        val request = HashMap<String, String>()

        request["sCompanyFolder"] = MyCustumApplication.getInstance().user.companyCode
        request["iDcrId"] = MyCustumApplication.getInstance().user.dcrId
        request["iNoChemist"] = "1"
        request["iNoStockist"] = "1"
        request["sChemistRemark"] = ""
        request["sStockistREmark"] = ""
        request["iPob"] = "0.0"
        request["iPobQty"] = "0"
        request["iActualFareAmt"] = ACTUALFARE
        request["sDatype"] = "NA"
        request["iDistanceId"] = "99999"
        request["sRemark"] = remark.getText().toString()
        request["sLoc"] = Custom_Variables_And_Method.GLOBAL_LATLON + "@" + locExtra + "!^" + address //mLatLong +"@"+locExtra+ "!^" + mAddress
        request["iOuttime"] = "99"

        request["sRouteYn"] = routeValue

        request["sDCRLATCOMMIT_ID"] = "" + sDCRLATCOMMIT_ID
        request["sDCRLATCOMMIT_IN_TIME"] = "" + sb_sDCRLATCOMMIT_IN_TIME
        request["sDCRLATCOMMIT_LOC_LAT"] = "" + sb_DCRLATCOMMIT_LOC_LAT
        request["sDCRLATCOMMIT_LOC"] = "" + sDCRLATCOMMIT_LOC
        request["sDCRLATCOMMIT_KM"] = "" + sb_DCRLATCOMMIT_KM

        request["sDCRITEM_DR_ID"] = "" + sDCRITEM_DR_ID
        request["sDCRITEM_ITEMIDIN"] = "" + sDCRITEM_ITEMIDIN
        request["sDCRITEM_ITEM_ID_ARR"] = "" + sDCRITEM_ITEM_ID_ARR
        request["sDCRITEM_QTY_ARR"] = "" + sDCRITEM_QTY_ARR
        request["sDCRITEM_ITEM_ID_GIFT_ARR"] = "" + sDCRITEM_ITEM_ID_GIFT_ARR
        request["sDCRITEM_QTY_GIFT_ARR"] = "" + sDCRITEM_QTY_GIFT_ARR

        request["sDCRITEM_POB_QTY"] = "" + sDCRITEM_POB_QTY
        request["sDCRITEM_POB_VALUE"] = "" + sDCRITEM_POB_VALUE
        request["sDCRITEM_VISUAL_ARR"] = "" + sDCRITEM_VISUAL_ARR
        request["sDCRITEM_NOC_ARR"] = "" + sDCRITEM_NOC_ARR

        request["sDCRDR_DR_ID"] = "" + sDCRDR_DR_ID
        request["sDCRDR_WW1"] = "" + sDCRDR_WW1
        request["sDCRDR_WW2"] = "" + sDCRDR_WW2
        request["sDCRDR_WW3"] = "" + sDCRDR_WW3
        request["sDCRDR_LOC"] = "" + sDCRDR_LOC
        request["sDCRDR_IN_TIME"] = "" + sDCRDR_IN_TIME
        request["sDCRDR_BATTERY_PERCENT"] = "" + sDCRDR_BATTERY_PERCENT
        request["sDCRDR_REMARK"] = "" + sDCRDR_REMARK
        request["sDCRDR_KM"] = "" + sDCRDR_KM
        request["sDCRDR_SRNO"] = "" + sDCRDR_SRNO
        request["sDCRDR_FILE"] = "" + sDCRDR_FILE
        request["sDCRDR_CALLTYPE"] = "" + sDCRDR_CALLTYPE

        request["sDCRCHEM_CHEM_ID"] = "" + sDCRCHEM_CHEM_ID
        request["sDCRCHEM_POB_QTY"] = "" + sDCRCHEM_POB_QTY
        request["sDCRCHEM_POB_AMT"] = "" + sDCRCHEM_POB_AMT
        request["sDCRCHEM_ITEM_ID_ARR"] = "" + sDCRCHEM_ITEM_ID_ARR
        request["sDCRCHEM_QTY_ARR"] = "" + sDCRCHEM_QTY_ARR
        request["sDCRCHEM_LOC"] = "" + sDCRCHEM_LOC
        request["sDCRCHEM_IN_TIME"] = "" + sDCRCHEM_IN_TIME
        request["sDCRCHEM_SQTY_ARR"] = "" + sDCRCHEM_SQTY_ARR
        request["sDCRCHEM_ITEM_ID_GIFT_ARR"] = "" + sDCRCHEM_ITEM_ID_GIFT_ARR
        request["sDCRCHEM_QTY_GIFT_ARR"] = "" + sDCRCHEM_QTY_GIFT_ARR
        request["sDCRCHEM_BATTERY_PERCENT"] = "" + sDCRCHEM_BATTERY_PERCENT
        request["sDCRCHEM_KM"] = "" + sDCRCHEM_KM
        request["sDCRCHEM_SRNO"] = "" + sDCRCHEM_SRNO
        request["sDCRCHEM_REMARK"] = "" + sDCRCHEM_REMARK
        request["sDCRCHEM_FILE"] = "" + sDCRCHEM_FILE


        request["sDCRSTK_STK_ID"] = "" + sDCRSTK_STK_ID
        request["sDCRSTK_POB_QTY"] = "" + sDCRSTK_POB_QTY
        request["sDCRSTK_POB_AMT"] = "" + sDCRSTK_POB_AMT
        request["sDCRSTK_ITEM_ID_ARR"] = "" + sDCRSTK_ITEM_ID_ARR
        request["sDCRSTK_QTY_ARR"] = "" + sDCRSTK_QTY_ARR
        request["sDCRSTK_LOC"] = "" + sDCRSTK_LOC
        request["sDCRSTK_IN_TIME"] = "" + sDCRSTK_IN_TIME
        request["sDCRSTK_SQTY_ARR"] = "" + sDCRSTK_SQTY_ARR
        request["sDCRSTK_ITEM_ID_GIFT_ARR"] = "" + sDCRSTK_ITEM_ID_GIFT_ARR
        request["sDCRSTK_QTY_GIFT_ARR"] = "" + sDCRSTK_QTY_GIFT_ARR
        request["sDCRSTK_BATTERY_PERCENT"] = "" + sDCRSTK_BATTERY_PERCENT
        request["sDCRSTK_KM"] = "" + sDCRSTK_KM
        request["sDCRSTK_SRNO"] = "" + sDCRSTK_SRNO
        request["sDCRSTK_REMARK"] = "" + sDCRSTK_REMARK
        request["sDCRSTK_FILE"] = "" + sDCRSTK_FILE

        request["sDCRRC_DR_ID"] = "" + sDCRRC_DR_ID
        request["sDCRRC_LOC"] = "" + sDCRRC_LOC
        request["sDCRRC_IN_TIME"] = "" + sDCRRC_IN_TIME
        request["sDCRRC_KM"] = "" + sDCRRC_KM
        request["sDCRRC_SRNO"] = "" + sDCRRC_SRNO
        request["sDCRRC_BATTERY_PERCENT"] = "" + sDCRRC_BATTERY_PERCENT
        request["sDCRRC_REMARK"] = "" + sDCRRC_REMARK
        request["sDCRRC_FILE"] = "" + sDCRRC_FILE



        request["sDCRRX_DR_ARR"] = "" + sDCR_DR_RX
        request["sDCRRX_ITEMID_ARR"] = "" + sDCR_ITM_RX

        request["iFinalKM"] = sFinalKm
        request["iPaId"] = MyCustumApplication.getInstance().user.id

        request["sGCM_TOKEN"] = MyCustumApplication.getInstance().user.gcmToken
        request["sAPI_PATTERN"] = sAPI_Pattern
        request["sBATTERY_PERCENT"] = "" + MyCustumApplication.getInstance().user.battery

        request["REG_ID_ARR"] = "" + DCS_ID_ARR
        request["REG_LAT_LONG_ARR"] = "" + LAT_LONG_ARR
        request["REG_TYPE_ARR"] = "" + DCS_TYPE_ARR
        request["REG_ADD_ARR"] = "" + DCS_ADD_ARR
        request["REG_INDES_ARR"] = "" + DCS_INDES_ARR

        request["DCS_ID_ARR"] = "" + DCS_ID_ARR
        request["LAT_LONG_ARR"] = "" + LAT_LONG_ARR
        request["DCS_TYPE_ARR"] = "" + DCS_TYPE_ARR
        request["DCS_ADD_ARR"] = "" + DCS_ADD_ARR
        request["DCS_INDES_ARR"] = "" + DCS_INDES_ARR


        request["sDAIRY_ID"] = "" + sDAIRY_ID
        request["sSTRDAIRY_CPID"] = "" + sSTRDAIRY_CPID
        request["sDCRDAIRY_LOC"] = "" + sDCRDAIRY_LOC
        request["sDCRDAIRY_IN_TIME"] = "" + sDCRDAIRY_IN_TIME
        request["sDCRDAIRY_BATTERY_PERCENT"] = "" + sDCRDAIRY_BATTERY_PERCENT


        request["sDCRDAIRY_REMARK"] = "" + sDCRDAIRY_REMARK
        request["sDCRDAIRY_KM"] = "" + sDCRDAIRY_KM

        request["sDCRDAIRY_SRNO"] = "" + sDCRDAIRY_SRNO
        request["sDCRDAIRYITEM_DAIRY_ID"] = "" + sDCRDAIRYITEM_DAIRY_ID
        request["sDCRDAIRYITEM_ITEM_ID_ARR"] = "" + sDCRDAIRYITEM_ITEM_ID_ARR
        request["sDCRDAIRYITEM_QTY_ARR"] = "" + sDCRDAIRYITEM_QTY_ARR
        request["sDCRDAIRYITEM_ITEM_ID_GIFT_ARR"] = "" + sDCRDAIRYITEM_ITEM_ID_GIFT_ARR
        request["sDCRDAIRYITEM_QTY_GIFT_ARR"] = "" + sDCRDAIRYITEM_QTY_GIFT_ARR
        request["sDCRDAIRYITEM_POB_QTY"] = "" + sDCRDAIRYITEM_POB_QTY
        request["sDAIRY_FILE"] = "" + sDAIRY_FILE
        request["sDCRDAIRY_INTERSETEDYN"] = "" + sDCRDAIRY_INTERSETEDYN


        request["SDCR_DATE"] = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "DCR_DATE")
        request["sDR_REF_LAT_LONG"] = "" + sDR_REF_LAT_LONG
        request["sCHEM_REF_LAT_LONG"] = "" + sCHEM_REF_LAT_LONG
        request["sSTK_REF_LAT_LONG"] = "" + sSTK_REF_LAT_LONG
        request["sDAIRY_REF_LAT_LONG"] = "" + sDAIRY_REF_LAT_LONG
        request["sRC_REF_LAT_LONG"] = "" + sRC_REF_LAT_LONG

        request["DCRSTK_RATE"] = "" + DCRSTK_RATE
        request["DCRDR_RATE"] = "" + DCRDR_RATE
        request["DCRCHEM_RATE"] = "" + DCRCHEM_RATE

        request["sCHEM_STATUS"] = "" + sCHEM_STATUS
        request["sCOMPETITOR_REMARK"] = "" + sCOMPETITOR_REMARK

        request["aDCRDRRX_DOC_DATE"] = "" + aDCRDRRX_DOC_DATE
        request["aDCRDRRX_DR_ID"] = "" + aDCRDRRX_DR_ID
        request["aDCRDRRX_ITEM_ID"] = "" + aDCRDRRX_ITEM_ID
        request["aDCRDRRX_QTY"] = "" + aDCRDRRX_QTY
        request["aDCRDRRX_AMOUNT"] = "" + aDCRDRRX_AMOUNT
        request["ISSUPPORTLOGIN"] = if (MyCustumApplication.getInstance().user.loggedInAsSupport) "Y" else "N"



        request["sDCRRC_CHEM_ID"] = "" + sDCRRC_CHEM_ID
        request["sDCRRC_LOC_CHEM"] = "" + sDCRRC_LOC_CHEM
        request["sDCRRC_IN_TIME_CHEM"] = "" + sDCRRC_IN_TIME_CHEM
        request["sDCRRC_KM_CHEM"] = "" + sDCRRC_KM_CHEM
        request["sDCRRC_SRNO_CHEM"] = "" + sDCRRC_SRNO_CHEM
        request["sDCRRC_BATTERY_PERCENT_CHEM"] = "" + sDCRRC_BATTERY_PERCENT_CHEM
        request["sDCRRC_REMARK_CHEM"] = "" + sDCRRC_REMARK_CHEM
        request["sDCRRC_FILE_CHEM"] = "" + sDCRRC_FILE_CHEM
        request["sRC_REF_LAT_LONG_CHEM"] = "" + sRC_REF_LAT_LONG_CHEM


        val tables = ArrayList<Int>()
        tables.add(-1)


        /* commitDialog = new ProgressDialog(FinalSubmitDcr_new.this);
        commitDialog.setMessage("Please Wait..");
        commitDialog.setCanceledOnTouchOutside(false);
        commitDialog.setCancelable(false);
        commitDialog.show();


        new CboServices(this, mHandler).customMethodForAllServices(request, "DCRCommitFinal_New_18", MESSAGE_INTERNET_FINAL_SUBMIT, tables);
*/


        MyAPIService(context)
                .execute(ResponseBuilder("DCRCommitFinal_New_23", request)
                        .setTables(tables)
                        .setDescription("Please Wait..\n" + "Final Submit in process......")
                        .setResponse(object : CBOServices.APIResponse {
                            @Throws(JSONException::class)
                            override fun onComplete(message: Bundle) {

                                submit.setEnabled(true)

                                val table0 = message.getString("Tables0")
                                val jsonArray1 = JSONArray(table0)
                                val c = jsonArray1.getJSONObject(0)
                                if (c.getString("STATUS") == "Y") {
                                    customMethod.stopAlarm10Sec()
                                    customMethod.stopAlarm10Minute()
                                    customMethod.stopDOB_DOA_Remainder()
                                    CustomTextToSpeech().stopTextToSpeech()


                                    MainDB().delete(null)
                                    //MyCustumApplication.getInstance().updateUser();

                                    CBOFinalTasks(this@Checkout).releseResources()
                                    DayPlanTextToSpeech().setTextToSpeech(context, "", "", null)

                                    /*Intent i = new Intent(getApplicationContext(), LoginFake.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(i);*/

                                    if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "ASKUPDATEYN", "N") == "Y") {
                                        GetVersionCode(this@Checkout).execute()
                                    }

                                    stopLoctionService()
                                    customVariablesAndMethod.msgBox(context, "Attendance Saved Sucessfully..")
                                    MyCustumApplication.getInstance().Logout(context as AppCompatActivity)
                                    /*finish();*/
                                } else if (c.getString("MESSAGE").toUpperCase().contains("[REPLAN]")) {

                                    AppAlert.getInstance().Alert(context, "Alert !!!", c.getString("MESSAGE")) { startActivity(Intent(context, GetDCR::class.java)) }

                                } else {

                                    AppAlert.getInstance().getAlert(context, "Alert !!!", c.getString("MESSAGE"), c.getString("URL"))

                                }

                            }

                            @Throws(JSONException::class)
                            override fun onResponse(response: Bundle) {
                                parser6(response)
                            }

                            override fun onError(s: String, s1: String) {
                                AppAlert.getInstance().getAlert(context, s, s1)
                                submit.setEnabled(true)
                            }


                        })
                )
        //End of call to service

    }

    @Throws(JSONException::class)
    private fun parser6(result: Bundle) {

        customVariablesAndMethod.SetLastCallLocation(context)
        val table0 = result.getString("Tables0")
        val jsonArray1 = JSONArray(table0)
        val c = jsonArray1.getJSONObject(0)


        if (c.getString("STATUS") == "Y") {
            val table1 = result.getString("Tables1")
            val jsonArray2 = JSONArray(table1)
            service.parseFMCG(context, jsonArray1, jsonArray2,
                    JSONArray(result.getString("Tables2")))

        }

    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item != null) {
            if (back_allowed == "Y") {
                finish()
            } else {
                customVariablesAndMethod.getAlert(context, "Please Submit", "Please complete your Final Submit")
            }

        }
        return super.onOptionsItemSelected(item)
    }

//    fun setAddressToUI() {
//        if (Custom_Variables_And_Method.global_address != null && Custom_Variables_And_Method.global_address != "") {
//            loc.setText(Custom_Variables_And_Method.global_address)
//        } else {
//            loc.setText(Custom_Variables_And_Method.GLOBAL_LATLON)
//        }
//
//
//    }

    override fun onBackPressed() {

        if (back_allowed == "Y") {
            finish()
        } else {
            customVariablesAndMethod.getAlert(context, "Please Submit", "Please complete your Final Submit")
        }

    }

    override fun started(responseCode: Int?, message: String, description: String) {

    }

    override fun progess(responseCode: Int?, FileSize: Long?, value: Float?, description: String) {

    }

    override fun complete(responseCode: Int?, message: String, description: String) {
        cboProgressDialog!!.dismiss()
        finalSubmitNew(true)
    }

    override fun aborted(responseCode: Int?, message: String, description: String) {
        cboProgressDialog!!.dismiss()
        AppAlert.getInstance().getAlert(context, message, description)
    }

    override fun failed(responseCode: Int?, message: String, description: String) {
        cboProgressDialog!!.dismiss()
        AppAlert.getInstance().getAlert(context, message, description)
    }


}
