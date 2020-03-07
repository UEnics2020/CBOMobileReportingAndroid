package utils_new.cboUtils.attachmentsyncdr

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import kotlinx.android.synthetic.main.activity_attachment_sync_dr.*
import utils_new.cboUtils.attachmentsync.IAttachmentSync
import utils_new.cboUtils.attachmentsync.aAttachmentS
import utils_new.cboUtils.attachmentsync.mAttachmentSync
import utils_new.cboUtils.attachmentsync.vmAttachment

class AttachmentSyncDr : CustomActivity(), IAttachmentSyncDr {

    override fun setCount(count: Int, totalCount: Int) {
        tvCount.text = "" + count
        tvTotalCount1.text = "" + totalCount
        if (count == totalCount) {
            tvTotalCount1.setTextColor(resources.getColor(R.color.colorPrimary))
        }


    }


    private var vAttachment: vmAttachmentDr? = null
    private var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment_sync_dr)
        context = this
        vAttachment = ViewModelProviders.of(this).get(vmAttachmentDr::class.java)
        vAttachment!!.setArrayData(intent.getSerializableExtra("dataS") as ArrayList<mAttachmentSyncDr>)

    }

    override fun onStart() {
        super.onStart()
        vAttachment!!.setView(context, this)
    }

    override fun referenceId() {

        recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        val toolbar = findViewById<View>(R.id.toolbar) as androidx.appcompat.widget.Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            //getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
            toolbar.setNavigationOnClickListener { view -> onBackPressed() }
        }
        val title = toolbar.findViewById<TextView>(R.id.title)
        title.setText("Uploading Picture to Server")
    }

    override fun onUpdateArray(attachmentArray: ArrayList<mAttachmentSyncDr>) {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = aAttachmentSDr(context, vAttachment!!.getArrayData())
    }

    override fun syncDataToServer(attachmentArray: ArrayList<mAttachmentSyncDr>) {
        vAttachment?.syncDataToServer(context, attachmentArray);
    }

}
