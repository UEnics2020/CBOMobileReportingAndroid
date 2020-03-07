package com.cbo.cbomobilereporting.ui_new.utilities_activities.newVisualaddDownload

import android.widget.TextView
import utils_new.cboDownload.CircleProgressBar

interface IVisualAddDownload {

    fun referenceId();
    fun onUpdateArray(visualAddArray: ArrayList<MDownLoadVisualAdd>);
    fun syncDataToServer(visualAddArray: ArrayList<MDownLoadVisualAdd>);
    fun setCount(count: Int, totalCount: Int,layoutPosition:Int, tvDataInfo:TextView, custom_progressBar: CircleProgressBar)
}