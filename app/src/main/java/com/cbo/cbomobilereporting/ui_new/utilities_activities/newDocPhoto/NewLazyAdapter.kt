package com.cbo.cbomobilereporting.ui_new.utilities_activities.newDocPhoto

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.R
import kotlinx.android.synthetic.main.new_row_doclist.view.*
import utils.adapterutils.DocSampleModel
import utils.adapterutils.ImageLoader
import utils_new.Custom_Variables_And_Method

class NewLazyAdapter() : RecyclerView.Adapter<NewLazyAdapter.HomeViewHolder>() {

    var recycleViewOnItemClickListener: IRecyclerViewList? = null
    var imageLoader: ImageLoader? = null
    val stub_id = R.drawable.no_image
    var visual_pdf: String? = null
    var sCheck: String? = null
    var checkID = java.util.ArrayList<String>()
    var sampleId = java.util.ArrayList<String>()
    var customVariablesAndMethod: Custom_Variables_And_Method? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.new_row_doclist, parent, false)
        return HomeViewHolder(view)
    }

    override fun getItemCount(): Int = arrayList.size

    override fun onBindViewHolder(holder: HomeViewHolder, position: Int) {
        arrayList.get(position)?.let { holder.bind(it) }
    }

    lateinit var context: Activity
    lateinit var arrayList: ArrayList<DocSampleModel>

    fun setOnClickListner(recycleViewOnItemClickListener: IRecyclerViewList?) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener
    }

    constructor(context: Activity, arrayList: ArrayList<DocSampleModel>, s: String, checkId1: ArrayList<String>, sampleId: ArrayList<String>) : this() {
        this.context = context
        this.arrayList = arrayList
        sCheck = s
        checkID = checkId1
        this.sampleId = sampleId

        imageLoader = ImageLoader(context.applicationContext)
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance()
        visual_pdf = Custom_Variables_And_Method.getInstance().getDataFrom_FMCG_PREFRENCE(context, "VISUALAIDPDFYN")

    }

    inner class HomeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(data: DocSampleModel) = with(itemView) {

            dcr_workwith_name.setText(data.getName())
            dcr_workwith_id.setText(data.getId())
            list_row_id.setText(data.getRowId())


            if (sCheck == "1") {
                btnCheckBox.setChecked(false)
            }

            if (data.get_Checked()) { //            holder.promoted.setVisibility(View.VISIBLE);
                btnCheckBox.setChecked(true)
            } else { //            holder.promoted.setVisibility(View.GONE);
                btnCheckBox.setChecked(false)
            }

            if (checkID.contains(data.getId())) {
                btnCheckBox.setChecked(true)
                btnCheckBox.setClickable(false)
            } else {
                btnCheckBox.setChecked(false)
                btnCheckBox.setClickable(true)
            }


            if (data.isHighlighted()) {
                dcr_workwith_name.setTextColor(-0x7ccd)
            } else {
                if (data.get_Checked()) {
                    dcr_workwith_name.setTextColor(-0x838485)
                } else {
                    dcr_workwith_name.setTextColor(-0x1000000)
                }
            }

            btnCheckBox.setOnClickListener {
                recycleViewOnItemClickListener!!.onClickData(position, btnCheckBox, data.getId())

            }

            imageLoader!!.DisplayImage1(doc_sample_img, data.getId(), visual_pdf, data.getName())

        }
    }
}