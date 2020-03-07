package utils_new.cboUtils.attachmentsyncdr

import utils_new.cboUtils.attachmentsync.mAttachmentSync

interface IAttachmentSyncDr {
    public fun referenceId();
    public fun onUpdateArray(attachmentArray: ArrayList<mAttachmentSyncDr>);
    public fun syncDataToServer(attachmentArray: ArrayList<mAttachmentSyncDr>);
    public fun setCount(count: Int, totalCount: Int)
}