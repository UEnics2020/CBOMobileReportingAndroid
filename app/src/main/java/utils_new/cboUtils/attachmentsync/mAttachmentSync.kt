package utils_new.cboUtils.attachmentsync

import java.io.Serializable

class mAttachmentSync : Serializable {

    var ID: String = ""
    var NAME: String = ""
    var FILE_ATTACHMENT: String = ""
    var UPLOAD_STATUS: Boolean = false
    var UPLOAD = 0 //0 not uploaded ,1 uploaded ,2 uploading ,3 uploading Error


}