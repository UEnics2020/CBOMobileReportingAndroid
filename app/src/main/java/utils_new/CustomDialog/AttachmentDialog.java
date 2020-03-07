package utils_new.CustomDialog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.ui_new.transaction_activities.Doctor_registration_GPS;
import com.cbo.myattachment.aAttachFile;
import com.uenics.javed.CBOLibrary.Response;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import locationpkg.Const;
import utils.adapterutils.SpinAdapter_new;
import utils.adapterutils.SpinnerModel;
import utils_new.AppAlert;
import utils_new.Custom_Variables_And_Method;
import utils_new.Report_Registration;
import utils_new.Service_Call_From_Multiple_Classes;

/**
 * Created by cboios on 10/03/19.
 */

public class AttachmentDialog {
    AppCompatActivity context;
    private String attachments;
    private AlertDialog myalertDialog = null;

    private OnItemClickListener Listener = null;

    public interface OnItemClickListener {
        void ItemSelected(SpinnerModel item);
        void onListRefressed();
    }

    public AttachmentDialog(@NonNull AppCompatActivity context, String attachments) {
        this.context = context;
        this.attachments = attachments;

    }

    public void show() {



        AlertDialog.Builder myDialog = new AlertDialog.Builder(context);
        final RecyclerView listview=new RecyclerView(context);
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(listview);
        myDialog.setView(layout);

        aAttachFile arrayAdapter=new aAttachFile(context,false);
        arrayAdapter.setAttachments(attachments);
        RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        listview.setLayoutManager(mLayoutManager1);
        listview.setItemAnimator(new DefaultItemAnimator());
        listview.setAdapter(arrayAdapter);

        myalertDialog=myDialog.show();

    }


}
