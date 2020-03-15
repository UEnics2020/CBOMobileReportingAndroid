package bill.openingStock;

import android.content.Context;

import java.util.ArrayList;

import bill.BillReport.FBillFilter;
import bill.BillReport.mBill;
import bill.BillReport.mCompany;
import bill.Cart.mCustomer;
import bill.mBillOrder;

public interface IOpening {
    void getReferencesById();
    void setActvityTitle(String title);
    String getActivityTitle();
    void addNewStock(FBillFilter fBillFilter);
    void onBillListlistchange(ArrayList<mOpening> billlist);
    void updateTotBillAmt(Double totamt);
    void onBillDeleted(Context context);
    void setCompanyName(String CompanyName);
    String getCompanyName();
    void setBillTitle(String titile);
    void setmCompany(mCompany company);
    void showBillDetail(mBillOrder order, mCustomer customer);
}
