package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkout

import androidx.appcompat.app.AppCompatActivity
import saleOrder.ViewModel.CBOViewModel

class VmCheckOut : CBOViewModel<ICheckOut>() {

    override fun onUpdateView(context: AppCompatActivity?, view: ICheckOut?) {


        if (view != null) {
            view.getReferencesById()
            view.getActivityTitle()
            view.setTile(view.getActivityTitle())

        }
    }
}