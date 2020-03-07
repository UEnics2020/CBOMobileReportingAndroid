package com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense.newotherExp

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense.*
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.ResponseBuilder
import org.json.JSONArray
import org.json.JSONException
import saleOrder.ViewModel.CBOViewModel
import services.MyAPIService
import utils_new.AppAlert
import utils_new.Custom_Variables_And_Method
import java.util.*

class VmNewOtherExp : CBOViewModel<IOtherExpense>() {
    override fun onUpdateView(context: AppCompatActivity?, view: IOtherExpense?) {
        othExpenseDB = OthExpenseDB(context)
        expHeadDB = ExpHeadDB(context)
        cbohelp = CBO_DB_Helper(context)
        view?.getReferencesById()

        fromArea = ArrayList<MPopupData>()
        toArea = ArrayList<MPopupData>()
        expType = ArrayList<MPopupData>()
    }

    private lateinit var expense: mExpense
    internal var expense_type = eExpense.TA
    internal var othExpense = mOthExpense()
    internal var newAttachment = ""
    private var othExpenseDB: OthExpenseDB? = null
    private var expHeadDB: ExpHeadDB? = null
    private var cbohelp: CBO_DB_Helper? = null
    private lateinit var expType: ArrayList<MPopupData>
    private lateinit var toArea: ArrayList<MPopupData>
    private lateinit var fromArea: ArrayList<MPopupData>


    fun setExpense(expense: mExpense) {
        this.expense = expense
        /*if (view != null) {
                   view.loadExpenseHead(expense.getExpHeads());
               }*/
    }

    fun getExpense(): mExpense {
        return expense
    }

    fun getExpense_type(): eExpense {
        return expense_type
    }

    fun setExpense_type(expense_type: eExpense) {
        this.expense_type = eExpense.TA
        if (view != null) {
            view.setTitle()
        }
    }


    fun getOthExpense(): mOthExpense {
        return othExpense
    }

    fun getNewAttachment(): String {
        return newAttachment
    }

