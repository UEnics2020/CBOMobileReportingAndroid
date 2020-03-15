package bill.Cart;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.R;

import java.text.ParseException;
import java.util.ArrayList;

import bill.NewOrder.mBillBatch;
import cbomobilereporting.cbo.com.cboorder.Utils.AddToCartView;
import utils_new.CustomDatePicker;
import utils_new.interfaces.RecycleViewOnItemClickListener;


public class aBillBatch extends RecyclerView.Adapter<aBillBatch.MyViewHolder>{
    public ArrayList<mBillBatch> data;

    public aBillBatch(Context activitySpinner, int textViewResourceId, ArrayList objects) {
        data = objects;
    }

    RecycleViewOnItemClickListener listener = null;


    public void setListener(RecycleViewOnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.bill_row, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        mBillBatch tempValues = data.get(position);
        holder.batch_no.setText(tempValues.getBATCH_NO());
        holder.pack.setText("( " + tempValues.getPACK() +" )");

        try {
            holder.exp.setText(CustomDatePicker.formatDate(CustomDatePicker.getDate(tempValues.getEXP_DATE(),CustomDatePicker.ShowFormatOld),"MMM-yy") );
        } catch (ParseException e) {
            holder.exp.setText(tempValues.getEXP_DATE());
            e.printStackTrace();
        }
        holder.mrp.setText(AddToCartView.toCurrency(String.format("%.2f", tempValues.getMRP_RATE())));
        holder.rate.setText(AddToCartView.toCurrency( String.format("%.2f", tempValues.getSALE_RATE())));
        holder.stock.setText(String.format("%.0f", tempValues.getSTOCK()));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView batch_no,pack,exp,rate,mrp,stock;

        public MyViewHolder(@NonNull View row) {
            super(row);

            batch_no =  row.findViewById(R.id.batch_no);
            pack = row.findViewById(R.id.pack);
            exp = row.findViewById(R.id.exp);
            rate =  row.findViewById(R.id.rate);
            stock =  row.findViewById(R.id.stock);
            mrp =  row.findViewById(R.id.mrp);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onClick(v,getAdapterPosition(),false);
                }
            });
        }
    }

}
