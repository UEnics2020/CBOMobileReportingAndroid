package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.cbo.cbomobilereporting.databaseHelper.Call.mDayPlan
import com.cbo.cbomobilereporting.databaseHelper.Location.LocationDB
import com.cbo.cbomobilereporting.emp_tracking.MyCustomMethod
import com.cbo.cbomobilereporting.ui_new.AttachImage
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import com.cbo.myattachment.AttachFile
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.CboProgressDialog
import com.uenics.javed.CBOLibrary.Response
import com.uenics.javed.CBOLibrary.ResponseBuilder
import kotlinx.android.synthetic.main.activity_check_in.*
import locationpkg.Const
import org.json.JSONArray
import org.json.JSONException
import services.CboServices
import services.MyAPIService
import utils_new.*
import utils_new.cboUtils.CBOImageView
import java.util.*
import kotlin.collections.ArrayList

class CheckIn : CustomActivity(), ICheckIn, up_down_ftp.AdapterCallback {
    var officeId = 0
    override fun setOffice(office: MSpinData) {
        officename.setText(office.p_NAME)
        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"working_head",office.p_NAME)
        setLatLng(office.laT_LNG)
        setTime(office.pA_TIME)
        officeId = office.id
    }

    override fun setLatLng(str: String) {
        mLatLong = str
    }

    override fun setTime(TIME: String) {
        time1!!.setText(TIME)
    }

    private var time1: TextView? = null
    private lateinit var aSpinAdapter: SpinCheckInAdapter
    private lateinit var array_sort: ArrayList<MSpinData>
    private lateinit var mVmCheckIn: VmCheckIn
    internal lateinit var save: Button
    internal lateinit var Back: Button
    internal var PA_ID: Int = 0
    val GPS_TIMMER = 4


    internal var real_date: String? = null

    internal lateinit var locationLayout: LinearLayout

    internal lateinit var cbo_helper: CBO_DB_Helper
    internal var paid1 = ""
    internal lateinit var dcrPendingDatesLayout: LinearLayout
    internal lateinit var dcrpendingDates: TextView
    internal lateinit var hader_text: TextView
    internal lateinit var loc: TextView
    internal lateinit var date: TextView
    internal lateinit var time: TextView
    internal lateinit var customMethod: MyCustomMethod
    internal lateinit var toolbar: androidx.appcompat.widget.Toolbar

    var address = ""


    private var currentBestLocation: Location? = null
    internal lateinit var mLatLong: String
    internal lateinit var mAddress: String
    internal var LocExtra = ""
    internal lateinit var attachment: CBOImageView
    internal var cboProgressDialog: CboProgressDialog? = null

    internal lateinit var dayPlan: mDayPlan
    internal lateinit var locationDB: LocationDB


    internal var fmcg_Live_Km = ""
    internal var r1: Runnable = object : Runnable {
        override fun run() {
            synchronized(this) {
                customMethod.startAlarm10Sec()
            }
        }
    }
    internal var r2: Runnable = object : Runnable {
        override fun run() {
            synchronized(this) {
                customMethod.startAlarmIn10Minute()

            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        mVmCheckIn = ViewModelProviders.of(this).get(VmCheckIn::class.java)
        mVmCheckIn!!.setView(this, this)
    }

    override fun onStart() {
        super.onStart()

        mVmCheckIn.getAddressData(this)
    }


    override fun getReferencesById() {

        dcrpendingDates = findViewById<View>(R.id.dcr_pending_dates_route) as TextView
        dcrPendingDatesLayout = findViewById<View>(R.id.pending_dcr_dates_layouts_route) as LinearLayout
        date = findViewById<TextView>(R.id.rootdate)
        time = findViewById<TextView>(R.id.rootTime)
        time1 = findViewById<TextView>(R.id.rootTime1)
        loc = findViewById<View>(R.id.rootloc_dcr_open) as TextView
        save = findViewById<View>(R.id.rootsave) as Button
        locationLayout = findViewById<View>(R.id.RootlocLayoutDopen) as LinearLayout
        attachment = findViewById<CBOImageView>(R.id.attachment)
        Back = findViewById<View>(R.id.rootback) as Button

    }

    override fun getActivityTitle(): String {

        return "In Time"
    }

    override fun setTile(titleTxt: String) {

        toolbar = findViewById<View>(R.id.toolbar_hadder) as androidx.appcompat.widget.Toolbar
        hader_text = findViewById<View>(R.id.hadder_text_1) as TextView
        hader_text.setText(titleTxt)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            toolbar.setNavigationOnClickListener { view -> onBackPressed() }
        }

        locationDB = LocationDB()
        dayPlan = mDayPlan("Day Plan")
        Custom_Variables_And_Method.GLOBAL_LATLON = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON)
        cbo_helper = CBO_DB_Helper(this)
        customMethod = MyCustomMethod(this)
        PA_ID = Custom_Variables_And_Method.PA_ID
        paid1 = "" + PA_ID

        currentBestLocation = customVariablesAndMethod.getObject(this, "currentBestLocation", Location::class.java)

        mLatLong = Custom_Variables_And_Method.GLOBAL_LATLON
        mAddress = Custom_Variables_And_Method.global_address

        if (currentBestLocation != null) {
            LocExtra = "Lat_Long " + currentBestLocation!!.getLatitude() + "," + currentBestLocation!!.getLongitude() + ", Accuracy " + currentBestLocation!!.getAccuracy() + ", Time " + currentBestLocation!!.getTime() + ", Speed " + currentBestLocation!!.getSpeed() + ", Provider " + currentBestLocation!!.getProvider()
        }

        Custom_Variables_And_Method.ROOT_NEEDED = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "root_needed")
        fmcg_Live_Km = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "live_km")

        Custom_Variables_And_Method.DCR_DATE = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "DATE_NAME")
        date.setText(Custom_Variables_And_Method.DCR_DATE)
        time.setText("" + currentBestLocation!!.getTime())


        Custom_Variables_And_Method.DCR_DATE_TO_SUBMIT = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "DCR_DATE")
        real_date = Custom_Variables_And_Method.DCR_DATE_TO_SUBMIT


        if (Custom_Variables_And_Method.DcrPending_datesList.size == 1 || Custom_Variables_And_Method.DcrPending_datesList.size == 0) {
            dcrPendingDatesLayout.setVisibility(View.GONE)
        } else {
            dcrPendingDatesLayout.setVisibility(View.VISIBLE)
            dcrpendingDates.setSelected(true)
            dcrpendingDates.setEllipsize(TextUtils.TruncateAt.MARQUEE)
            dcrpendingDates.setText(Custom_Variables_And_Method.DcrPending_datesList.toString())

        }

        attachment.setTitle(MyCustumApplication.getInstance().dcr.attachmentTilte)
        attachment.setMaxAttachment(1)
        if (!MyCustumApplication.getInstance().dcr.getAttachmentMandatory("D")) {
            attachment.setVisibility(View.GONE)
        } else {
            attachment.setVisibility(View.GONE)
        }


        attachment.setListener(object : CBOImageView.iCBOImageView {
            override fun OnAddClicked() {
                attachment.addAttachment(this@CheckIn, AttachImage.ChooseFrom.frontCamera)
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


        if (Custom_Variables_And_Method.location_required == "Y") {
            locationLayout.setVisibility(View.VISIBLE)
        } else {
            locationLayout.setVisibility(View.GONE)
        }

        if (intent.getStringExtra("plan_type") == "p") {

            MyCustumApplication.getInstance().dcr.setDivertRoute(false)
            MyCustumApplication.getInstance().dcr.attachment = ""

            if (!customVariablesAndMethod.IsBackDate(this)) {
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "IsBackDate", "1") //not back date entry
            } else {
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "IsBackDate", "0")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "BackDateReason", "")
            }

        } else {
            hader_text.setText("IN TIME")
            dayPlan = mDayPlan("IN TIME")

            attachment.setAttachment(MyCustumApplication.getInstance().dcr.attachment)
            save.visibility = View.GONE

        }


    }


    fun setAddressToUI() {

        if (Custom_Variables_And_Method.global_address != null || Custom_Variables_And_Method.global_address != "") {
            loc.text = Custom_Variables_And_Method.global_address
        } else if (loc.text.toString() == "") {
            loc.text = Custom_Variables_And_Method.GLOBAL_LATLON

        } else {
            loc.text = Custom_Variables_And_Method.GLOBAL_LATLON
        }


    }


    //==========================================================  onCreate finish  ================================================

    private val mLocationUpdated = object : BroadcastReceiver() {
        override fun onReceive(contex: Context, intent: Intent) {
            val location = intent.getParcelableExtra<Location>(Const.LBM_EVENT_LOCATION_UPDATE)
            LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver(this)
            startSubmitDCR()

        }
    }

    override fun onActivityResult(reqcode: Int, rescode: Int, iob: Intent?) {

        when (reqcode) {

            CBOImageView.REQUEST_CAMERA -> if (rescode == Activity.RESULT_OK) {
                attachment.onActivityResult(reqcode, rescode, iob)
            }
            else -> super.onActivityResult(reqcode, rescode, iob)
        }
    }


    private fun startSubmitDCR() {

        if (intent.getStringExtra("plan_type") == "p") {

            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "srno", "0")
            val FIRST_CALL_LOCK_TIME = java.lang.Float.valueOf(customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "FIRST_CALL_LOCK_TIME", "0"))
            if (FIRST_CALL_LOCK_TIME.toInt() == 0) {
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "CALL_UNLOCK_STATUS", "[CALL_UNLOCK]")
            } else {
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "CALL_UNLOCK_STATUS", "")
            }
        }

        GPS_Timmer_Dialog(this, mHandler, "Day Plan in Process...", GPS_TIMMER).show()
    }


    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            val b1: Bundle
            when (msg.what) {

                GPS_TIMMER -> submitDCR(false)


                99 -> {

                    if (null != msg.data) {
                        customVariablesAndMethod.msgBox(this@CheckIn, msg.data.getString("Error"))
                    }
                }
            }
        }
    }


    fun submitDCR(skipUpload: Boolean?) {

        if (!(skipUpload)!! && attachment.filesToUpload().size > 0
                && !attachment.attachmentStr.equals(MyCustumApplication.getInstance().dcr.attachment, ignoreCase = true)) {
            cboProgressDialog = CboProgressDialog(this, "Please Wait..\nuploading Image")
            cboProgressDialog!!.show()
            up_down_ftp().uploadFile(MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("ATTENDANCE_UPLOAD_DIRECTORY", ""), attachment.filesToUpload(), this)
            return
        } else {
            submitforMeeting()
        }

    }


    override fun started(responseCode: Int?, message: String?, description: String?) {
    }

    override fun progess(responseCode: Int?, FileSize: Long?, value: Float?, description: String?) {
    }

    override fun complete(responseCode: Int?, message: String?, description: String?) {
        cboProgressDialog!!.dismiss()
        submitDCR(true)
    }

    override fun aborted(responseCode: Int?, message: String?, description: String?) {
        cboProgressDialog!!.dismiss()
        AppAlert.getInstance().getAlert(this, message, description)
    }

    override fun failed(responseCode: Int?, message: String?, description: String?) {
        cboProgressDialog!!.dismiss()
        AppAlert.getInstance().getAlert(this, message, description)
    }


    override fun setOnClickListner() {

        Back.setOnClickListener(View.OnClickListener { finish() })

        save.setOnClickListener(View.OnClickListener {
            setAddressToUI()
            if (loc.getText().toString() == "") {
                loc.setText("UnKnown Location")
            }
            address = loc.text.toString()

            if (address == "") {
                val builder1 = AlertDialog.Builder(this)
                builder1.setTitle("Network Error")
                builder1.setMessage(" Slow Network Connection" + "\n" + "Please Re-Start Your Device And Try Again .....")
                builder1.setPositiveButton("Ok") { dialog, which ->
                    finish()
                }
                builder1.show()
            } else if (!customVariablesAndMethod.checkIfCallLocationValid(this, false)) {
                //customVariablesAndMethod.msgBox(context,"Verifing Your Location");
                LocalBroadcastManager.getInstance(this).registerReceiver(mLocationUpdated,
                        IntentFilter(Const.INTENT_FILTER_LOCATION_UPDATE_AVAILABLE))
            } else {
                startSubmitDCR()
            }
        })


        ivPopupIcon.setOnClickListener {
            if (mVmCheckIn.getData().size != 0) {
                onClickDrName()
            } else {
                AppAlert.getInstance().SystemAlert("Alert !!", "Location Data Not Found...", View.OnClickListener {
                    finish()
                })
            }

        }


    }

    private var myalertDialog: AlertDialog? = null
    internal var IsRefreshedClicked = true
    private var textlength: Int = 0

    private fun onClickDrName() {
        val builder = AlertDialog.Builder(this).create()
        val inflater = getLayoutInflater()
        val view = inflater.inflate(R.layout.office_selection_layout, null);
        val editText = view.findViewById<View>(R.id.editText) as EditText
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        builder.setView(view);
        recyclerView.layoutManager = LinearLayoutManager(this)
        aSpinAdapter = SpinCheckInAdapter(this, mVmCheckIn.getData())
        recyclerView.adapter = aSpinAdapter


        aSpinAdapter.setOnClickListner { view, mSpinData, position ->


            setOffice(mSpinData)

            builder.dismiss()

        }



        editText.addTextChangedListener(object : TextWatcher {


            override fun afterTextChanged(s: Editable) {

            }

            override fun beforeTextChanged(s: CharSequence,
                                           start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //editText.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                textlength = editText.text.length
                array_sort.clear()

            }
        })


        false
        builder.show();
    }

    fun submitforMeeting() {

        val request = HashMap<String, String>()
        request["sCompanyFolder"] = cbo_helper.companyCode
        request["iPA_ID"] = "" + Custom_Variables_And_Method.PA_ID
        request["sDCR_DATE"] = "" + real_date
        request["sLOC1"] = mLatLong
        request["iWORK_WITH1"] = "" + officeId
        request["FILE_NAME"] = attachment.attachmentNameStr



        val tables = java.util.ArrayList<Int>()
        tables.add(0)

        MyAPIService(this).execute(ResponseBuilder("INTIME_COMMIT_MOBILE", request)
                .setTables(tables)
                /*.setShowProgess(orders.size() == 0 )*/
                .setResponse(object : CBOServices.APIResponse {
                    @Throws(Exception::class)
                    override fun onComplete(bundle: Bundle) {
                        parser_submit_for_working(bundle)
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"working_code","NRX")
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(context,"CHEMIST_NOT_VISITED","NRX")
                    }

                    @Throws(Exception::class)
                    override fun onResponse(bundle: Bundle?) {

                    }

                    override fun onError(s: String, s1: String) {


                    }
                }))
    }

    fun parser_submit_for_working(result: Bundle?) {

        if (result != null) {

            try {
                val table0 = result.getString("Tables0")
                val jsonArray1 = JSONArray(table0)
                for (i in 0 until jsonArray1.length()) {
                    val c = jsonArray1.getJSONObject(i)
                    Custom_Variables_And_Method.DCR_ID = c.getString("DCRID")

                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "FCMHITCALLYN", c.getString("FCMHITCALLYN"))
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "FARMERREGISTERYN", c.getString("FARMERREGISTERYN"))
                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DIVERTLOCKYN", c.getString("DIVERTLOCKYN"))


                    if (c.getString("FCMHITCALLYN") != "" && c.getString("FCMHITCALLYN") != "N") {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "MOBILEDATAYN", "Y")
                    }

                    customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "APPROVAL_MSG", "")
                    if (Custom_Variables_And_Method.DCR_ID == "0" && c.getString("DCRID") != null) {
                        Alert("Alert !!!", c.getString("MSG"))
                    } else if (c.getString("DIVERTLOCKYN").toUpperCase() == "Y") {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "APPROVAL_MSG", c.getString("MSG"))
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DCR_ID", c.getString("DCRID"))
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DcrPlanTime_server", c.getString("IN_TIME"))
                        Alert("Alert !!!", c.getString("MSG"))
                    } else if (c.getString("FARMERREGISTERYN") == "Y") {
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "APPROVAL_MSG", c.getString("MSG"))
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DCR_ID", c.getString("DCRID"))
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DcrPlanTime_server", c.getString("IN_TIME"))
                        Alert("Alert !!!", "Today You have an activity for " + cbo_helper.getMenu("DCR", "D_FAR")["D_FAR"]!!)
                    } else {

                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DCR_ID", c.getString("DCRID"))
                        customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DcrPlanTime_server", c.getString("IN_TIME"))
                        DownloadAll()
                    }
                }


            } catch (e: JSONException) {
                Log.d("MYAPP", "objects are: $e")
                CboServices.getAlert(this, "Missing field error", resources.getString(R.string.service_unavilable) + e.toString())
                e.printStackTrace()
            }

        }

    }

    private fun Alert(title: String, msg: String) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val dialogLayout = inflater.inflate(R.layout.update_available_alert_view, null)
        val Alert_title = dialogLayout.findViewById<View>(R.id.title) as TextView
        val Alert_message = dialogLayout.findViewById<View>(R.id.message) as TextView
        val Alert_Positive = dialogLayout.findViewById<View>(R.id.positive) as Button
        val Alert_Nagative = dialogLayout.findViewById<View>(R.id.nagative) as Button
        Alert_Nagative.visibility = View.GONE
        Alert_Positive.text = "OK"
        Alert_title.text = title
        Alert_message.text = msg

        val builder1 = AlertDialog.Builder(this)

        val dialog = builder1.create()

        dialog.setView(dialogLayout)
        Alert_Positive.setOnClickListener {
            dialog.dismiss()
            DownloadAll()
        }

        dialog.setCancelable(false)
        dialog.show()
    }

    private fun DownloadAll() {
        if (Custom_Variables_And_Method.DCR_ID != "0") {

            cbo_helper.deletedcrFromSqlite()
            cbo_helper.deleteUtils()
            cbo_helper.deleteDCRDetails()

            CustomTextToSpeech().setTextToSpeech("")
            DayPlanTextToSpeech().stopTextToSpeech()

            val `val` = cbo_helper.insertUtils(Custom_Variables_And_Method.pub_area)
            val val2 = cbo_helper.insertDcrDetails(Custom_Variables_And_Method.DCR_ID, Custom_Variables_And_Method.pub_area)


            if (customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "dcr_date_real") == "") {
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "OveAllKm", "0.0")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DayPlanLatLong", customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON))
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "DcrPlantimestamp", customVariablesAndMethod._currentTimeStamp)
            }



            customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this, "dcr_date_real", real_date)
            cbo_helper.putDcrId(Custom_Variables_And_Method.DCR_ID)
            MyCustumApplication.getInstance().user.dcrId = Custom_Variables_And_Method.DCR_ID

            MyCustumApplication.getInstance().dcr.attachment = attachment.attachmentStr

            dayPlan.time = customVariablesAndMethod.currentTime(this)
            dayPlan.latLong = customVariablesAndMethod.get_best_latlong(this)
            locationDB.insert(dayPlan)

            Service_Call_From_Multiple_Classes().DownloadAll(this, object : Response {
                override fun onSuccess(bundle: Bundle) {
                    customVariablesAndMethod.SetLastCallLocation(this@CheckIn)
                    finish()
                }

                override fun onError(s: String, s1: String) {
                    AppAlert.getInstance().getAlert(this@CheckIn, s, s1)
                }
            })


            if (fmcg_Live_Km.equals("Y", ignoreCase = true) || fmcg_Live_Km.equals("5", ignoreCase = true) || fmcg_Live_Km.equals("Y5", ignoreCase = true)) {
                val lat: String
                val lon: String
                val time: String
                val km: String
                customVariablesAndMethod.deleteFmcg_ByKey(this@CheckIn, "myKm1")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this@CheckIn, "Tracking", "Y")
                lat = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this@CheckIn, "shareLat")
                lon = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this@CheckIn, "shareLon")
                time = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(this@CheckIn, "shareMyTime")
                km = "0.0"
                customMethod.insertDataInOnces_Minute(lat, lon, km, time)

                Thread(r1).start()
                Thread(r2).start()
            }


            if (intent.getStringExtra("plan_type") == "p") {
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("CALL_TYPE", "")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this@CheckIn, "Final_submit", "N")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this@CheckIn, "ACTUALFAREYN", "")
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this@CheckIn, "ACTUALFARE", "")
                cbo_helper.deleteAllRecord10()
                cbo_helper.delete_DCR_Item(null, null, null, null)
                customVariablesAndMethod.setDataInTo_FMCG_PREFRENCE(this@CheckIn, "Dcr_Planed_Date", customVariablesAndMethod.currentDate())
            }
        }
    }

}
