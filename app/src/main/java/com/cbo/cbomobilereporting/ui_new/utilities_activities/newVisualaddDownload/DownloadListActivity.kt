package com.cbo.cbomobilereporting.ui_new.utilities_activities.newVisualaddDownload

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.MyCustumApplication
import com.cbo.cbomobilereporting.R
import kotlinx.android.synthetic.main.activity_download_list.*
import kotlinx.android.synthetic.main.activity_download_list.visual_compname
import kotlinx.android.synthetic.main.activity_download_list.visual_empname
import kotlinx.android.synthetic.main.item_download_list.view.*
import utils.Utils
import utils_new.AppAlert
import utils_new.Custom_Variables_And_Method
import utils_new.multipledownload.DownloadInfo
import utils_new.multipledownload.DownloadListener
import utils_new.multipledownload.CBOZipDownload
import utils_new.multipledownload.utils.LogUtil
import java.io.File
import java.util.*

class DownloadListActivity : AppCompatActivity(), IDownloadList {
    override fun setCount() {

        tvCount.setText("" + CBOZipDownload.getDownloadedList().size)
        downloadObserver.enable()
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        //Get all download list.
//        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getAllDownloadList() else CBOZipDownload.getDownloadListByTag(tag)
//        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getDownloadedList() else CBOZipDownload.getDownloadListByTag(tag)
        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getDownloadingList() else CBOZipDownload.getDownloadingList()

        //Sort download list if need.
        Collections.sort(downloadInfoList) { o1, o2 -> (o1.createTime - o2.createTime).toInt() }
        rvDownloadList.layoutManager = linearLayoutManager
        downloadAdapter = DownloadAdapter(map, downloadInfoList)
        rvDownloadList.adapter = downloadAdapter


        if (CBOZipDownload.getAllDownloadList().size == CBOZipDownload.getDownloadedList().size) {

            //Initialize the Handler
            mDelayHandler = Handler()

            tvMSG.visibility = View.VISIBLE
            //Navigate with delay
            mDelayHandler!!.postDelayed(mRunnable, SPLASH_DELAY)

        }
//        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getDownloadingList() else CBOZipDownload.getDownloadingList()
//        downloadAdapter!!.notifyDataSetChanged()

    }

    private var mDelayHandler: Handler? = null
    private val SPLASH_DELAY: Long = 10000 //5 seconds
    internal val mRunnable: Runnable = Runnable {
        if (!isFinishing) {
            showAlert()
        }
    }

    private var pa_name: String = ""
    private var downloadObserver: DownloadListener = object : DownloadListener() {
        override fun onProgress(progress: Int) {
            val downloadInfo = downloadInfo
            if (downloadInfo.extraData != null) {
                val viewHolder = downloadInfo.extraData as DownloadViewHolder

                val tag = map[viewHolder]
                if (tag != null && tag.id == downloadInfo.id) {
                    viewHolder.bindData(downloadInfo, status)
                }
            }

        }

        override fun onFailed() {
            super.onFailed()
            LogUtil.e("onFailed code=" + downloadInfo.errorCode)
        }
    }
    private val map = HashMap<DownloadViewHolder, DownloadInfo>()
    private var downloadAdapter: DownloadAdapter? = null
    private lateinit var downloadInfoList: MutableList<DownloadInfo>
    private var tag: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tag = intent.getStringExtra("tag")
        setContentView(R.layout.activity_download_list)
        setUpToolBaar()



        downloadObserver.enable()
        val linearLayoutManager = LinearLayoutManager(applicationContext)
        //Get all download list.
//        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getAllDownloadList() else CBOZipDownload.getDownloadListByTag(tag)
//        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getDownloadedList() else CBOZipDownload.getDownloadListByTag(tag)
        downloadInfoList = if (TextUtils.isEmpty(tag)) CBOZipDownload.getDownloadingList() else CBOZipDownload.getDownloadingList()

