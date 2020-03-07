package com.cbo.cbomobilereporting.ui_new.report_activities.Dashboard;

import android.content.Context;
import android.graphics.Paint;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;

import java.util.ArrayList;

public class DashboardInner extends RecyclerView.Adapter<DashboardInner.MyViewHolder> {


    private Context context;
    private ArrayList<String> CaptionList = new ArrayList<>();
    private ArrayList<String> AnmountList = new ArrayList<>();
    private ArrayList<String> AmountCumList = new ArrayList<>();
    private ArrayList<String> UrlList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView caption, value, tvcum;
        View rowView;


        public MyViewHolder(View view) {
            super(view);
            this.rowView = view;
            caption = view.findViewById(R.id.caption);
            value = view.findViewById(R.id.value);
            tvcum = view.findViewById(R.id.tvcum);

        }
    }


    public DashboardInner(Context context) {
        this.context = context;
    }

    public void updateList(ArrayList<String> CaptionList, ArrayList<String> AnmountList, ArrayList<String> AmountCumList, ArrayList<String> UrlList) {
        this.AnmountList = AnmountList;
        this.CaptionList = CaptionList;
        this.AmountCumList = AmountCumList;
        this.UrlList = UrlList;
    }


    @NonNull
    @Override
    public DashboardInner.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.dashboard_inner_row_view_dash, parent, false);

        return new DashboardInner.MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(@NonNull DashboardInner.MyViewHolder holder, int position) {
        holder.caption.setText(CaptionList.get(position));
        holder.value.setText(AnmountList.get(position));
        if (AmountCumList.get(position).equals("")) {
            holder.tvcum.setVisibility(View.GONE);
        } else {
            holder.tvcum.setVisibility(View.VISIBLE);
            holder.tvcum.setText("" + AnmountList.get(position));
        }


//        if (UrlList.get(position).isEmpty()) {
//            holder.caption.setText(CaptionList.get(position));
//        } else {
//            holder.caption.setText(Html.fromHtml("<u>" + CaptionList.get(position) + "</u>"));
//        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!UrlList.get(position).isEmpty()) {
//                    MyCustumApplication.getInstance().LoadURL(CaptionList.get(position), UrlList.get(position));
                }


            }
        });


    }


    @Override
    public int getItemCount() {
        return CaptionList.size();
    }
}
