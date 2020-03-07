package utils_new.cboUtils.attachmentsyncdr

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper
import kotlinx.android.synthetic.main.layout_signature_attached.view.*
import utils.AppConstant.DR_UPLOAD_DIRECTORY
import utils_new.AppAlert
import utils_new.cboUtils.attachmentsync.aAttachmentS
import utils_new.cboUtils.attachmentsync.mAttachmentSync
import utils_new.up_down_ftp
import java.io.File

class aAttachmentSDr() : RecyclerView.Adapter<aAttachmentSDr.HomeViewHolder>(), up_down_ftp.AdapterCallback {

    var arrayCount = ArrayList<String>()

    override fun started(responseCode: Int?, message: String?, description: String?) {
    }

    override fun progess(responseCode: Int?, FileSize: Long?, value: Float?, description: String?) {
    }

    override fun complete(responseCode: Int?, message: String?, description: String?) {
        arrayList.get(responseCode!!).UPLOAD = 1
        arrayList.get(responseCode!!).UPLOAD_STATUS = true
        notifyItemChanged(responseCode)
        arrayCount.add("a")
        if (arrayCount.size == arrayList.size) {
            /*AppAlert.getInstance().Alert(context, "Alert", "All Files Upload Successfully", View.OnClickListener {
                (context as Activity).finish()
            })*/

            (context as IAttachmentSyncDr).syncDataToServer(arrayList)
        }

    }

    override fun aborted(responseCode: Int?, message: String?, description: String?) {
        arrayList.get(responseCode!!).UPLOAD = 3
        notifyItemChanged(responseCode)
    }

    override fun failed(responseCode: Int?, message: String?, description: String?) {
        arrayList.get(responseCode!!).UPLOAD = 0
        notifyItemChanged(responseCode)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_signature_attached, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        arrayList.get(position)?.let { holder.bind(it) }
    }

    lateinit var context: Context
    lateinit var arrayList: ArrayList<mAttachmentSyncDr>

    constructor(context: Context, arrayList: ArrayList<mAttachmentSyncDr>) : this() {
        this.context = context
        this.arrayList = arrayList
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: mAttachmentSyncDr) = with(itemView) {
            (context as IAttachmentSyncDr).setCount(arrayCount.size, arrayList.size)

            if (data.UPLOAD != 1) {
                Glide.with(context)
                        .load(data.FILE_ATTACHMENT)
                        .error(R.drawable.no_image)
                        .into(ivIcon)
                tvName.setText(data.NAME)

                if (data.UPLOAD_STATUS) {
                    progressBaar.visibility = View.GONE
                } else if (data.UPLOAD == 2) {
                    progressBaar.visibility = View.VISIBLE
                } else if (data.UPLOAD == 0) {
                    data.UPLOAD = 2
                    //uploading code
//                Toast.makeText(context, "Not Uploded"+layoutPosition, Toast.LENGTH_SHORT).show()
//                    up_down_ftp().setResponseCode(layoutPosition).uploadFile(DR_UPLOAD_DIRECTORY, File(data.FILE_ATTACHMENT),context, this@aAttachmentSDr)
                    up_down_ftp().setResponseCode(layoutPosition).uploadFileAPI(DR_UPLOAD_DIRECTORY, File(data.FILE_ATTACHMENT), context, this@aAttachmentSDr)


                }
            } else {
                llContainer.visibility = View.GONE
            }
        }
    }
}
