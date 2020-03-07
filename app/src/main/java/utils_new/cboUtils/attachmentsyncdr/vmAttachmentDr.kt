package utils_new.cboUtils.attachmentsyncdr

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import com.uenics.javed.CBOLibrary.CBOServices
import com.uenics.javed.CBOLibrary.ResponseBuilder
import saleOrder.ViewModel.CBOViewModel
import services.MyAPIService
import utils_new.AppAlert
import utils_new.cboUtils.attachmentsync.IAttachmentSync
import utils_new.cboUtils.attachmentsync.mAttachmentSync

class vmAttachmentDr : CBOViewModel<IAttachmentSyncDr>() {

    private var mAttachmentSync = ArrayList<mAttachmentSyncDr>()
    private lateinit var cbohelp : CBO_DB_Helper


    override fun onUpdateView(context: AppCompatActivity?, view: IAttachmentSyncDr?) {
        cbohelp = CBO_DB_Helper(context)
        if (view != null) {
            view.referenceId()
            view.onUpdateArray(getArrayData())
        }

    }

    fun setArrayData(mAttachmentSync: ArrayList<mAttachmentSyncDr>) {
        this.mAttachmentSync = mAttachmentSync
    }

    fun getArrayData(): ArrayList<mAttachmentSyncDr> {
        return mAttachmentSync
    }

    fun syncDataToServer(context: AppCompatActivity,attachmentArray: java.util.ArrayList<mAttachmentSyncDr>) {
        val docTypeStr = StringBuilder()
        val dcsId_ArrStr = StringBuilder()
        val indexArrStr = StringBuilder()
        val latlngStr = StringBuilder()
        val dcs_file_str = StringBuilder()

        var count = 0;
        for (array in attachmentArray) {

            var SEPERATER = "^"
            //var SEPERATER1 = ""
            if (count == 0){
                 SEPERATER = ""

                 //SEPERATER1 = ""
            }

            count++;

            dcs_file_str.append(SEPERATER).append(array.FILE_ATTACHMENT)
            docTypeStr.append(SEPERATER).append(array.DCS_TYPE)
            dcsId_ArrStr.append(SEPERATER).append(array.ID)
            indexArrStr.append(SEPERATER).append(array.DCS_INDES)
            latlngStr.append(SEPERATER).append(array.LAT_LONG)



        }

        if (dcsId_ArrStr.toString().isEmpty()) {
            AppAlert.getInstance().getAlert(context, "Alert!!", "Attachmnet Not Available")
        } else {

            val request = HashMap<String, String>()
            request["sCompanyFolder"] = MyCustumApplication.getInstance().user.companyCode
            request["PA_ID"] = MyCustumApplication.getInstance().user.id
            request["DCS_ARR"] = docTypeStr.toString()
            request["DCS_ID_ARR"] = dcsId_ArrStr.toString()
            request["DCS_FILE_ARR"] = dcs_file_str.toString()
            request["INDEX_ARR"] = indexArrStr.toString()
            request["LOC_LAT_ARR"] = latlngStr.toString()

            MyAPIService(context).execute(ResponseBuilder("DRCHEM_REG_FILECOMMIT", request)
                    .setDescription("Please Wait..\n" + "Registration in progress......")
                    .setResponse(object : CBOServices.APIResponse {
                        @Throws(Exception::class)
                        override fun onComplete(bundle: Bundle) {




                                AppAlert.getInstance().Alert(context, "Alert",
                                        "All Files Upload Successfully",
                                        View.OnClickListener {
                                    context.finish()
                                })



                        }

                        @Throws(Exception::class)
                        override fun onResponse(bundle: Bundle) {
                            for (array in attachmentArray) {
                                //cbohelp.updateLatLong(array.LAT_LONG, array.ID, array.DCS_TYPE, array.DCS_INDES, array.FILE_ATTACHMENT, "1","1")
                                cbohelp.updatedLat_Long_RegFile(array.ID)
                            }

                        }

                        override fun onError(s: String, s1: String) {
                            AppAlert.getInstance().getAlert(context, s, s1)
                        }
                    }))


        }
    }


}