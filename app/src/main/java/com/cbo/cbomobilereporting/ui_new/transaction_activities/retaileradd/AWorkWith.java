package com.cbo.cbomobilereporting.ui_new.transaction_activities.retaileradd;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;

import java.util.ArrayList;

import utils.adapterutils.Dcr_Workwith_Model;
import utils_new.Custom_Variables_And_Method;

public class AWorkWith extends RecyclerView.Adapter<AWorkWith.MySingleViewHolder> {

    private final Custom_Variables_And_Method customVariablesAndMethod;
    private int show = 0;
    private ArrayList<MRetailerPopup> arrayList;
    private Context context;
    private String latLong = "";
    IWorkWith recycleViewOnItemClickListener = null;

    public AWorkWith(Context context, ArrayList<MRetailerPopup> arrayList) {
        customVariablesAndMethod = Custom_Variables_And_Method.getInstance();
        this.context = context;
        this.arrayList = arrayList;

        if (MyCustumApplication.getInstance().getUser().getLoggedInAsSupport()) {
            this.show = 0;
        }
    }

    @Override
    public MySingleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.category_selection_row, parent, false);
        return new MySingleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MySingleViewHolder holder, int position) {
        holder.onBindData(position);
    }

    public void setOnClickListner(IWorkWith recycleViewOnItemClickListener) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class MySingleViewHolder extends RecyclerView.ViewHolder {

        TextView spin_name;
        TextView spin_id;

        public MySingleViewHolder(@NonNull View itemView) {
            super(itemView);

            spin_name = itemView.findViewById(R.id.spin_name);
            spin_id = itemView.findViewById(R.id.spin_id);
        }


        public void onBindData(int position) {

            MRetailerPopup tempValues = null;
            tempValues = (MRetailerPopup) arrayList.get(position);
            spin_name.setText(arrayList.get(position).getNAME());
            spin_id.setText("" + arrayList.get(position).getID());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    recycleViewOnItemClickListener.onClickW(view, (MRetailerPopup) arrayList.get(position), position);
                }
            });
        }
    }
}
