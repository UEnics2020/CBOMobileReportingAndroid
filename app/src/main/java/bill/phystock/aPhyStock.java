package bill.phystock;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.cbo.cbomobilereporting.R;

import bill.NewOrder.mBillItem;
import bill.mBillOrder;
import bill.openingStock.OpeningStockActivity;
import bill.openingStock.mPage;
import bill.stockEntry.aOpenCart;
import cbomobilereporting.cbo.com.cboorder.Enum.eDeal;
import cbomobilereporting.cbo.com.cboorder.Enum.eTax;
import cbomobilereporting.cbo.com.cboorder.Utils.AddToCartView;
import utils_new.AppAlert;
import utils_new.interfaces.RecycleViewOnItemClickListener;

public class aPhyStock extends RecyclerView.Adapter<aPhyStock.ProductViewHolder> {

    RecycleViewOnItemClickListener recycleViewOnItemClickListener = null;
    private Context mContext;
    private mBillOrder order;
    private Boolean keypressed = true;
    private mPage page;

    public mPage getPage() {
        return page;
    }

    public void setPage(mPage page) {
        this.page = page;
    }

    public aPhyStock(Context mContext, mBillOrder order) {
        this.mContext = mContext;
        this.order = order;

    }


    public void update(mBillOrder order) {
        this.order = order;
        notifyDataSetChanged();
    }

