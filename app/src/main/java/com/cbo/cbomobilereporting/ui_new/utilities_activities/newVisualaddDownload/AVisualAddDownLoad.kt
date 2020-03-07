package com.cbo.cbomobilereporting.ui_new.utilities_activities.newVisualaddDownload

import android.content.Context
import android.graphics.Color
import android.opengl.Visibility
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.R
import kotlinx.android.synthetic.main.layout_download_visual.view.*
import utils_new.cboDownload.CBODownload
import utils_new.cboUtils.attachmentsyncdr.IAttachmentSyncDr

class AVisualAddDownLoad() : RecyclerView.Adapter<AVisualAddDownLoad.HomeViewHolder>() {

    var arrayCount = ArrayList<String>()
    var context: Context? = null
    var count = 0
    var arrayList: ArrayList<MDownLoadVisualAdd>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_download_visual, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = arrayList!!.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        arrayList!!.get(position)?.let { holder.bind(it) }
    }


    constructor(context: Context, arrayList: ArrayList<MDownLoadVisualAdd>) : this() {
        this.context = context
        this.arrayList = arrayList
    }

    constructor(context: Context, arrayList: ArrayList<MDownLoadVisualAdd>, count: Int) : this() {
        this.context = context
        this.arrayList = arrayList
        this.count = count
    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: MDownLoadVisualAdd) = with(itemView) {
            tvDataInfo.visibility = View.VISIBLE
            tvTitle.setText(data.iteM_NAME)
            tvTitle.setText(data.filE_NAME.removeSuffix(".zip"))

        }
    }
}