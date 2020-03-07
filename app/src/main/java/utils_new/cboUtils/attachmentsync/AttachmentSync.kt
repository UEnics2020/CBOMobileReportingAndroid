package utils_new.cboUtils.attachmentsync

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import bill.BillReport.vmBill
import com.cbo.cbomobilereporting.R
import com.cbo.cbomobilereporting.ui_new.CustomActivity
import kotlinx.android.synthetic.main.activity_attachment_sync.*
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.widget.LinearLayout


class AttachmentSync : CustomActivity(), IAttachmentSync {


    private var vAttachment: vmAttachment? = null
    private var recyclerView: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment_sync)
        context = this
        vAttachment = ViewModelProviders.of(this).get(vmAttachment::class.java)
        vAttachment!!.setArrayData(intent.getSerializableExtra("dataS") as ArrayList<mAttachmentSync>)

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
        title.setText("Uploading Signature to Server")
    }

    override fun onUpdateArray(attachmentArray: ArrayList<mAttachmentSync>) {
        recyclerView!!.layoutManager = LinearLayoutManager(this)
        recyclerView!!.adapter = aAttachmentS(context, vAttachment!!.getArrayData())
    }


}
