package com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense.newotherExp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.Image
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.ui_new.AttachImage
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense.*
import com.uenics.javed.CBOLibrary.CboProgressDialog
import kotlinx.android.synthetic.main.activity_new_other_expence.*
import utils.font_package.Edit_text_Regular
import utils_new.AppAlert
import utils_new.Custom_Variables_And_Method
import utils_new.cboUtils.CBOImageView
import utils_new.up_down_ftp
import java.io.File
import java.lang.Exception
import java.util.*

class NewOtherExpence : CustomActivity(), IOtherExpense, up_down_ftp.AdapterCallback {
    override fun loadExpenseHead(expHeads: ArrayList<mExpHead>?) {

    }


    private var ImageStr: String = ""
    private var expTypeStrS: String = "--Select--"
    lateinit var exphead: Spinner
    lateinit var spToArea: Spinner
    lateinit var spFromArea: Spinner
    lateinit var exhAmt: EditText
    lateinit var rem_final: EditText
    lateinit var Add: Button
    lateinit var Cancel: Button
    lateinit var textRemark: TextView
    lateinit var head: TextView

    var fromAreaStr: String = "--Select--"
    var toAreaStr: String = "--Select--"
    var expTypeStr: String = "--Select--"

    lateinit var ex_head_root_txt: TextView
    lateinit var title: TextView
    lateinit var amtLayout: LinearLayout
    lateinit var adapter: aNewPopup
    var keyPressed: Boolean = true

    var fromArea = ArrayList<MPopupData>()
    var toArea = ArrayList<MPopupData>()
    var expType = ArrayList<MPopupData>()

    private val REQUEST_CAMERA = 201

