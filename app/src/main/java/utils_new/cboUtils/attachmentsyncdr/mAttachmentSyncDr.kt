package utils_new.cboUtils.attachmentsyncdr

import java.io.Serializable

class mAttachmentSyncDr : Serializable {

    var ID: String = ""
    var NAME: String = ""
    var LAT_LONG: String = ""
    var DCS_TYPE: String = ""
    var DCS_ADD: String = ""
    var DCS_INDES: String = ""
    var UPDATED: String = ""
    var file: String = ""
    var FILE_ATTACHMENT: String = ""
    var UPLOAD_STATUS: Boolean = false
    var UPLOAD = 0 //0 not uploaded ,1 uploaded ,2 uploading ,3 uploading Error

}