package utils_new.cboUtils.attachmentsync

import androidx.appcompat.app.AppCompatActivity
import saleOrder.ViewModel.CBOViewModel

class vmAttachment : CBOViewModel<IAttachmentSync>() {

    private var mAttachmentSync = ArrayList<mAttachmentSync>()

    override fun onUpdateView(context: AppCompatActivity?, view: IAttachmentSync?) {
        if (view != null) {
            view.referenceId()
            view.onUpdateArray(getArrayData())
        }

    }

    fun setArrayData(mAttachmentSync: ArrayList<mAttachmentSync>) {
        this.mAttachmentSync = mAttachmentSync
    }

    fun getArrayData(): ArrayList<mAttachmentSync> {
        return mAttachmentSync
    }




}