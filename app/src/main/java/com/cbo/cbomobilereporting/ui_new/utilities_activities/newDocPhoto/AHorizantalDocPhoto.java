package com.cbo.cbomobilereporting.ui_new.utilities_activities.newDocPhoto;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.R;

import java.util.ArrayList;
import java.util.List;

import utils.adapterutils.DocSampleModel;

public class AHorizantalDocPhoto extends RecyclerView.Adapter<AHorizantalDocPhoto.MyViewHolder> {

    private Context mContext;
    private List<DocSampleModel> list;
    ArrayList<String> checkId;
    RecyclerViewOnClickListner recycleViewOnItemClickListener = null;
    private int flagCheck = 1000;

    public AHorizantalDocPhoto(Context mContext, List<DocSampleModel> list, ArrayList<String> checkId) {
        this.mContext = mContext;
        this.list = list;
        this.checkId = checkId;
    }


    public void update(List<DocSampleModel> list, int position, ArrayList<String> checkId) {
        this.list = list;
        this.flagCheck = position;
        this.checkId = checkId;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_row_doclist_tab, parent, false);

        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        holder.onBindData(position);

    }

    public void setOnClickListner(RecyclerViewOnClickListner recycleViewOnItemClickListener) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener;
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView text;
        private final TextView id;
        private final TextView rowid;
        private final LinearLayout llTab;

        public MyViewHolder(View view) {
            super(view);

            text = (TextView) view.findViewById(R.id.dcr_workwith_name);
            id = (TextView) view.findViewById(R.id.dcr_workwith_id);
            rowid = (TextView) view.findViewById(R.id.list_row_id);
            llTab = (LinearLayout) view.findViewById(R.id.llTab);


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recycleViewOnItemClickListener != null) {
                        recycleViewOnItemClickListener.onClick(view, getAdapterPosition());
                    }
                }
            });
        }

        public void onBindData(int position) {

            if (checkId.size() > position) {
                text.setTextColor(mContext.getResources().getColor(R.color.white));
                llTab.setBackgroundColor(mContext.getResources().getColor(R.color.logo_green));
            }
            if (flagCheck == position) {
                llTab.setBackgroundColor(mContext.getResources().getColor(R.color.white));
                text.setTextColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
            } else {
                if (checkId.size() > position) {
                    text.setTextColor(mContext.getResources().getColor(R.color.white));
                    llTab.setBackgroundColor(mContext.getResources().getColor(R.color.logo_green));
                }else {
                    llTab.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimaryDark));
                    text.setTextColor(mContext.getResources().getColor(R.color.white));

                }

            }



            id.setText(list.get(position).getId());
            rowid.setText(list.get(position).getRowId());
            text.setText(list.get(position).getName());

        }
    }


}

