package com.cbo.cbomobilereporting.ui_new.transaction_activities.retaileradd

import android.annotation.TargetApi
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import kotlinx.android.synthetic.main.activity_chemist_registration.*
import utils.adapterutils.Dcr_Workwith_Model
import utils.font_package.Edit_text_Regular
import utils_new.Custom_Variables_And_Method
import java.util.*

class RetailerAdd : CustomActivity(), IRetailerAdd {

    private var routID: String = ""
    private var iPaID: String = ""
    private lateinit var list1: ArrayList<MRetailerPopup>
    private lateinit var cbohelp: CBO_DB_Helper
    private lateinit var aSpinCat: ASpinCat
    private lateinit var aWorkWith: AWorkWith
    private lateinit var db_helper: CBO_DB_Helper
    private var mVmRetailerAdd: VmRetailerAdd? = null
    internal val Dialog_id = 0
    internal var year_x: Int = 0
    internal var month_x: Int = 0
    internal var day_x: Int = 0
    var string_date_SERVER: String = ""
    var string_date_SERVERM: String = ""
    var showDate: String = ""
    internal var calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chemist_registration)
        mVmRetailerAdd = ViewModelProviders.of(this).get(VmRetailerAdd::class.java)
        mVmRetailerAdd!!.setView(this, this)

