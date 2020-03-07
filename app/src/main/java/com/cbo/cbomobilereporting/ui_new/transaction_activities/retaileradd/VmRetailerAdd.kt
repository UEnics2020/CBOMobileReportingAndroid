package com.cbo.cbomobilereporting.ui_new.transaction_activities.retaileradd

import androidx.appcompat.app.AppCompatActivity
import com.cbo.cbomobilereporting.MyCustumApplication
import saleOrder.ViewModel.CBOViewModel
import java.util.ArrayList

class VmRetailerAdd : CBOViewModel<IRetailerAdd>() {

    override fun onUpdateView(context: AppCompatActivity?, view: IRetailerAdd?) {

        if (view != null) {
            view.getReferencesById()
            view.getActivityTitle()
            view.setOnClickListner()
            view.setTile(view.getActivityTitle())

        }
    }

    fun getData(): ArrayList<MRetailerPopup>? {

        var arrList = ArrayList<MRetailerPopup>()
        var id = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_CAT_ID", "")
        var name = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_CAT_NAME", "")

        var idStr = id.removePrefix("[").removeSuffix("]").split(",")
        var nameStr = name.removePrefix("[").removeSuffix("]").split(",")

        for (i in 0..idStr.size - 1) {
            var mCAt = MRetailerPopup()
            mCAt.id = idStr.get(i)
            mCAt.name = nameStr.get(i)
            arrList.add(mCAt)
        }

        return arrList;
    }

    fun getDataR(): ArrayList<MRetailerPopup>? {

        var arrList = ArrayList<MRetailerPopup>()
        var id = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_ROUT_ID", "")
        var name = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_ROUT_NAME", "")
        var pa_id = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("RETAILERADD_PA_ID_L", "")

//        var idStr = id.removePrefix("[").removeSuffix("]").split(",")
//        var idStr = id.split("~")
//        var nameStr = name.removePrefix("[").removeSuffix("]").split("~,")
//        var paIDStr = pa_id.removePrefix("[").removeSuffix("]").split(",")

        var idStr = id.split("~")
        var nameStr = name.split("~")
        var paIDStr = pa_id.split("~")

        for (i in 0..idStr.size - 1) {
            var mCAt = MRetailerPopup()
            mCAt.id = idStr.get(i)
            mCAt.name = nameStr.get(i)

            if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("IDD", "").trim().equals(paIDStr.get(i).trim())) {
                arrList.add(mCAt)
            }

        }

        return arrList;
    }

}