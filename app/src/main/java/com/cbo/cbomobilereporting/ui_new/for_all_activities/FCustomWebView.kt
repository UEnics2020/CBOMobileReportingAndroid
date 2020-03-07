package com.cbo.cbomobilereporting.ui_new.for_all_activities

import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.cbo.cbomobilereporting.MyCustumApplication

import com.cbo.cbomobilereporting.R
import com.uenics.javed.CBOLibrary.Response
import kotlinx.android.synthetic.main.fcustom_web_view_fragment.*
import org.json.JSONArray
import org.json.JSONException
import services.CboServices
import utils.adapterutils.SpinnerModel
import utils_new.AppAlert
import utils_new.Custom_Variables_And_Method
import utils_new.Service_Call_From_Multiple_Classes
import java.io.File
import java.net.URL
import java.util.ArrayList

class FCustomWebView : Fragment() {

    private val MESSAGE_INTERNET = 1
    //    private var progressDialog: ProgressDialog? = null
    private var url: String = ""
    internal var count = 0
    internal var previous_url = ""
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fcustom_web_view_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        url = arguments!!.getString("URL");
//        url = "http://www.google.com/"

//        progressDialog = ProgressDialog(activity)
//        progressDialog!!.setCancelable(false)
//        progressDialog!!.setCanceledOnTouchOutside(false)
//        progressDialog!!.setMessage("Loading...")
//        progressDialog!!.show()

        web_progress.visibility = View.VISIBLE
        initWebView(webView)

        webView.setWebViewClient(object : HelloWebViewClient() {
            override fun onPageFinished(view: WebView, url: String) {
                //Calling an init method that tells the website, we're ready
//                webView.loadUrl("javascript:m2Init()")

                if (url.toLowerCase().contains("https://play.google.com/store/apps/details?id=com.cbo.cbomobilereporting")) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cbo.cbomobilereporting&hl=en"))
                    startActivity(i)
                    activity!!.finish()
                } else if (url.toLowerCase().contains("play.google.com/store/apps/")) {
                    val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(i)
                    activity!!.finish()
                } else if (url.toLowerCase().contains(".pdf")) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(Uri.parse(url), "application/pdf")
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(intent)

                    /*  if (Build.VERSION.SDK_INT >= 24) {
                    path = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",path2);
                } else {
                    path = Uri.fromFile(path2);
                }

                // Setting the intent for pdf reader
                Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
                pdfIntent.setDataAndType(path, "application/pdf");
                pdfIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                pdfIntent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);*/

                } else {
                    previous_url = url
                }

//                if (progressDialog != null) {
//                    progressDialog!!.dismiss()
//                }

                if(web_progress!=null){
                    web_progress.visibility = View.GONE
                }

            }