    @Override
    public ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phy_stock_card_new, parent, false);
        return new ProductViewHolder(itemView);

       /* itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_phy_stock_card, parent, false);
        return new CartItemViewHolder(itemView);*/

    }

    @Override
    public void onBindViewHolder(final ProductViewHolder holder, int position) {
        mBillItem item = order.getItems().get(position);

        holder.brand.setText(item.getName());
        holder.batch.setText(item.getBATCH_NO());
        holder.pack.setText("( "+item.getPACK() + " )");

        holder.missmatch.setText(String.format("%.2f",item.getStock()- item.getQty()));
        holder.avl_stock.setText(String.format("%.2f",item.getStock()));
        holder.phyStock.setText(String.format("%.2f", item.getQty()));


    }


 /*   @Override
    public void onBindViewHolder(final CartItemViewHolder holder, int position) {
        mBillItem item = order.getItems().get(position);

        holder.brand.setText(item.getName());
        holder.net_amount.setText(String.format("%.2f", item.getNetAmt()));
        holder.brand_rate.setText(String.format("%.2f", item.getSALE_RATE()));
        holder.batch.setText(item.getBATCH_NO());
        holder.missmatch.setText(String.format("%.0f",item.getStock()- item.getQty()));
        holder.avl_stock.setText(String.format("%.2f",item.getStock()));
//        holder.brand_pack.setText(item.getPACK());
        holder.amount.setText(String.format("%.2f", item.getAmt()));
        holder.phyStock.setText(String.format("%.0f", item.getQty()));
        holder.MRPDet.setText(String.format("%.2f", item.getMRP_RATE()));

        holder.FreeQty.setText(String.format("%.0f", item.getFreeQty()));
        holder.QtyDet.setText(String.format("%.0f", item.getQty()));





        if (item.getDeal().getType() != eDeal.NA) {
            holder.rate.setText(String.format("%.0f", item.getQty()) +
                    " X " + AddToCartView.toCurrency(String.format("%.2f", item.getSALE_RATE())) +
                    " + " + item.getFreeQty());
        } else {
            holder.rate.setText(String.format("%.0f", item.getQty()) +
                    " X " + AddToCartView.toCurrency(String.format("%.2f", item.getSALE_RATE())));
        }
        //holder.discountName.setText("Discount " + item.getDiscountStr());
        holder.discount.setText(String.format("%.2f", (item.getAmt() - item.getNetAmt())));

        if (item.getGST().getSGST() == 0) {
            holder.LocalTax.setVisibility(View.GONE);
            holder.centralTaxName.setText(eTax.IGST.name() + " @" + item.getGST().getCGST() + "%");
        } else {
            holder.LocalTax.setVisibility(View.VISIBLE);
            holder.centralTaxName.setText(eTax.CGST.name() + " @" + item.getGST().getCGST() + "%");
        }

        holder.LocalTaxName.setText(eTax.SGST.name() + " @ " + item.getGST().getSGST() + "%");

        holder.CGST_amt.setText(String.format("%.2f", (item.getCGSTAmt())));
        holder.SGST_amt.setText(String.format("%.2f", (item.getSGSTAmt())));
        holder.brand_tot_amt.setText(String.format("%.2f", (item.getTotAmt())));

       *//* holder.remark.setText(item.getRemark());
        holder.remarkTitle.setText(MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("SALE_ORDER_REMARK_TITLE","Remark"));
        holder.remarkLayout.setVisibility(item.getRemarkReqd()? View.VISIBLE: View.GONE);*//*


    }*/


    public void setOnClickListner(RecycleViewOnItemClickListener recycleViewOnItemClickListener) {
        this.recycleViewOnItemClickListener = recycleViewOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return order.getItems().size();
    }

    public class CartItemViewHolder extends RecyclerView.ViewHolder {

        private final LinearLayout FreeQtyLayout;
        private TextView brand, avl_stock, batch, missmatch, rate, amount, net_amount, brand_rate, brand_tot_amt, SGST_amt, CGST_amt;
        private TextView phyStock, discountName, discount, centralTaxName, LocalTaxName, remark, remarkTitle, expand, MRPDet;
        private TextView QtyDet, FreeQty;
        private ImageView delete, edit;
        private LinearLayout extraLayout, centralTax, LocalTax, remarkLayout;

        public CartItemViewHolder(View view) {
            super(view);
            FreeQtyLayout = (LinearLayout) view.findViewById(R.id.FreeQtyLayout);
            expand = (TextView) view.findViewById(R.id.expand);
            brand = (TextView) view.findViewById(R.id.brand);
            net_amount = (TextView) view.findViewById(R.id.brand_net_amt);
            brand_rate = view.findViewById(R.id.brand_rate);
            missmatch = (TextView) view.findViewById(R.id.missmatch);
            batch = (TextView) view.findViewById(R.id.batch);
//            brand_pack = (TextView) view.findViewById(R.id.brand_pack);
            avl_stock = (TextView) view.findViewById(R.id.avl_stock);
            phyStock = (TextView) view.findViewById(R.id.phy_stock);
            rate = view.findViewById(R.id.rate);
            amount = view.findViewById(R.id.amount);
            discountName = view.findViewById(R.id.discountName);
            discount = view.findViewById(R.id.discount);
            MRPDet = view.findViewById(R.id.MRPDet);

            QtyDet = view.findViewById(R.id.QtyDet);
            FreeQty = view.findViewById(R.id.FreeQty);

            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);

            if (getPage().getCode().equalsIgnoreCase(OpeningStockActivity.DOC_TYPE.OPENING.name())){
                FreeQtyLayout.setVisibility(View.INVISIBLE);
            }

            centralTax = view.findViewById(R.id.centralTax);
            LocalTax = view.findViewById(R.id.LocalTax);
            centralTaxName = view.findViewById(R.id.centralTaxName);
            LocalTaxName = view.findViewById(R.id.LocalTaxName);
            SGST_amt = view.findViewById(R.id.SGST_amt);
            CGST_amt = view.findViewById(R.id.CGST_amt);

            brand_tot_amt = view.findViewById(R.id.brand_tot_amt);

            extraLayout = view.findViewById(R.id.extraLayout);

            remark = view.findViewById(R.id.remark);
            remarkTitle = view.findViewById(R.id.remarkTitle);
            remarkLayout = view.findViewById(R.id.remarkLayout);


            if (order.getDocId().equalsIgnoreCase("0") || order.getStatus().equalsIgnoreCase("E")) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mItem item = order.getItems().get(getAdapterPosition());
                        AppAlert.getInstance().DecisionAlert(mContext,
                                "Delete !!!", "Are you sure to delete " + order.getItems().get(getAdapterPosition()).getName() + " ?",
                                new AppAlert.OnClickListener() {
                                    @Override
                                    public void onPositiveClicked(View item, String result) {
                                        order.getItems().remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        if (recycleViewOnItemClickListener != null)
                                            recycleViewOnItemClickListener.onClick(v, getAdapterPosition(), false);
                                    }

                                    @Override
                                    public void onNegativeClicked(View item, String result) {

                                    }
                                });
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recycleViewOnItemClickListener != null)
                            recycleViewOnItemClickListener.onClick(v, getAdapterPosition(), false);
                    }
                });


            } else {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            }

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (extraLayout.getVisibility() == View.VISIBLE) {
                        //more.setRotation(0);
                        expand.setText("+");
                        extraLayout.setVisibility(View.GONE);
                    } else {
                        //more.setRotation(180);
                        expand.setText("-");
                        extraLayout.setVisibility(View.VISIBLE);
                    }
                }
            });

        }
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {
        private TextView brand, pack, batch, avl_stock, phyStock,missmatch;
        private ImageView delete, edit;

        public ProductViewHolder(View view) {
            super(view);
            brand = (TextView) view.findViewById(R.id.brand);
            batch = (TextView) view.findViewById(R.id.brand_batch);
            pack = (TextView) view.findViewById(R.id.brand_pack);
            missmatch = (TextView) view.findViewById(R.id.missmatch);

            avl_stock = (TextView) view.findViewById(R.id.avl_stock);
            phyStock = (TextView) view.findViewById(R.id.phy_stock);

            edit = view.findViewById(R.id.edit);
            delete = view.findViewById(R.id.delete);




            if (order.getDocId().equalsIgnoreCase("0") || order.getStatus().equalsIgnoreCase("E")) {
                delete.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //mItem item = order.getItems().get(getAdapterPosition());
                        AppAlert.getInstance().DecisionAlert(mContext,
                                "Delete !!!", "Are you to sure to delete ?",
                                new AppAlert.OnClickListener() {
                                    @Override
                                    public void onPositiveClicked(View item, String result) {
                                        order.getItems().remove(getAdapterPosition());
                                        notifyItemRemoved(getAdapterPosition());
                                        if (recycleViewOnItemClickListener != null)
                                            recycleViewOnItemClickListener.onClick(v, getAdapterPosition(), false);
                                    }

                                    @Override
                                    public void onNegativeClicked(View item, String result) {

                                    }
                                });
                    }
                });

                edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (recycleViewOnItemClickListener != null)
                            recycleViewOnItemClickListener.onClick(v, getAdapterPosition(), false);
                    }
                });


            } else {
                delete.setVisibility(View.GONE);
                edit.setVisibility(View.GONE);
            }


        }
    }
}
