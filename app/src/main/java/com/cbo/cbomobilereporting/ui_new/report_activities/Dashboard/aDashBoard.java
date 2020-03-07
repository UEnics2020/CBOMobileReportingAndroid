package com.cbo.cbomobilereporting.ui_new.report_activities.Dashboard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.R;

import java.util.ArrayList;


public class aDashBoard extends RecyclerView.Adapter<aDashBoard.MyViewHolder> {


    private Context context;
    private ArrayList<mDatabase> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView tvCap1;
        TextView tvCap2;
        RecyclerView itemList;
        View rowView;
        DashboardInner InnerAdaptor;


        public MyViewHolder(View view) {
            super(view);
            this.rowView = view;
            name = view.findViewById(R.id.name);
            tvCap1 = view.findViewById(R.id.tvCap1);
            tvCap2 = view.findViewById(R.id.tvCap2);
            itemList = view.findViewById(R.id.itemList);

            InnerAdaptor = new DashboardInner(context);
            itemList.setLayoutManager(new LinearLayoutManager(context));
            itemList.setItemAnimator(new DefaultItemAnimator());
            itemList.setAdapter(InnerAdaptor);

            itemList.setOnClickListener(null);

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!list.get(getAdapterPosition()).getDOC_TYPE().isEmpty()) {
//                        Intent intent = new Intent(context, Outlet.class);
//                        intent.putExtra("dashboard", list.get(getAdapterPosition()));
//                        intent.putExtra("iLEVEL_NO", "2");
//                        intent.putExtra("DOC_TYPE1", "");
//                        context.startActivity(intent);
                    }
                }
            });


        }
    }


    public aDashBoard(Context context, ArrayList<mDatabase> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public aDashBoard.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_new_row_view_dash, parent, false);

        return new aDashBoard.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull aDashBoard.MyViewHolder holder, int position) {


        mDatabase dashboard = list.get(position);
        holder.name.setText(dashboard.getGROUP_NAME());
        holder.tvCap1.setText(dashboard.getGROUP_CAPTION1());
        holder.tvCap2.setText(dashboard.getGROUP_CAPTION2());
        ((DashboardInner) holder.itemList.getAdapter()).updateList(dashboard.getCOL_NAME(), dashboard.getCOL_AMOUNT(), dashboard.getCOL_AMOUNT_CUM(), dashboard.getCOL_URL());


        holder.rowView.setBackgroundColor(Color.parseColor(dashboard.getBG_COLOR()));


        holder.rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dashboard.getDOC_TYPE().isEmpty()) {
//                    Intent intent = new Intent(context, Outlet.class);
//                    intent.putExtra("dashboard", dashboard);
//                    intent.putExtra("iLEVEL_NO", "2");
//                    intent.putExtra("DOC_TYPE1", "");
//                    context.startActivity(intent);
                }
            }
        });


    }


    @Override
    public int getItemCount() {
        return list.size();
    }
}
