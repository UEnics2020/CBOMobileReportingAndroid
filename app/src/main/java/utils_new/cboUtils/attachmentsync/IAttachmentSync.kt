package utils_new.cboUtils.attachmentsync

interface IAttachmentSync {
    public fun referenceId();
    public fun onUpdateArray(attachmentArray: ArrayList<mAttachmentSync>);
}