            override fun onReceivedError(view: WebView, request: WebResourceRequest, error: WebResourceError) {

                Custom_Variables_And_Method.getInstance().msgBox(context, "error$error")
            }


        })


        /* webView.setDownloadListener(new DownloadListener() {
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setDataAndType(Uri.parse(url),mimetype);
                startActivity(i);
            }
        });*/

        if (url.toLowerCase().contains("https://play.google.com/store/apps/details?id=com.cbo.cbomobilereporting")) run {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.cbo.cbomobilereporting&hl=en"))
            startActivity(i)
            activity!!.finish()
            if(web_progress!=null){
                web_progress.visibility = View.GONE
            }
//            if (progressDialog != null) {
//                progressDialog!!.dismiss()
//            }
        } else if (url.toLowerCase().contains("play.google.com/store/apps/")) run {
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(i)
            activity!!.finish()
//            if (progressDialog != null) {
//                progressDialog!!.dismiss()
//            }
            if(web_progress!=null){
                web_progress.visibility = View.GONE
            }
        } else {

            if (!url.toLowerCase().contains("http://") && !url.toLowerCase().contains("emulated/0")) {
                url = "http://$url"
            } else if (url.toLowerCase().contains("emulated/0")) {
                url = "file:///$url"
            }

            Custom_Variables_And_Method.GLOBAL_LATLON = Custom_Variables_And_Method.getInstance().getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON)
            Custom_Variables_And_Method.DCR_DATE_TO_SUBMIT = Custom_Variables_And_Method.getInstance().getDataFrom_FMCG_PREFRENCE(context, "DCR_DATE")
            if (!url.contains("emulated/0") && !url.isEmpty()) {
                if (url.contains("?")) {
                    url = url + "&LAT_LONG=" + Custom_Variables_And_Method.GLOBAL_LATLON + "&BRAND=" + MyCustumApplication.getInstance().user.brand
                } else {
                    url = url + "?LAT_LONG=" + Custom_Variables_And_Method.GLOBAL_LATLON + "&BRAND=" + MyCustumApplication.getInstance().user.brand
                }
            }

            //customVariablesAndMethod.getAlert(context,"Url",url);

            val ALLOWED_URI_CHARS = "@#&=*-_.,:!?()/~'%"
            val url1 = Uri.encode(url, ALLOWED_URI_CHARS)
            webView.loadUrl(url1)
        }
    }


    private fun methodInvoke(obj: Any, method: String, parameterTypes: Array<Class<*>>, args: Array<Any>): Any? {
        try {
            val m = obj.javaClass.getMethod(method, *arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!))
            m.invoke(obj, *args)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    private fun initWebView(webView: WebView) {

        val settings = webView.settings

        settings.javaScriptEnabled = true
        settings.allowFileAccess = true
        /*settings.setSupportMultipleWindows(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);*/
        settings.domStorageEnabled = true
//        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        //change latest
        webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
        webView.settings.setAppCacheEnabled(true)
        webView.settings.setAppCachePath(activity!!.getApplicationContext().getFilesDir().getAbsolutePath() + "/cache")

        settings.loadWithOverviewMode = true
        settings.useWideViewPort = true
        settings.setSupportZoom(true)
        settings.builtInZoomControls = true
        // settings.setPluginsEnabled(true);
        methodInvoke(settings, "setPluginsEnabled", arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!), arrayOf<Any>(true))
        // settings.setPluginState(PluginState.ON);
        methodInvoke(settings, "setPluginState", arrayOf<Class<*>>(WebSettings.PluginState::class.java), arrayOf<Any>(WebSettings.PluginState.ON))
        // settings.setPluginsEnabled(true);
        methodInvoke(settings, "setPluginsEnabled", arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!), arrayOf<Any>(true))
        // settings.setAllowUniversalAccessFromFileURLs(true);
        methodInvoke(settings, "setAllowUniversalAccessFromFileURLs", arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!), arrayOf<Any>(true))
        // settings.setAllowFileAccessFromFileURLs(true);
        methodInvoke(settings, "setAllowFileAccessFromFileURLs", arrayOf<Class<*>>(Boolean::class.javaPrimitiveType!!), arrayOf<Any>(true))

        webView.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