//        customVariablesAndMethod.msgBox(context, "" + db_helper.collect_all_ChemistReg_data())

    }

    override fun getReferencesById() {
        year_x = calendar.get(Calendar.YEAR)
        month_x = calendar.get(Calendar.MONTH)
        day_x = calendar.get(Calendar.DAY_OF_MONTH)
        db_helper = CBO_DB_Helper(context)
        list1 = ArrayList<MRetailerPopup>()

        if (MyCustumApplication.getInstance().user.desginationID.equals("1")) {
            MrSpinner.setText(MyCustumApplication.getInstance().user.name)
            iPaID = MyCustumApplication.getInstance().user.id
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("IDD", iPaID)

        } else {

            var id = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_MR_ID", "")
            var name = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_MR_NAME", "")

            var idStr = id.removePrefix("[").removeSuffix("]").split(",")
            var nameStr = name.removePrefix("[").removeSuffix("]").split(",")

            for (i in 0..idStr.size - 1) {
                val retailer = MRetailerPopup()
                retailer.id = idStr.get(i)
                retailer.name = nameStr.get(i)
                list1.add(retailer)

            }
        }

    }

    override fun setOnClickListner() {

        btnDateOfBirth.setOnClickListener(View.OnClickListener {
            //  sending_data_to_server();
            showDate = "DATEOFBIRTH"
            showDialog(Dialog_id)
        })

        btnMarriageAniversary.setOnClickListener {
            showDate = "DATEOFBIRTHM"
            showDialog(Dialog_id)
        }

        btn_save.setOnClickListener {

            //            if (isValidation()) {
//
//            }

            var ch = MRetailerAdd();
            ch.custmerName = etChemistName.text.toString().trim()
            ch.mobieNo = etMobile.text.toString().trim()
            ch.mr_name = iPaID
            ch.mr_ID = iPaID
            ch.concernPerson = etConcernPerson.text.toString().trim()
            ch.address = etAddressLine1.text.toString().trim()
            ch.address2 = etAddressLine2.text.toString().trim()
            ch.address3 = etAddressLine3.text.toString().trim()
            ch.pincode = etPinCode.text.toString().trim()
            ch.email = etEmail.text.toString().trim()
            ch.visitFrequency = etVisitFrequency.text.toString().trim()
            ch.category = categorySpinner.text.toString().trim()
            ch.routeName = RoutSpinner.text.toString().trim()
            ch.routeID = routID
            ch.dateOfBirth = string_date_SERVER
            ch.marriageAnniversary = string_date_SERVERM

            if (isValidation(ch)) {
                db_helper.SaveRetailer(ch)
                customVariablesAndMethod.msgBox(context, "Retailer Added Successfully ....")
                finish()
            }


        }

        ivCategorySpinner.setOnClickListener { onClickName() }
        ivMrSpinner.setOnClickListener {
            if (!MyCustumApplication.getInstance().user.desginationID.equals("1")) {
                onClickNameW()
            }
        }
        ivRoutSpinner.setOnClickListener { onClickNameR() }

        btn_close.setOnClickListener { finish() }
    }


    override fun setTile(strText: String) {

        val toolbar = findViewById<View>(R.id.toolbar_hadder) as Toolbar
        val textView = findViewById<View>(R.id.hadder_text_1) as TextView
        setSupportActionBar(toolbar)

        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_hadder_2016)
        }
        textView.setText(strText)
    }

    override fun getActivityTitle(): String {
        return "Add Retailer"
    }

    override fun onCreateDialog(id: Int): Dialog? {
        return if (id == Dialog_id) {
            DatePickerDialog(this, daypickerlistner, year_x, month_x, day_x)

        } else null
    }

    private val daypickerlistner = DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
        year_x = year
        month_x = monthOfYear
        day_x = dayOfMonth
        val month = monthOfYear + 1
        if (showDate.equals("DATEOFBIRTH")) {
            etDateOfBirth.setText("" + day_x + "/" + month + "/" + year_x)
            string_date_SERVER = "$month-$day_x-$year_x"
        } else {
            etMarriageAnnversary.setText("" + day_x + "/" + month + "/" + year_x)
            string_date_SERVERM = "$month-$day_x-$year_x"
        }

    }

    @TargetApi(Build.VERSION_CODES.O)
    private fun isValidation(ch: MRetailerAdd): Boolean {

        if (ch.mr_name!!.length == 0) {
            Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select Employee")
            return false
        }
        if (ch.custmerName!!.length == 0) {

            setFocous(etChemistName)
            etChemistName.setError("Enter Customer Name")
            return false
        }
        if (ch.routeID!!.length == 0) {
            Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select Route")
            return false
        }
        if (ch.concernPerson!!.length == 0) {
            setFocous(etConcernPerson)
            etConcernPerson.setError("Enter Concern Person")

            return false
        }

        if (ch.address!!.length == 0) {
            setFocous(etAddressLine1)
            etAddressLine1.setError("Enter Address")
            return false
        }

        if (ch.pincode!!.length == 0) {
            setFocous(etPinCode)
            etPinCode.setError("Enter Pincode")
            return false
        } else if (ch.pincode!!.length != 6) {
            etPinCode.setError("Enter Valid PinCode")
            return false
        }
        if (ch.mobieNo!!.length == 0) {
            setFocous(etMobile)
            etMobile.setError("Enter Mobile")
            return false
        } else if (ch.mobieNo!!.length != 10) {
            setFocous(etMobile)
            etMobile.setError("Enter Valid Mobile")
            return false
        }

        return true
    }

    private fun setFocous(etEditTextsV: Edit_text_Regular) {

        etEditTextsV.setFocusable(true);
        etEditTextsV.setFocusableInTouchMode(true);
        etEditTextsV.requestFocus();
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        imm.showSoftInput(etEditTextsV, InputMethodManager.SHOW_IMPLICIT);
    }


    private fun onClickName() {
        val builder = AlertDialog.Builder(this).create()
        val inflater = getLayoutInflater()
        val view = inflater.inflate(R.layout.office_selection_layout, null);
        val editText = view.findViewById<View>(R.id.editText) as EditText
        val tvError = view.findViewById<View>(R.id.tvError) as TextView
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        builder.setView(view);

        editText.setHint("")
        recyclerView.layoutManager = LinearLayoutManager(this)
        aSpinCat = ASpinCat(this, mVmRetailerAdd!!.getData())
        recyclerView.adapter = aSpinCat


        aSpinCat.setOnClickListner { view, mSpinCat, position ->
            //            setOffice(mSpinCat)
            categorySpinner.setText(mSpinCat.name)
//            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("IDD", mSpinCat.id)
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
//                textlength = editText.text.length
//                array_sort.clear()

            }
        })
        if (mVmRetailerAdd!!.getData()!!.size == 0) {
            tvError.visibility = View.VISIBLE
            tvError.setText("Category Data Not Found")
        } else {
            tvError.visibility = View.GONE
        }

        false
        builder.show();
    }


    private fun onClickNameW() {

        val builder = AlertDialog.Builder(this).create()
        val inflater = getLayoutInflater()
        val view = inflater.inflate(R.layout.office_selection_layout, null);
        val editText = view.findViewById<View>(R.id.editText) as EditText
        val tvError = view.findViewById<View>(R.id.tvError) as TextView
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        builder.setView(view);

        editText.setHint("")
        if (list1!!.size == 0) {
            tvError.visibility = View.VISIBLE
            tvError.setText("Employee Not Found")
        } else {
            tvError.visibility = View.GONE
        }
        recyclerView.layoutManager = LinearLayoutManager(this)
        aWorkWith = AWorkWith(this, list1)
        recyclerView.adapter = aWorkWith


        aWorkWith.setOnClickListner { view, mSpinCat, position ->
            //            setOffice(mSpinCat)
            MrSpinner.setText(mSpinCat.name)
            iPaID = mSpinCat.id
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("IDD", iPaID)
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
//                textlength = editText.text.length
//                array_sort.clear()

            }
        })


        false
        builder.show();
    }

    private fun onClickNameR() {
        val builder = AlertDialog.Builder(this).create()
        val inflater = getLayoutInflater()
        val view = inflater.inflate(R.layout.office_selection_layout, null);
        val editText = view.findViewById<View>(R.id.editText) as EditText
        val tvError = view.findViewById<View>(R.id.tvError) as TextView
        val recyclerView = view.findViewById<View>(R.id.recyclerView) as RecyclerView
        builder.setView(view);

        editText.setHint("")
        recyclerView.layoutManager = LinearLayoutManager(this)
        aSpinCat = ASpinCat(this, mVmRetailerAdd!!.getDataR())
        recyclerView.adapter = aSpinCat


        aSpinCat.setOnClickListner { view, mSpinCat, position ->
            RoutSpinner.setText(mSpinCat.name)
            routID = mSpinCat.id
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
//                textlength = editText.text.length
//                array_sort.clear()

            }
        })


        if (mVmRetailerAdd!!.getData()!!.size == 0) {
            tvError.visibility = View.VISIBLE
            tvError.setText("Route Data Not Found")
        } else {
            tvError.visibility = View.GONE
        }
        false
        builder.show();
    }


    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {

            finish()
        }
        return super.onOptionsItemSelected(item)
    }

}