        //Sort download list if need.
        Collections.sort(downloadInfoList) { o1, o2 -> (o1.createTime - o2.createTime).toInt() }
        rvDownloadList.layoutManager = linearLayoutManager
        downloadAdapter = DownloadAdapter(map, downloadInfoList)
        rvDownloadList.adapter = downloadAdapter
    }


    private fun showAlert() {
//        llMsg.setVisibility(View.VISIBLE)
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", ""))
        AppAlert.getInstance().Alert(this, "Download Complete",
                "Visual Ads Downloaded Successfully....") {
            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("FIRST_TIME", "")


            deleateDir()

        }
    }

    private fun showAlertA() {
//        llMsg.setVisibility(View.VISIBLE)
//        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("VISUALAID_VERSION_DOWNLOAD", MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("VISUALAID_VERSION", ""))
        AppAlert.getInstance().Alert(this, "Download Complete",
                "Visual Ads All Ready Downloaded Successfully....") {

            MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("FIRST_TIME", "")
            finish()
        }
    }

    private fun setUpToolBaar() {

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


        pa_name = Custom_Variables_And_Method.PA_NAME
        visual_empname.setText("Welcome  $pa_name")
        visual_compname.setText(Custom_Variables_And_Method.COMPANY_NAME)

        countContainer.visibility = View.VISIBLE
        tvCount.setText("" + CBOZipDownload.getDownloadedList().size)
        tvTotalCount1.setText("" + CBOZipDownload.getAllDownloadList().size)

        if (CBOZipDownload.getAllDownloadList().size == CBOZipDownload.getDownloadedList().size) {
            showAlertA()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
//        downloadObserver.disable()
//        for (downloadInfo in downloadInfoList) {
//            CBOZipDownload.stop(downloadInfo.id)
//        }
//        CBOZipDownload.shutdown()
    }

    class DownloadAdapter(var map: HashMap<DownloadViewHolder, DownloadInfo>, var downloadInfoList: MutableList<DownloadInfo>) : RecyclerView.Adapter<DownloadViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): DownloadViewHolder {
            val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_download_list, viewGroup, false)
            return DownloadViewHolder(v, this)
        }

        override fun onBindViewHolder(viewHolder: DownloadViewHolder, i: Int) {
            val downloadInfo = downloadInfoList[i]
            viewHolder.bindData(downloadInfo, downloadInfo.status)

            downloadInfo.extraData = viewHolder
            map[viewHolder] = downloadInfo
        }

        fun delete(viewHolder: DownloadViewHolder) {
            val position = viewHolder.adapterPosition
            downloadInfoList.removeAt(position)
            notifyItemRemoved(position)
            map.remove(viewHolder)
        }

        override fun getItemCount(): Int {
            return downloadInfoList.size
        }
    }

    class DownloadViewHolder(itemView: View, adapter: DownloadAdapter) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {
        lateinit var downloadInfo: DownloadInfo
        lateinit var status: DownloadInfo.Status
        private var totalSizeString: String? = null
        var totalSize: Long = 0
        var dialog: AlertDialog

        init {
            itemView.bt_status.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            dialog = AlertDialog.Builder(itemView.context)
                    .setTitle("Confirm delete?")
                    .setPositiveButton("Yes") { _, _ ->
                        adapter.delete(this@DownloadViewHolder)
                        CBOZipDownload.deleteById(downloadInfo.id)
                    }
                    .setNegativeButton("No") { _, _ -> }
                    .create()
        }

        fun bindData(downloadInfo: DownloadInfo, status: DownloadInfo.Status) {
            this.downloadInfo = downloadInfo
            this.status = status
            itemView.tv_name.text = downloadInfo.name
            var speed = ""
            val progress = downloadInfo.progress
            itemView.pb_progress.progress = progress
            when (status) {
                DownloadInfo.Status.STOPPED -> {
                    itemView.bt_status.text = "Start"

//                    CBOZipDownload.newRequest(downloadInfo.url, downloadInfo.filePath)
//                            .setId(downloadInfo.id)
//                            .submit()
                    CBOZipDownload.resume(downloadInfo.id)
//                    CBOZipDownload.resume(downloadInfo.id)
                    itemView.bt_status.visibility = View.GONE
                }
                DownloadInfo.Status.PAUSING -> {
//                    itemView.bt_status.visibility = View.VISIBLE
                    itemView.bt_status.text = "Pausing"
                }
                DownloadInfo.Status.PAUSED -> {
//                    itemView.bt_status.visibility = View.VISIBLE
                    itemView.bt_status.text = "Continue"
                }
                DownloadInfo.Status.WAIT -> {
                    itemView.bt_status.visibility = View.VISIBLE
                    itemView.bt_status.text = "Waiting"
                }
                DownloadInfo.Status.RUNNING -> {
//                    itemView.bt_status.text = "Pause"
                    itemView.bt_status.text = ""
                    itemView.bt_status.visibility = View.GONE

                    speed = downloadInfo.speed
                }
                DownloadInfo.Status.FINISHED -> {
                    itemView.bt_status.text = "Done"
//                    itemView.bt_status.visibility = View.VISIBLE
                    (itemView.context as IDownloadList).setCount()

//                    itemView.setBackgroundColor(Color.GREEN)
                }
                DownloadInfo.Status.FAILED -> {
                    itemView.bt_status.text = "Retry"
                    CBOZipDownload.resume(downloadInfo.id)
                    itemView.bt_status.visibility = View.GONE
                }
            }
            itemView.tv_speed.text = speed
            val completedSize = downloadInfo.completedSize
            if (totalSize == 0L) {
                val totalSize = downloadInfo.contentLength
                totalSizeString = "/" + Utils.getDataSize(totalSize)
            }
            itemView.tv_download.text = Utils.getDataSize(completedSize) + totalSizeString!!
        }

        override fun onClick(v: View) {
            if (v === itemView.bt_status) {
                when (status) {
                    DownloadInfo.Status.STOPPED -> {
                        CBOZipDownload.newRequest(downloadInfo.url, downloadInfo.filePath)
                                .setId(downloadInfo.id)
                                .submit()
                    }
                    DownloadInfo.Status.PAUSED -> {
                        CBOZipDownload.resume(downloadInfo.id)
                    }
                    DownloadInfo.Status.WAIT -> {
//                        CBOZipDownload.newRequest(downloadInfo.url, downloadInfo.filePath)
//                                .setId(downloadInfo.id)
//                                .submit()
//                        itemView.bt_status.text = "Pause"

                    }
                    DownloadInfo.Status.RUNNING -> {
                        CBOZipDownload.pause(downloadInfo.id)
                    }
                    DownloadInfo.Status.FINISHED -> ""/*APK.with(itemView.context)*//*
                            .from(downloadInfo.filePath)
                            .install()*/
                    DownloadInfo.Status.FAILED -> {
                        CBOZipDownload.resume(downloadInfo.id)
                        itemView.bt_status.visibility = View.GONE
                    }
                }//do nothing.
            }

        }


        override fun onLongClick(v: View): Boolean {
//            dialog.show()
            return true
        }
    }

    companion object {
        fun start(context: Context, tag: String) {
            val intent = Intent(context, DownloadListActivity::class.java)
            intent.putExtra("tag", tag)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            context.startActivity(intent)
        }
    }


    override fun onBackPressed() {
        AppAlert.getInstance().getAlert(this,
                "Alert !!", "You can't cancel before download?")

    }


    fun deleateDir() {

        var dir = File(Environment.getExternalStorageDirectory(), "cbo/");
        if (dir.isDirectory()) {
            var children = dir.list();
            for (i in 0..children.size - 1) {
                if (children[i].equals("tiwari")) {
                    File(dir, children[i]).deleteRecursively();
                }
            }
        }

        finish()
    }
}

interface IDownloadList {
    fun setCount()
}