//        webView.clearHistory()
//        webView.clearFormData()
//        webView.clearCache(true)

        webView.webChromeClient = MyWebChromeClient()
        // webView.setDownloadListener(downloadListener);
    }

    internal var mUploadHandler: UploadHandler? = null


    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {

        if (requestCode == Controller.FILE_SELECTED) {
            // Chose a file from the file picker.
            if (mUploadHandler != null) {
                mUploadHandler!!.onResult(resultCode, intent)
            }
        }

        super.onActivityResult(requestCode, resultCode, intent)
    }

    internal inner class MyWebChromeClient : WebChromeClient() {


        private fun getTitleFromUrl(url: String): String? {
            val title = url
            try {
                val urlObj = URL(url)
                val host = urlObj.host
                if (host != null && !host.isEmpty()) {
                    return urlObj.protocol + "://" + host
                }
                if (url.startsWith("file:")) {
                    val fileName = urlObj.file
                    if (fileName != null && !fileName.isEmpty()) {
                        return fileName
                    }
                }
            } catch (e: Exception) {
                // ignore
            }

            return title
        }

        override fun onCloseWindow(window: WebView) {
            super.onCloseWindow(window)
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            when (consoleMessage.message().toLowerCase()) {
                "window.close();" -> activity!!.finish()
                "sync" -> Service_Call_From_Multiple_Classes().DownloadAll(context, object : Response {
                    override fun onSuccess(bundle: Bundle) {
                        activity!!.finish()
                    }

                    override fun onError(s: String, s1: String) {
                        AppAlert.getInstance().getAlert(context, s, s1)
                    }
                })
                else -> Log.d("web", consoleMessage.message().toLowerCase())
            }

            return super.onConsoleMessage(consoleMessage)
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            val newTitle = getTitleFromUrl(url)

            AlertDialog.Builder(activity!!).setTitle(newTitle).setMessage(message).setPositiveButton(android.R.string.ok) { dialog, which -> result.confirm() }.setCancelable(false).create().show()
            return true
            // return super.onJsAlert(view, url, message, result);
        }

        override fun onJsConfirm(view: WebView, url: String, message: String, result: JsResult): Boolean {

            val newTitle = getTitleFromUrl(url)

            AlertDialog.Builder(activity!!).setTitle(newTitle).setMessage(message).setPositiveButton(android.R.string.ok) { dialog, which -> result.confirm() }.setNegativeButton(android.R.string.cancel) { dialog, which -> result.cancel() }.setCancelable(false).create().show()
            return true

            // return super.onJsConfirm(view, url, message, result);
        }

        // Android 3.0
        @JvmOverloads
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String = "") {
            openFileChooser(uploadMsg, "", "filesystem")
        }

        // Android 4.1
        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            mUploadHandler = UploadHandler(Controller())
            mUploadHandler!!.openFileChooser(uploadMsg, acceptType, capture)
        }

        // Android 4.4, 4.4.1, 4.4.2
        // openFileChooser function is not called on Android 4.4, 4.4.1, 4.4.2,
        // you may use your own java script interface or other hybrid framework.

        // Android 5.0.1
        override fun onShowFileChooser(
                webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: WebChromeClient.FileChooserParams): Boolean {

            val acceptTypes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                fileChooserParams.acceptTypes
            } else {
                fileChooserParams.acceptTypes
            }

            var acceptType = ""
            for (i in acceptTypes.indices) {
                if (acceptTypes[i] != null && acceptTypes[i].length != 0)
                    acceptType += acceptTypes[i] + ";"
            }
            if (acceptType.length == 0)
                acceptType = "*/*"

            val vc = ValueCallback<Uri> { value ->
                val result: Array<Uri>?
                if (value != null)
                    result = arrayOf(value)
                else
                    result = null

                filePathCallback.onReceiveValue(result)
            }

            openFileChooser(vc, acceptType, "filesystem")


            return true
        }
    }// Android 2.x;

    internal class Controller {

        val activity: AppCompatActivity
            get() = activity

        companion object {
            val FILE_SELECTED = 4
        }

    }

// copied from android-4.4.3_r1/src/com/android/browser/UploadHandler.java
//////////////////////////////////////////////////////////////////////