    fun getNewAttachmentArr(): ArrayList<String> {
        return if (this.getNewAttachment().isEmpty()) ArrayList() else ArrayList(Arrays.asList(*this.newAttachment.split("\\|\\^".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()))
    }

    fun setNewAttachment(newAttachment: String) {
        this.newAttachment = newAttachment
    }

    fun setAttachment(attachment: ArrayList<String>) {
        val sb = StringBuilder()
        var count = 0

        for (file in attachment) {
            if (count != 0) {
                sb.append("|^")
            }
            ++count
            sb.append(file)
        }
        this.newAttachment = sb.toString()


    }


    fun other_expense_commit(context: AppCompatActivity, m: mOthExpense) {
        //Start of call to service

        val request = HashMap<String, String>()
        request["sCompanyFolder"] = MyCustumApplication.getInstance().user.companyCode
        request["sDCR_ID"] = MyCustumApplication.getInstance().user.dcrId
        request["iLOGIN_PA_ID"] = MyCustumApplication.getInstance().user.id
        request["sFROM_STATION"] = m.fromArea
        request["sTO_STATION"] = m.toArea
        request["sEXP_TYPE"] = m.expType
        request["iFARE"] = m.fare
        request["iKM"] = "" + m.km
        request["iAMOUNT"] = "" + m.amount
        request["sREMARK"] = m.remark
        request["sFILE_NAME"] = getOthExpense().getAttachmentName()

        val tables = ArrayList<Int>()
        tables.add(0)

        MyAPIService(context).execute(ResponseBuilder("EXPTAMANUAL_COMMIT", request)
                .setTables(tables)
                .setDescription(("Please wait.....\n" + "Processing your expense request...."))
                .setResponse(object : CBOServices.APIResponse {
                    @Throws(Exception::class)
                    override fun onComplete(bundle: Bundle) {
                        if (m.expType.equals("As Per KM")) {
                            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("ACTUALFARE", "" + m.amount);
                            getOthExpense().setAmount(m.amount)
                        } else if (m.expType.equals("Fix")) {
                            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("ACTUALFARE", "" + m.amount);
                            getOthExpense().setAmount(m.amount)
                        }


                        Custom_Variables_And_Method.getInstance().msgBox(context, (if (getExpense_type() == eExpense.None) "Exp." else getExpense_type().name) + "  Added Successfully")
                        if (view != null) {
                            view.onSendResponse(getOthExpense())
                        }
                    }

                    @Throws(Exception::class)
                    override fun onResponse(bundle: Bundle) {
                        parser2(bundle)

                    }

                    override fun onError(s: String, s1: String) {
                        AppAlert.getInstance().getAlert(context, s, s1)
                    }
                }))


    }


    @Throws(JSONException::class)
    private fun parser2(result: Bundle) {


//        val table0 = result.getString("Tables0")
//        val jsonArray1 = JSONArray(table0)
//
//        val `object` = jsonArray1.getJSONObject(0)
//

//        if (`object`.getString("INVALIDMSG").isEmpty()) {
//            othExpenseDB.insert(getOthExpense().setId(`object`.getInt("ID"))
//                    .setTime(Custom_Variables_And_Method.getInstance().currentTime(MyCustumApplication.getInstance())))
//
//            cbohelp.insert_Expense("" + getOthExpense().expHead.id,
//                    getOthExpense().expHead.name, "" + getOthExpense().amount!!,
//                    getOthExpense().remark, getOthExpense().attachment,
//                    "" + getOthExpense().id, getOthExpense().time)
//
//            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("da_val", "" + getExpense().dA_Amt!!)
//
//        }

//        customVariablesAndMethod.msgBox(context, " Exp. Added Sucessfully");


    }


    //getpopup data
    fun getPopupData(context: Context) {


        val request = HashMap<String, String>()

        request.put("sCompanyFolder", MyCustumApplication.getInstance().getUser().getCompanyCode())
        request.put("iLOGIN_PA_ID", MyCustumApplication.getInstance().getUser().getID())
        request.put("iPA_ID", MyCustumApplication.getInstance().getUser().getID())
        request.put("sDCR_ID", MyCustumApplication.getInstance().user.dcrId)

        val tables = ArrayList<Int>()
        tables.add(0)
        tables.add(1)
        tables.add(2)

        MyAPIService(context).execute(ResponseBuilder("EXPTAMANUAL_POPULATE", request)
                .setDescription(("Please Wait..\n" + " in progress......"))
                .setTables(tables)
                .setResponse(
                        object : CBOServices.APIResponse {
                            @Throws(Exception::class)
                            override fun onComplete(bundle: Bundle) {
                                parseJson(bundle, context);
                            }

                            @Throws(Exception::class)
                            override fun onResponse(bundle: Bundle) {
                                //                        parseJson(bundle, context, listener);

                            }

                            override fun onError(s: String, s1: String) {
                                AppAlert.getInstance().getAlert(context, s, s1)
                            }
                        }))


    }

    @Throws(Exception::class)
    private fun parseJson(bundle: Bundle, context: Context) {

        val table0 = bundle.getString("Tables0")
        val jsonArray1 = JSONArray(table0)

        for (i in 0 until jsonArray1.length()) {
            val jsonObj = jsonArray1.getJSONObject(i)
            val name = jsonObj.getString("AREA")
            fromArea.add(MPopupData("", name))

        }

        view.onUpdateArea(fromArea)

        val table1 = bundle.getString("Tables1")
        val jsonArray2 = JSONArray(table1)

        for (i in 0 until jsonArray2.length()) {
            val jsonObj = jsonArray2.getJSONObject(i)
            val name = jsonObj.getString("EXPANCE_TYPE")
            expType.add(MPopupData("", name))
        }
        view.onUpdateExpType(expType)

        val table2 = bundle.getString("Tables2")
        val jsonArray3 = JSONArray(table2)

        for (i in 0 until jsonArray3.length()) {
            val jsonObj = jsonArray3.getJSONObject(i)
            val ID = jsonObj.getString("ID")
            val DCR_ID = jsonObj.getString("DCR_ID")
            val FROM_STATION = jsonObj.getString("FROM_STATION")
            val TO_STATION = jsonObj.getString("TO_STATION")
            val EXP_TYPE = jsonObj.getString("EXP_TYPE")
            val FARE = jsonObj.getString("FARE")
            val KM = jsonObj.getString("KM")
            val AMOUNT = jsonObj.getString("AMOUNT")
            val REMARK = jsonObj.getString("REMARK")
            val FILE_NAME = jsonObj.getString("FILE_NAME")
            view.onUpdateView(ID, DCR_ID, FROM_STATION, TO_STATION, EXP_TYPE, FARE, KM, AMOUNT, REMARK, FILE_NAME)
        }


    }

    fun setOthExpense(othExpense: mOthExpense) {
        this.othExpense = othExpense
        var expHeads = ArrayList<mExpHead>()
        if (view != null) {
//            if (othExpense.id != 0) {
//
//                expHeads.add(othExpense.expHead)
//
//            } else {
//                expHeads = expHeadDB!!.get_NotAdded(getExpense_type())
//                expHeads.add(0, mExpHead(0, "---Select Exp. Head---"))
//            }
//            expense.expHeads = expHeads
//            view.loadExpenseHead(expense.expHeads)
//            getOthExpense().attachmentArr[0]="/storage/emulated/0/CBO/11190_16100_attach_1582806178736.jpg"
//            view.setAttachment(getOthExpense().attachmentArr)

        }

    }
}