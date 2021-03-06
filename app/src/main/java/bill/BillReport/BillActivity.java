package bill.BillReport;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.ui_new.CustomActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import bill.Cart.CompanyCartActivity;
import bill.Cart.mCustomer;
import bill.CompanySelecter.CompanyActivity;
import bill.Outlet.mOutlet;
import bill.mBillOrder;
import bill.openingStock.OpeningStockActivity;
import utils_new.AppAlert;
import utils_new.CustomDatePicker;

public class BillActivity     extends CustomActivity implements
        IBill, aBill.Bill_interface,iBillMain, SearchView.OnQueryTextListener {
private  vmBill vmBill;
private Toolbar toolbar;
private TextView textView,Totamt;
private AppBarLayout appBarLayout;
private RecyclerView  billrecyclerView;
private aBill billadapter;
private FloatingActionButton fab;
private FBillFilter fBillFilter;
private CardView addBill;
private SwipeRefreshLayout swipeRefressLayoutRecycler;
public  CollapsingToolbarLayout collapsingToolbarLayout;
private  Menu menu;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bill_view);
        vmBill = ViewModelProviders.of(BillActivity.this).get(vmBill.class);
        vmBill.setOutlet((mOutlet) getIntent().getSerializableExtra("outlet"));


    }

    @Override
    protected void onStart() {
        super.onStart();
        vmBill.setView(context,this);
    }

    @Override
    public void getReferencesById() {
        context = this;
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        appBarLayout=(AppBarLayout)findViewById(R.id.app_bar);
        textView= (TextView) findViewById(R.id.hadder_text_1);
        billrecyclerView =findViewById(R.id.bill_report_content);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        swipeRefressLayoutRecycler = findViewById(R.id.swipeRefressLayoutRecycler);
        Totamt = findViewById(R.id.Totamt);

        addBill = findViewById(R.id.add_bill);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view ->onBackPressed());

        }

        collapsingToolbarLayout= (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        fBillFilter = (FBillFilter) getSupportFragmentManager().findFragmentById(R.id.billfragment);

        fab .setOnClickListener(view -> getBills());


        addBill.setOnClickListener((view)-> addBill(fBillFilter));


        if (vmBill.getOutlet() == null) {
            AppBarLayout mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar);
            mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
                boolean isShow = false;
                int scrollRange = -1;

                @Override
                public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                    if (scrollRange == -1) {
                        scrollRange = appBarLayout.getTotalScrollRange();
                    }
                    if (scrollRange + verticalOffset == 0) {
                        showOption(R.id.action_filter);
                        // collapsingToolbarLayout.setTitle("Title");
                        isShow = true;
                    } else if (isShow) {

                        hideOption(R.id.action_filter);
                        // collapsingToolbarLayout.setTitle(" ");//carefull there should a space between double quote otherwise it wont work
                        isShow = false;
                    }
                }
            });
        }


        swipeRefressLayoutRecycler.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefressLayoutRecycler.setRefreshing(true);
                getBills();
            }
        });




        //getBills();
    }



    public void getBills(){
        ViewCompat.setNestedScrollingEnabled(
                billrecyclerView, false);
        appBarLayout.setExpanded(false,true);

        if (vmBill.getOutlet() != null){
            hideOption(R.id.action_filter);
            vmBill.getBills(context,vmBill.getOutlet());
        }else{
            vmBill.getBills(context,fBillFilter.getSelectedCompany(),fBillFilter.getFDateSelected(),
                    fBillFilter.getTDateSelected());
        }

    }

    @Override
    public boolean isFromDateRequired() {
        return true;
    }

    @Override
    public boolean isToDateRequired() {
        return true;
    }

    @Override
    public boolean isShowPopup() {
        return false;
    }

    @Override
    public String getDocType() {
        return "Bill";
    }

    @Override
    public boolean IsAllRequiredInFilter() {
        return true;
    }


    @Override
    protected void onResume() {
        super.onResume();
       if (vmBill.isLoaded()) {
           getBills();
       }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.bill_report_menu, menu);
        this.menu = menu;
        /*MenuItem searchItem = menu.findItem(R.id.menu_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));*/
        hideOption(R.id.action_filter);
        hideOption(R.id.add);
        return true;
    }


    private void hideOption(int id) {
        if(menu==null){
            return;
        }
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.action_filter) {

            if (appBarLayout.getTop() < 0)
                appBarLayout.setExpanded(true);
            else
                appBarLayout.setExpanded(false);
            return true;
        } else  if (id == R.id.add) {

            addBill(fBillFilter);
        }
        return super.onOptionsItemSelected(item);
    }
    private void showOption(int id) {
        if(menu==null){
            return;
        }
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }
    @Override
    public void setActvityTitle(String title) {
        textView.setText(title);
    }

    @Override
    public String getActivityTitle() {
        return getIntent().getStringExtra("title");
    }

    @Override
    public void onBillListlistchange(ArrayList<mBill> billlist) {
        billadapter = new aBill(context, billlist);
        billrecyclerView.setLayoutManager(new LinearLayoutManager(context));
        billrecyclerView.setItemAnimator(new DefaultItemAnimator());
        billrecyclerView.setAdapter(billadapter);
        swipeRefressLayoutRecycler.setRefreshing(false);



    }

    @Override
    public void updateTotBillAmt(Double totamt) {
        Totamt.setText(String.format("%.2f", totamt));
    }

    @Override
    public void addBill(FBillFilter billFilter) {
        Intent intent = new Intent(context, CompanyActivity.class);
        intent.putExtra("doc_type", OpeningStockActivity.DOC_TYPE.BILL);
        intent.putExtra("Companies",billFilter.getCompanies());
        intent.putExtra("PayModes",billFilter.getPayModes());
        intent.putExtra("DocDate",billFilter.getDOCDATE());
        intent.putExtra("IS_DOC_DATE_CHANGEBLE",billFilter.getDocDateChangble());
        intent.putExtra("IS_DOC_DATE_Required",true);
        startActivity(intent);
    }

    @Override
    public void onBillDeleted(Context context) {
        fab.performClick();
//        vmBill.getmComponey(context,fBillFilter.getViewModel().getComponeyId(),
//                CustomDatePicker.formatDate(fBillFilter.getViewModel().getFDATE(),CustomDatePicker.CommitFormat),
//                CustomDatePicker.formatDate(fBillFilter.getViewModel().getFDATE(),CustomDatePicker.CommitFormat));
    }

    @Override
    public void setCompanyName(String CompanyName) { }

    @Override
    public String getCompanyName() {
        return null;
    }

    @Override
    public void setBillTitle(String titile) { }

    @Override
    public void setmCompany(mCompany company) {
        getBills();

    }

    @Override
    public void showBillDetail(mBillOrder order, mCustomer customer) {
        Intent intent = new Intent(context, CompanyCartActivity.class);
        intent.putExtra("order", order);
        intent.putExtra("customer", customer);
        intent.putExtra("PayModes",fBillFilter.getPayModes());
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) { return false; }

    @Override
    public boolean onQueryTextChange(String newText) { return false; }





    @Override
    public void Edit_Bill(mBill mbill) {
        vmBill.getBillDet(context,mbill,"E");

    }

    @Override
    public void Delete_Bill(mBill mbill) {
        AppAlert.getInstance().DecisionAlert(context,
                "Delete !!!", "Are you sure to delete\nBill : "+mbill.getBILL_PRINT()+"\nBill Amt. :- "+mbill.getNET_AMT()+" ?",
                new AppAlert.OnClickListener() {
                    @Override
                    public void onPositiveClicked(View item, String result) {

                        vmBill.deleteBill(context,mbill);
                    }

                    @Override
                    public void onNegativeClicked(View item, String result) {

                    }
                });

    }

    @Override
    public void OnClick_Bill(mBill mbill) {
        vmBill.getBillDet(context,mbill,"V");
    }
}
