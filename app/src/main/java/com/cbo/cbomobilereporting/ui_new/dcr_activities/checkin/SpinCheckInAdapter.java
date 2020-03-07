package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.emp_tracking.DistanceCalculator;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.pobmail.fragment.pending.RecyclerStokistListner;

import java.util.ArrayList;

import utils.adapterutils.SpinnerModel;
import utils_new.Custom_Variables_And_Method;

public class SpinCheckInAdapter extends RecyclerView.Adapter<SpinCheckInAdapter.MySingleViewHolder> {

    private final Custom_Variables_And_Method customVariablesAndMethod;
    private int show = 0;
    private ArrayList<MSpinData> arrayList;
    private Context context;
    private String latLong = "";
    RecyclerViewPopupListner recycleViewOnItemClickListener = null;

    public SpinCheckInAdapter(Context context, ArrayList<MSpinData> arrayList) {
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        this.context = context;
        this.arrayList = arrayList;

        if (MyCustumApplication.getInstance().getUser().getLoggedInAsSupport()) {
            this.show = 0;
        }
    }

    @Override
    public MySingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.office_selection_row, parent, false);
        return new MySingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySingleViewHolder holder, int position) {
        holder.onBindData(position);
    }

    public void setOnClickListner(RecyclerViewPopupListner recycleViewOnItemClickListener) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MySingleViewHolder extends RecyclerView.ViewHolder {

        TextView spin_name;
        TextView address;
        TextView spin_id;
        TextView distance;
        LinearLayout distanceLayout;
        ImageView loc_icon;

        public MySingleViewHolder(@NonNull View itemView) {
            super(itemView);

            spin_name = itemView.findViewById(R.id.spin_name);
            address = itemView.findViewById(R.id.address);
            spin_id = itemView.findViewById(R.id.spin_id);
            distance = itemView.findViewById(R.id.distance);
            distanceLayout = itemView.findViewById(R.id.distanceLayout);
            loc_icon = itemView.findViewById(R.id.loc_icon);
        }


        public void onBindData(int position) {

            MSpinData tempValues = null;
            tempValues = (MSpinData) arrayList.get(position);
            latLong = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "shareLatLong", Custom_Variables_And_Method.GLOBAL_LATLON);
//            latLong = "28.621453,77.0366037";
            spin_name.setText(arrayList.get(position).getP_NAME());
            spin_id.setText("" + arrayList.get(position).getID());
            address.setText("" + arrayList.get(position).getPA_LOC());


            loc_icon.setVisibility(View.VISIBLE);

            if (!tempValues.getLAT_LNG().equalsIgnoreCase("")) {

                Double km = 0D;
                km = DistanceCalculator.distance(Double.valueOf(tempValues.getLAT_LNG().split(",")[0]), Double.valueOf(tempValues.getLAT_LNG().split(",")[1])
                        , Double.valueOf(latLong.split(",")[0]), Double.valueOf(latLong.split(",")[1]), "K");

                String geo_fancing_km = customVariablesAndMethod.getDataFrom_FMCG_PREFRENCE(context, "GEO_FANCING_KM", "0");

                if (km > Double.valueOf(geo_fancing_km)) {
                    loc_icon.setVisibility(View.VISIBLE);
                    distance.setText(String.format("%.2f", km) + " Km Away");
                    distance.setBackgroundColor(0xffE2921F);
                } else {
                    distance.setText("Within Range");
                    distance.setBackgroundColor(0xff2C7164);
                }

            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recycleViewOnItemClickListener.onClick(view, (MSpinData) arrayList.get(position), position);
                }
            });
        }
    }


}