/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// package com.android.browser;
//
// import android.app.Activity;
// import android.content.ActivityNotFoundException;
// import android.content.Intent;
// import android.net.Uri;
// import android.os.Environment;
// import android.provider.MediaStore;
// import android.webkit.ValueCallback;
// import android.widget.Toast;
//
// import java.io.File;
// import java.util.Vector;
//
// /**
// * Handle the file upload callbacks from WebView here
// */
// public class UploadHandler {

    internal inner class UploadHandler(private val mController: Controller) {
        /*
         * The Object used to inform the WebView of the file to upload.
         */
        private var mUploadMessage: ValueCallback<Uri>? = null
        var filePath: String? = null
            private set
        private var mHandled: Boolean = false
        private var mCaughtActivityNotFoundException: Boolean = false
        fun handled(): Boolean {
            return mHandled
        }

        fun onResult(resultCode: Int, intent: Intent?) {
            if (resultCode == AppCompatActivity.RESULT_CANCELED && mCaughtActivityNotFoundException) {
                // Couldn't resolve an activity, we are going to try again so skip
                // this result.
                mCaughtActivityNotFoundException = false
                return
            }
            var result: Uri? = if (intent == null || resultCode != AppCompatActivity.RESULT_OK)
                null
            else
                intent.data
            // As we ask the camera to save the result of the user taking
            // a picture, the camera application does not return anything other
            // than RESULT_OK. So we need to check whether the file we expected
            // was written to disk in the in the case that we
            // did not get an intent returned but did get a RESULT_OK. If it was,
            // we assume that this result has came back from the camera.
            if (result == null && intent == null && resultCode == AppCompatActivity.RESULT_OK) {
                val cameraFile = File(filePath)
                if (cameraFile.exists()) {
                    result = Uri.fromFile(cameraFile)
                    // Broadcast to the media scanner that we have a new photo
                    // so it will be added into the gallery for the user.
                    mController.activity.sendBroadcast(
                            Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, result))
                }
            }
            mUploadMessage!!.onReceiveValue(result)
            mHandled = true
            mCaughtActivityNotFoundException = false
        }

        fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String, capture: String) {
            val imageMimeType = "image/*"
            val videoMimeType = "video/*"
            val audioMimeType = "audio/*"
            val mediaSourceKey = "capture"
            val mediaSourceValueCamera = "camera"
            val mediaSourceValueFileSystem = "filesystem"
            val mediaSourceValueCamcorder = "camcorder"
            val mediaSourceValueMicrophone = "microphone"
            // According to the spec, media source can be 'filesystem' or 'camera' or 'camcorder'
            // or 'microphone' and the default value should be 'filesystem'.
            var mediaSource = mediaSourceValueFileSystem
            if (mUploadMessage != null) {
                // Already a file picker operation in progress.
                return
            }
            mUploadMessage = uploadMsg
            // Parse the accept type.
            val params = acceptType.split(";".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            val mimeType = params[0]
            if (capture.length > 0) {
                mediaSource = capture
            }
            if (capture == mediaSourceValueFileSystem) {
                // To maintain backwards compatibility with the previous implementation
                // of the media capture API, if the value of the 'capture' attribute is
                // "filesystem", we should examine the accept-type for a MIME type that
                // may specify a different capture value.
                for (p in params) {
                    val keyValue = p.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    if (keyValue.size == 2) {
                        // Process key=value parameters.
                        if (mediaSourceKey == keyValue[0]) {
                            mediaSource = keyValue[1]
                        }
                    }
                }
            }
            //Ensure it is not still set from a previous upload.
            filePath = null
            if (mimeType == imageMimeType) {
                if (mediaSource == mediaSourceValueCamera) {
                    // Specified 'image/*' and requested the camera, so go ahead and launch the
                    // camera directly.
                    startActivity(createCameraIntent())
                    return
                } else {
                    // Specified just 'image/*', capture=filesystem, or an invalid capture parameter.
                    // In all these cases we show a traditional picker filetered on accept type
                    // so launch an intent for both the Camera and image/* OPENABLE.
                    val chooser = createChooserIntent(createCameraIntent())
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(imageMimeType))
                    startActivity(chooser)
                    return
                }
            } else if (mimeType == videoMimeType) {
                if (mediaSource == mediaSourceValueCamcorder) {
                    // Specified 'video/*' and requested the camcorder, so go ahead and launch the
                    // camcorder directly.
                    startActivity(createCamcorderIntent())
                    return
                } else {
                    // Specified just 'video/*', capture=filesystem or an invalid capture parameter.
                    // In all these cases we show an intent for the traditional file picker, filtered
                    // on accept type so launch an intent for both camcorder and video/* OPENABLE.
                    val chooser = createChooserIntent(createCamcorderIntent())
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(videoMimeType))
                    startActivity(chooser)
                    return
                }
            } else if (mimeType == audioMimeType) {
                if (mediaSource == mediaSourceValueMicrophone) {
                    // Specified 'audio/*' and requested microphone, so go ahead and launch the sound
                    // recorder.
                    startActivity(createSoundRecorderIntent())
                    return
                } else {
                    // Specified just 'audio/*',  capture=filesystem of an invalid capture parameter.
                    // In all these cases so go ahead and launch an intent for both the sound
                    // recorder and audio/* OPENABLE.
                    val chooser = createChooserIntent(createSoundRecorderIntent())
                    chooser.putExtra(Intent.EXTRA_INTENT, createOpenableIntent(audioMimeType))
                    startActivity(chooser)
                    return
                }
            }
            // No special handling based on the accept type was necessary, so trigger the default
            // file upload chooser.
            startActivity(createDefaultOpenableIntent())
        }

        private fun startActivity(intent: Intent) {
            try {
                mController.activity.startActivityForResult(intent, Controller.FILE_SELECTED)
            } catch (e: ActivityNotFoundException) {
                // No installed app was able to handle the intent that
                // we sent, so fallback to the default file upload control.
                try {
                    mCaughtActivityNotFoundException = true
                    mController.activity.startActivityForResult(createDefaultOpenableIntent(),
                            Controller.FILE_SELECTED)
                } catch (e2: ActivityNotFoundException) {
                    // Nothing can return us a file, so file upload is effectively disabled.
                    Toast.makeText(mController.activity, R.string.uploads_disabled,
                            Toast.LENGTH_LONG).show()
                }

            }

        }

        private fun createDefaultOpenableIntent(): Intent {
            // Create and return a chooser with the default OPENABLE
            // actions including the camera, camcorder and sound
            // recorder where available.
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = "*/*"
            val chooser = createChooserIntent(createCameraIntent(), createCamcorderIntent(),
                    createSoundRecorderIntent())
            chooser.putExtra(Intent.EXTRA_INTENT, i)
            return chooser
        }

        private fun createChooserIntent(vararg intents: Intent): Intent {
            val chooser = Intent(Intent.ACTION_CHOOSER)
            chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, intents)
            chooser.putExtra(Intent.EXTRA_TITLE,
                    mController.activity.resources
                            .getString(R.string.choose_upload))
            return chooser
        }

        private fun createOpenableIntent(type: String): Intent {
            val i = Intent(Intent.ACTION_GET_CONTENT)
            i.addCategory(Intent.CATEGORY_OPENABLE)
            i.type = type
            return i
        }

        private fun createCameraIntent(): Intent {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val externalDataDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DCIM)
            val cameraDataDir = File(externalDataDir.absolutePath +
                    File.separator + "browser-photos")
            cameraDataDir.mkdirs()
            filePath = cameraDataDir.absolutePath + File.separator +
                    System.currentTimeMillis() + ".jpg"
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(File(filePath)))
            return cameraIntent
        }

        private fun createCamcorderIntent(): Intent {
            return Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        }

        private fun createSoundRecorderIntent(): Intent {
            return Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        }
    }


    
//    fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
//            webView.goBack()
//            return true
//        }
//        return super.onKeyDown(keyCode, event)
//    }


    /*@Override
    public void onBackPressed() {
        if(menu_code.equals("") || count==2) {
            finish();
        }else{
            //Start of call to service

           *//* HashMap<String,String> request=new HashMap<>();
            request.put("sCompanyFolder",cboDbHelper.getCompanyCode());
            request.put("iPaId",""+Custom_Variables_And_Method.PA_ID);
            //request.put("iDcrId", dcr_id);

            ArrayList<Integer> tables=new ArrayList<>();
            tables.add(0);

            progressDialog.setMessage("Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();

            count++;
            new CboServices(this,mHandler).customMethodForAllServices(request,"DCREXPDDLALLROUTE_MOBILE",MESSAGE_INTERNET,tables);
*//*
            //End of call to service
            finish();
        }
    }
*/


    private open inner class HelloWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: WebResourceRequest): Boolean {

            //if (Build.VERSION.SDK_INT >= 24) {
            return super.shouldOverrideUrlLoading(view, url)
            /* } else {
               view.loadUrl(url.toString());
                return true;
            }*/

        }

    }

}