    lateinit var viewModel: VmNewOtherExp
    lateinit var cboProgressDialog: CboProgressDialog
    lateinit var cboImageView: CBOImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_other_expence)

        viewModel = ViewModelProviders.of(this).get(VmNewOtherExp::class.java)
        viewModel.setView(context, this)
        viewModel.getPopupData(context)
        viewModel.setExpense(intent.getSerializableExtra("expense") as mExpense)
        viewModel.setExpense_type(intent.getSerializableExtra("eExpense") as eExpense)
        viewModel.setOthExpense(intent.getSerializableExtra("othExpense") as mOthExpense)

    }

    override fun getCompanyCode(): String {
        return MyCustumApplication.getInstance().user.companyCode
    }

    override fun getDCRId(): String {
        return MyCustumApplication.getInstance().user.dcrId
    }


    override fun getUserId(): String {
        return MyCustumApplication.getInstance().user.id
    }

    override fun getReferencesById() {
        title = findViewById(R.id.ExpTitle)
        exphead = findViewById(R.id.exp_head_root)
        spToArea = findViewById(R.id.spToArea)
        spFromArea = findViewById(R.id.sp_from_area)
        exhAmt = findViewById(R.id.ex_head_root)
        head = findViewById(R.id.head)
        textRemark = findViewById(R.id.text_remark)
        ex_head_root_txt = findViewById(R.id.ex_head_root_txt)
        rem_final = findViewById(R.id.exp_remark_root)
        amtLayout = findViewById(R.id.amtLayout)
        cboImageView = findViewById(R.id.attachment)
        //final String[] ext = {path};

        etPerKm.setText(MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("PER_KM", "0"))

        Add = findViewById(R.id.save)
        Cancel = findViewById(R.id.cancel)

        exphead.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        position: Int, arg3: Long) {

                val expHead = expType[position]
                setExpType(context, expHead)

            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }
        spToArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        position: Int, arg3: Long) {

                val area = fromArea[position]
                setToArea(context, area)

            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }

        spFromArea.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onItemSelected(arg0: AdapterView<*>, arg1: View,
                                        position: Int, arg3: Long) {

                val area = fromArea[position]
                setFrom(context, area)

            }

            override fun onNothingSelected(arg0: AdapterView<*>) {

            }
        }


        cboImageView.listener = object : CBOImageView.iCBOImageView {
            override fun OnAddClicked() {
                cboImageView.addAttachment(context)
            }

            override fun OnAdded() {
                OnUpdated(cboImageView.dataList)
            }

            override fun OnDeleted(file: String) {
                OnUpdated(cboImageView.dataList)
            }

            override fun OnUpdated(files: ArrayList<String>) {
                viewModel.setAttachment(files)
                setAttachment(files)
            }
        }


        rem_final.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                viewModel.othExpense.remark = s.toString()
            }

            override fun afterTextChanged(s: Editable) {

            }
        })



        etKm.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!s.equals("")) {
                    try {
                        var s = etPerKm.text.toString().toDouble()
                        ex_head_root.setText("" + (s * etKm.text.toString().toDouble()))
                    } catch (e: Exception) {

                    }

                }


                keyPressed = true
            }


            override fun afterTextChanged(s: Editable) {

            }
        })

        Cancel.setOnClickListener { onBackPressed() }

        Add.setOnClickListener {


            if (fromAreaStr.equals("--Select--")) {
                Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select the From Area...");
            } else if (toAreaStr.equals("--Select--")) {
                Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select the To Area...");
            } else if (fromAreaStr.equals(toAreaStr)) {
                Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select different Area...")
            } else if (expTypeStr.equals("--Select--")) {
                Custom_Variables_And_Method.getInstance().msgBox(this, "Please Select the Expense Type...");
            } else {

                if (expTypeStr.equals("As Per KM")) {

                    if (etKm.getText().isEmpty()) {

                        Custom_Variables_And_Method.getInstance().msgBox(this, "Please enter KM");
                    } else {

                        var m = mOthExpense();
                        m.fromArea = fromAreaStr
                        m.toArea = toAreaStr
                        m.setAmount(exhAmt.text.toString().trim().toDouble())
                        m.setExpType(expTypeStr)
                        m.setAttachment("")
                        m.setKm(etKm.text.toString().trim().toDouble())
                        m.setRemark(exp_remark_root.text.toString().trim())
                        m.fare = etPerKm.text.toString().trim()

                        ImageStr = ""
                        cboImageView.setAttachment("")
                        viewModel.other_expense_commit(context, m)
                    }
                } else if (expTypeStr.equals("Fix")) {

                    if (etFixAmt.getText().isEmpty()) {
                        Custom_Variables_And_Method.getInstance().msgBox(this, "Please enter Amount");
                    } else if (cboImageView.attachmentStr.equals("") && ImageStr.equals("")) {
                        customVariablesAndMethod.msgBox(context, "Please add an attachment....")
                    } else {


                        if (viewModel.getNewAttachment() != "") {
                            cboProgressDialog = CboProgressDialog(context, "Please Wait..\nuploading Image")
                            cboProgressDialog.show()
                            //File file2 = new File(Environment.getExternalStorageDirectory() + File.separator + "CBO" + File.separator + viewModel.getNewAttachment());
                            up_down_ftp().uploadFile(cboImageView.filesToUpload(), context)

                        } else {
                            var m = mOthExpense();
                            m.fromArea = fromAreaStr
                            m.toArea = toAreaStr
                            m.setAmount(etFixAmt.text.toString().trim().toDouble())
                            m.setExpType(expTypeStr)
                            m.setAttachment(viewModel.newAttachment)
                            m.setKm(0.0)
                            m.setRemark(exp_remark_root.text.toString().trim())
                            m.fare = "0"
                            viewModel.other_expense_commit(context, m)
                        }

                    }
                }


            }

        }

        // Custom_Variables_And_Method.closeCurrentPage(vc: self);
    }


    override fun setTitle() {
        if (viewModel.expense_type == eExpense.TA) {
            title.text = "Add TA"
        }
    }


    override fun loadExpenseType(expHeads: ArrayList<MPopupData>) {
        adapter = aNewPopup(applicationContext, R.layout.spin_row, expHeads)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        exphead.adapter = adapter
    }

    override fun addAttachment() {
        val filenameTemp = Custom_Variables_And_Method.PA_ID.toString() + "_" + Custom_Variables_And_Method.DCR_ID + "_OthExp_" + customVariablesAndMethod._currentTimeStamp + ".jpg"
        val intent = Intent(context, AttachImage::class.java)
        intent.putExtra("Output_FileName", filenameTemp)
        intent.putExtra("SelectFrom", AttachImage.ChooseFrom.all)
        startActivityForResult(intent, REQUEST_CAMERA)
    }


    override fun setRemarkCaption(caption: String) {
        textRemark.text = "Remark"
    }

    override fun setAmountCaption(caption: String) {
        ex_head_root_txt.text = caption
    }

    override fun setAmtHint(hint: String) {
        exhAmt.hint = hint
    }

    override fun setRemark(remark: String) {
        rem_final.setText(remark)
    }

    override fun setAmount(amount: Double?) {
        if (amount == 0.0) {
            exhAmt.setText("")
        } else {
            exhAmt.setText("" + amount!!)
        }

    }

    override fun setKm(km: Double?) {
    }

    override fun setRate(rate: Double?) {

    }

    override fun setAttachment(files: ArrayList<String>) {
        /*if (!path.isEmpty()) {
            add_attachment.setChecked(true);
            File OutputFile = new File(Environment.getExternalStorageDirectory() + File.separator + "CBO" + File.separator + path);
            previewCapturedImage(OutputFile.getPath());
        }*/
        cboImageView.updateDataList(files)
    }

    override fun onSendResponse(othExpense: mOthExpense) {
        val intent = Intent()
        intent.putExtra("othExpense", othExpense)
        intent.putExtra("eExpense", viewModel.expense_type)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent()
        setResult(Activity.RESULT_CANCELED, intent)
        finish()
    }

    override fun started(responseCode: Int?, message: String, description: String) {

    }

    override fun progess(responseCode: Int?, FileSize: Long?, value: Float?, description: String) {

    }

    override fun complete(responseCode: Int?, message: String, description: String) {
        cboProgressDialog.dismiss()
        viewModel.othExpense.attachment = viewModel.newAttachment

        var m = mOthExpense();
        m.fromArea = fromAreaStr
        m.toArea = toAreaStr
        m.setAmount(etFixAmt.text.toString().trim().toDouble())
        m.setExpType(expTypeStr)
        m.setAttachment(viewModel.newAttachment)
        m.setKm(0.0)
        m.setRemark(exp_remark_root.text.toString().trim())
        m.fare = "0"
        viewModel.other_expense_commit(context, m)
    }

    override fun aborted(responseCode: Int?, message: String, description: String) {
        cboProgressDialog.dismiss()
        AppAlert.getInstance().getAlert(context, message, description)
    }

    override fun failed(responseCode: Int?, message: String, description: String) {
        cboProgressDialog.dismiss()
        AppAlert.getInstance().getAlert(context, message, description)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {


        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CBOImageView.REQUEST_CAMERA -> cboImageView.onActivityResult(requestCode, resultCode, data)
                REQUEST_CAMERA -> {
                    val OutputFile = data!!.getSerializableExtra("Output") as File
                    viewModel.newAttachment = OutputFile.name
                }

                else -> super.onActivityResult(requestCode, resultCode, data)
            }
        }
    }


    override fun KmLayoutRequired(required: Boolean?) {

    }

    override fun onUpdateExpType(areaList: ArrayList<MPopupData>) {
        expType = areaList
        expType.add(0, MPopupData("0", "--Select--"))
        loadExpenseType(expType)

    }

    override fun onUpdateArea(areaList: ArrayList<MPopupData>) {
        fromArea = areaList
        toArea = areaList
        fromArea.add(0, MPopupData("0", "--Select--"))
        loadArea(fromArea)
        loadAreaT(fromArea)
    }

    private fun loadArea(areaList: ArrayList<MPopupData>) {
        adapter = aNewPopup(applicationContext, R.layout.spin_row, areaList)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        spFromArea.adapter = adapter
    }

    private fun loadAreaT(areaList: ArrayList<MPopupData>) {
        adapter = aNewPopup(applicationContext, R.layout.spin_row, areaList)
        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice)
        spToArea.adapter = adapter
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun setExpType(context: CustomActivity, expHead: MPopupData) {

        if (expHead.name.equals("As Per KM")) {
            ivImage.visibility = View.GONE
            llButtomContainer.visibility = View.VISIBLE
            attachment.visibility = View.GONE
            fixLayout.visibility = View.GONE
            amtLayout.visibility = View.VISIBLE
            setFocous(etKm)
        } else if (expHead.name.equals("Fix")) {
            if (!ImageStr.equals("")) {
                ivImage.visibility = View.VISIBLE
            } else {
                ivImage.visibility = View.GONE
            }

            llButtomContainer.visibility = View.VISIBLE
            fixLayout.visibility = View.VISIBLE
            amtLayout.visibility = View.GONE
            attachment.visibility = View.VISIBLE
            setFocous(etFixAmt)

        } else {
            llButtomContainer.visibility = View.GONE
        }

        expTypeStr = expHead.name

    }

    private fun setFrom(context: CustomActivity, data: MPopupData) {
        fromAreaStr = data.name

    }

    private fun setToArea(context: CustomActivity, data: MPopupData) {
        toAreaStr = data.name

    }

    override fun onUpdateView(ID: String, DCR_ID: String, FROM_STATION: String, TO_STATION: String, EXP_TYPE: String, FARE: String, KM: String, AMOUNT: String, REMARK: String, FILE_NAME: String) {

        if (!FROM_STATION.equals("")) {

            for (i in 0..fromArea.size - 1) {
                if (FROM_STATION.equals(fromArea[i].name)) {
                    spFromArea.setSelection(i)
                }
            }
        }
        if (!TO_STATION.equals("")) {

            for (i in 0..fromArea.size - 1) {
                if (TO_STATION.equals(fromArea[i].name)) {
                    spToArea.setSelection(i)
                }
            }
        }
        if (!EXP_TYPE.equals("")) {
            for (i in 0..expType.size - 1) {
                if (EXP_TYPE.equals(expType[i].name)) {
                    exphead.setSelection(i)
                    expTypeStrS = expType[i].name;
                }
            }
        }




        if (EXP_TYPE.equals("Fix")) {
            etKm.setText("")
            exhAmt.setText("0")
            ImageStr = FILE_NAME
            etFixAmt.setText(AMOUNT)
            setRemark(REMARK)
            if (!FILE_NAME.equals("")) {
                ivImage.visibility = View.VISIBLE
                Glide.with(this)
                        .load(FILE_NAME)
                        .into(ivImage)
            } else {
                ivImage.visibility = View.GONE
            }
        } else if (EXP_TYPE.equals("As Per KM")) {
            ivImage.visibility = View.GONE
            etKm.setText(KM)
            exhAmt.setText(AMOUNT)
            etFixAmt.setText("0")
            setRemark(REMARK)
        } else {
            etKm.setText("")
            exhAmt.setText("0")
            etFixAmt.setText("0")
            setRemark("")
        }



    }

    private fun setFocous(etEditTextsV: EditText) {

        etEditTextsV.setFocusable(true);
        etEditTextsV.setFocusableInTouchMode(true);
        etEditTextsV.requestFocus();
        var imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager;
        imm.showSoftInput(etEditTextsV, InputMethodManager.SHOW_IMPLICIT);
    }
}
