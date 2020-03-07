package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin

import com.cbo.cbomobilereporting.ui_new.dcr_activities.pobmail.fragment.pending.ChemistFragment
import com.cbo.cbomobilereporting.ui_new.dcr_activities.pobmail.fragment.pending.mChemist
import java.util.ArrayList

interface ICheckIn {

    fun getReferencesById()
    fun setOnClickListner()
    fun getActivityTitle(): String
    fun setTile(var1: String)
    fun setOffice(office : MSpinData )
    fun setLatLng(str: String)
    fun setTime(TIME: String)

}