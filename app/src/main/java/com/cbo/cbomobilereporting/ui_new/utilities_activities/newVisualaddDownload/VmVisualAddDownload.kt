package com.cbo.cbomobilereporting.ui_new.utilities_activities.newVisualaddDownload

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.ResponseBuilder
import org.json.JSONArray
import org.json.JSONException
import saleOrder.ViewModel.CBOViewModel
import services.MyAPIService
import utils_new.AppAlert
import java.util.HashMap

class VmVisualAddDownload : CBOViewModel<IVisualAddDownload>() {

    private var mDownLoadVisualAdd = ArrayList<MDownLoadVisualAdd>()
    private lateinit var cbohelp: CBO_DB_Helper

    override fun onUpdateView(context: AppCompatActivity?, view: IVisualAddDownload?) {
        cbohelp = CBO_DB_Helper(context)

        if (view != null) {
            view.referenceId()
//            view.onUpdateArray(getArrayData())
//            getVisualAddFile(context!!)
        }
    }

    fun setArrayData(mDownLoadVisualAdd: ArrayList<MDownLoadVisualAdd>) {
        this.mDownLoadVisualAdd = mDownLoadVisualAdd
    }

    fun getArrayData(): ArrayList<MDownLoadVisualAdd> {
        return mDownLoadVisualAdd
    }

    fun getVisualAddFile(context: Activity) {
//        cbohelp.deleteVisualAddTable()
        mDownLoadVisualAdd.clear()
        val request = HashMap<String, String>()
        request["sCompanyFolder"] = MyCustumApplication.getInstance().user.companyCode
        request["iPA_ID"] = "" + MyCustumApplication.getInstance().user.id
        val tables = java.util.ArrayList<Int>()
        tables.add(0)
        MyAPIService(context)
                .execute(ResponseBuilder("VISUALAID_DOWNLOAD_ZIP", request)
                        .setMultiTable(false).setTables(tables).setDescription("Please Wait....\nProcessing downloadable files....").setResponse(object : CBOServices.APIResponse {
                            override fun onComplete(message: Bundle) {
                                parser_data(message, context)
                            }

                            override fun onResponse(response: Bundle) {
                            }

                            override fun onError(s: String, s1: String) {

                            }


                        })
                )

    }

    private fun parser_data(result: Bundle, context: Activity) {
        //parse download list data


        if (result != null) {
            try {
                val table0 = result.getString("Tables0")
                val jsonArray1 = JSONArray(table0)
                mDownLoadVisualAdd.clear()

                for (i in 0 until jsonArray1.length()) {
                    val c = jsonArray1.getJSONObject(i)
                    val visualAd = MDownLoadVisualAdd()
                    visualAd.iteM_ID = c.getString("ITEM_ID")
                    visualAd.iteM_NAME = c.getString("ITEM_NAME")
                    visualAd.filE_NAME = c.getString("FILE_NAME")
                    visualAd.UPLOAD_STATUS = false
                    visualAd.filE_DOWNLOAD_VERSION = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", "")
                    mDownLoadVisualAdd.add(visualAd)
                }
                MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("SIZEE", "" + mDownLoadVisualAdd.size)

//                view.onUpdateArray(mDownLoadVisualAdd);
                saveVisualinLocal(mDownLoadVisualAdd)
//                setArrayData(mDownLoadVisualAdd)

            } catch (e: JSONException) {
                val handler = Handler(Looper.getMainLooper())
                handler.post {
                    Log.d("MYAPP", "objects are: $e")
                    AppAlert.getInstance().getAlert(context, "Missing field error", context.getResources().getString(R.string.service_unavilable) + e.toString())
                    e.printStackTrace()
                }
            }

        }


    }

    private fun saveVisualinLocal(listData: ArrayList<MDownLoadVisualAdd>) {

        cbohelp.deleteVisualAddTable()
        for (mDownLoadVisualAdd in listData) {
            cbohelp.SaveVisualAdd(mDownLoadVisualAdd)
        }

        view.onUpdateArray(listData)
//        setArrayData(listData)

    }

    fun getDataFromLocal() {
        mDownLoadVisualAdd.clear()
        mDownLoadVisualAdd = cbohelp.listVisualData
//        setArrayData(mDownLoadVisualAdd)
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("SIZEE", "" + mDownLoadVisualAdd.size)

//        view.onUpdateArray(mDownLoadVisualAdd);


    }

}