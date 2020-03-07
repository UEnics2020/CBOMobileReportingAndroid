package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin

import android.content.Context
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.cbo.cbomobilereporting.MyCustumApplication
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.ResponseBuilder
import org.json.JSONArray
import org.json.JSONException
import saleOrder.ViewModel.CBOViewModel
import services.MyAPIService
import utils_new.Custom_Variables_And_Method
import java.util.ArrayList
import java.util.HashMap

class VmCheckIn : CBOViewModel<ICheckIn>() {


    var locationList = ArrayList<MSpinData>()
    override fun onUpdateView(context: AppCompatActivity?, view: ICheckIn?) {

        if (view != null) {
            view.getReferencesById()
            view.getActivityTitle()
            view.setOnClickListner()
            view.setTile(view.getActivityTitle())

        }
    }


    fun getData(): ArrayList<MSpinData> {
        return locationList
    }

    fun setData(locationList: ArrayList<MSpinData>) {
        this.locationList = locationList
    }

    fun getAddressData(context: AppCompatActivity) {

        //Start of call to service
        val request = HashMap<String, String>()
        request["sCompanyFolder"] = MyCustumApplication.getInstance().user.companyCode
        request["iPaId"] = MyCustumApplication.getInstance().user.id
        request["sLATLONG"] = Custom_Variables_And_Method.getInstance().getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON)
        val tables = ArrayList<Int>()
        tables.add(0)

        MyAPIService(context).execute(ResponseBuilder("INTIME_DDL_MOBILE", request)
                .setTables(tables)
                /*.setShowProgess(orders.size() == 0 )*/
                .setResponse(object : CBOServices.APIResponse {
                    @Throws(Exception::class)
                    override fun onComplete(bundle: Bundle) {
                        parser2(bundle!!, context)
                    }

                    @Throws(Exception::class)
                    override fun onResponse(bundle: Bundle?) {



                    }

                    override fun onError(s: String, s1: String) {


                    }
                }))
    }

    //
    @Throws(JSONException::class)
    private fun parser2(result: Bundle, context: Context) {
        if (result != null) {
            val table0 = result.getString("Tables0")
            val jsonArray1 = JSONArray(table0)
            for (i in 0 until jsonArray1.length()) {
                val c = jsonArray1.getJSONObject(i)

                var spinData  = MSpinData(c.getString("PA_ID").toInt(),
                        c.getString("PA_NAME"), c.getString("PA_LAT_LONG"), c.getString("PA_LOC"))
                spinData.pA_TIME = c.getString("TIME")
                locationList.add(spinData)

                if (c.getString("IsSelected").equals("1")) {
                    //officename.setText(c.getString("PA_NAME"))
                    view.setOffice(spinData);
                    /*view.setLatLng(c.getString("PA_LAT_LONG"))
                    view.setTime(c.getString("TIME"))*/
                }
            }


            setData(locationList)
        }

    }

}


