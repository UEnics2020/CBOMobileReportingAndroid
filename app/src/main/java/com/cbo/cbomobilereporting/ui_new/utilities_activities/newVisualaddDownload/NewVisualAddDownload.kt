package com.cbo.cbomobilereporting.ui_new.utilities_activities.newVisualaddDownload

import android.os.Bundle
import android.os.Environment
import android.text.TextUtils
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import kotlinx.android.synthetic.main.activity_new_visual_add_download.*
import utils_new.Custom_Variables_And_Method
import utils_new.cboDownload.CircleProgressBar
import utils_new.multipledownload.CBOZipDownload
import utils_new.multipledownload.DownloadInfo
import utils_new.multipledownload.config.DownloadConfig
import java.io.File

class NewVisualAddDownload : CustomActivity(), IVisualAddDownload {

    private var statusStr: String? = ""
    private var pa_name: String = ""
    private var progress_text: TextView? = null
    private var custom_progressBar: CircleProgressBar? = null
    private lateinit var vVisualAdd: VmVisualAddDownload
    private lateinit var downloadInfoList: MutableList<DownloadInfo>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_visual_add_download)
        context = this
        vVisualAdd = ViewModelProviders.of(this).get(VmVisualAddDownload::class.java)
//        vVisualAdd!!.getVisualAddFile(context)
        pa_name = Custom_Variables_And_Method.PA_NAME
        visual_empname.setText("Welcome  $pa_name")
        visual_compname.setText(Custom_Variables_And_Method.COMPANY_NAME)
        tvTotalCount1.setText("" + CBOZipDownload.getAllDownloadList().size)

        if (intent.getStringExtra("V_DOWNLOAD") != null) {
            statusStr = intent.getStringExtra("V_DOWNLOAD")
        }



        vVisualAdd!!.getVisualAddFile(context)
    }

    private fun checkDemo() {

        if (statusStr.equals("Y")) {
            if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("FIRST_TIME", "").equals("")) {
                callFirstTime()
            } else {
                val groupByTag = false
                DownloadListActivity.start(this, MyCustumApplication.getInstance().user.companyCode)
                finish()
            }
        }

    }

    override fun onStart() {
        super.onStart()
        vVisualAdd!!.setView(context, this)
    }

    override fun referenceId() {

        val toolbar = findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setDefaultDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.back_hadder_2016)
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            toolbar.setNavigationOnClickListener { view -> onBackPressed() }
        }
        val title = toolbar.findViewById<TextView>(R.id.title)
        title.setText("VisualAid Download")

        startdownload.setOnClickListener {

            if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("FIRST_TIME", "").equals("")) {
                callFirstTime()
            } else {
                val groupByTag = false
                DownloadListActivity.start(this, MyCustumApplication.getInstance().user.companyCode)
                finish()
            }

        }

    }


    var visualAddArray = ArrayList<MDownLoadVisualAdd>();
    override fun onUpdateArray(visualAddArray: ArrayList<MDownLoadVisualAdd>) {
        this.visualAddArray = visualAddArray;
        checkDemo()


    }

    override fun syncDataToServer(visualAddArray: ArrayList<MDownLoadVisualAdd>) {

    }

    override fun setCount(count: Int, totalCount: Int, layoutPosition: Int, tvDataInfo: TextView, custom_progressBar: CircleProgressBar) {
        this.progress_text = tvDataInfo
        this.custom_progressBar = custom_progressBar
        tvCount!!.text = "" + count
        tvTotalCount1!!.text = "" + totalCount
        if (count == totalCount) {
            tvTotalCount1!!.setTextColor(resources.getColor(R.color.colorPrimary))
        }

    }

    private fun callFirstTime() {
        DownloadConfig.newBuilder()
                //Optional,set the maximum number of tasks to run, default 3.
//                .setMaxRunningTaskNum(visualAddArray.size)
                .setMaxRunningTaskNum(3)
                //Optional,set the minimum available storage space size for downloading to avoid insufficient storage space during downloading, default is 4kb.
                .setMinUsableStorageSpace(4 * 1024L)
                .build()
        CBOZipDownload.deleteByTag(MyCustumApplication.getInstance().user.companyCode)

        for (i in 0..visualAddArray.size - 1) {
            if (!visualAddArray[i].filE_NAME.removeSuffix(".zip").equals("Part")) {


                val file = File(Environment.getExternalStorageDirectory(), "tiwari/" + visualAddArray[i].filE_NAME.removeSuffix(".zip"))
                var url = MyCustumApplication.getInstance()
                        .getDataFrom_FMCG_PREFRENCE("API_URL_MOBILE", "") +
                        "download/VISUALAID/" +
                        visualAddArray[i].filE_NAME.removeSuffix(".zip") + "/zip"
                CBOZipDownload.newRequest(url, file.absolutePath)
                        .tag(MyCustumApplication.getInstance().user.companyCode)
                        .forceReDownload(true)
                        .submit()
            }
        }

        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("FIRST_TIME", "1")
        val groupByTag = false
        DownloadListActivity.start(applicationContext, MyCustumApplication.getInstance().user.companyCode)
        finish()
    }


}
