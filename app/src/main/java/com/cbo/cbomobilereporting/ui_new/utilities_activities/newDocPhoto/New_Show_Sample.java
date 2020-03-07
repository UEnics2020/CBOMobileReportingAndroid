package com.cbo.cbomobilereporting.ui_new.utilities_activities.newDocPhoto;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.media.Image;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ZoomControls;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.R;
import com.cbo.cbomobilereporting.databaseHelper.CBO_DB_Helper;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.Doctor_Sample;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.DrCall;
import com.flurry.android.FlurryAgent;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import utils.adapterutils.DocSampleModel;
import utils.adapterutils.ViewPagerAdapter;
import utils_new.AppAlert;
import utils_new.Custom_Variables_And_Method;

public class New_Show_Sample extends AppCompatActivity implements ViewPager.OnPageChangeListener {
    private static final String TAG = "Touch";
    ZoomControls zoom;
    private static final float MIN_ZOOM = 1f, MAX_ZOOM = 1f;
    int image_index = 0;
    CBO_DB_Helper cbohelp;
    ImageButton cancel;
    String item_name = "";
    String pic_id = "";
    ArrayList<String> show_id = new ArrayList<String>();
    ArrayList<String> show_name = new ArrayList<String>();
    ArrayList<String> checkId = new ArrayList<String>();
    private int countS = 0;
    protected View view;
    private ViewPager intro_images;
    private LinearLayout pager_indicator;
    private int dotsCount;
    private ImageView[] dots;
    TextView hader_text;
    androidx.appcompat.widget.Toolbar toolbar;
    ArrayList<String> selected_group_item_ids = new ArrayList<String>();
    ArrayList<String> selected_group_item_names = new ArrayList<String>();
    HashMap<String, ArrayList<String>> data = new HashMap<>();
    private ArrayList<String> imageArray = new ArrayList<>();

    String Dr_Id = "";
    private TextView tvCancel;
    private TextView tvSave;
    private ArrayList<DocSampleModel> list = new ArrayList<>();
    private List<DocSampleModel> listCopy = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private AHorizantalDocPhoto hAdapter;
    private ArrayList<String> group_item_ids = new ArrayList<>();
    private ArrayList<String> group_item_names = new ArrayList<>();
    private ArrayList<String> sample_id_top = new ArrayList<>();
    private ArrayList<String> sample_name_top = new ArrayList<>();
    private int indexId = 0;
    private int tempIndex = 0;
    private ImageView ivPlay;
    private ImageView ivWeb;
    private File video_path;
    private File webPath;

    public void onCreate(Bundle b) {
        super.onCreate(b);
        setContentView(R.layout.activity_new__show__sample);
        getIds();
        initilizeData();
        setImageIntroduction();
        setOnClickListner();
        setButtomAdapter();

    }


    private void getIds() {
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar_hadder);
        hader_text = (TextView) findViewById(R.id.hadder_text_1);
        tvCancel = (TextView) findViewById(R.id.tvCancel);
        tvSave = (TextView) findViewById(R.id.tvSave);
        ivPlay = (ImageView) findViewById(R.id.ivPlay);
        ivWeb = (ImageView) findViewById(R.id.ivWeb);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        cancel = findViewById(R.id.cancel);
        setSupportActionBar(toolbar);
        selected_group_item_ids.clear();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowHomeEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back_hadder_2016);
        }
    }

    private void initilizeData() {

        pic_id = getIntent().getExtras().getString("myid");
//        show_id = getIntent().getExtras().getStringArrayList("samid");
        show_name = getIntent().getExtras().getStringArrayList("sam_name");
        checkId = getIntent().getExtras().getStringArrayList("checkId");
        Dr_Id = getIntent().getStringExtra("dr_id");
        zoom = new ZoomControls(New_Show_Sample.this);


        if (MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("utility", "").equalsIgnoreCase("Y")) {
            tvSave.setVisibility(View.GONE);
        }
        cbohelp = new CBO_DB_Helper(getApplicationContext());
        list = ((ArrayList<DocSampleModel>) getIntent().getExtras().getSerializable("listData"));

        for (int i = 0; i < checkId.size(); i++) {
            for (int j = 0; j < list.size(); j++) {
                if (checkId.get(i).equalsIgnoreCase("" + list.get(j).id)) {

                    DocSampleModel docSampleModel = new DocSampleModel(list.get(j).name, list.get(j).id, list.get(j).rowid, false);
                    docSampleModel.set_Checked(true);
                    listCopy.add(i, docSampleModel);
                    show_name.add(list.get(j).name);
                    show_id.add(list.get(j).id);
                }
            }
        }
        for (int i = 0; i < list.size(); i++) {
            int count = 0;

            for (int j = 0; j < listCopy.size(); j++) {
                if (listCopy.get(j).id.equalsIgnoreCase("" + list.get(i).id)) {
                    count++;
                }
            }
            if (count == 0) {
                DocSampleModel d = new DocSampleModel(list.get(i).name, list.get(i).id, list.get(i).rowid, false);
                show_name.add(list.get(i).name);
                show_id.add(list.get(i).id);
                listCopy.add(d);
            }
        }

        try {
            Cursor c1 = cbohelp.getAllVisualAddByBrand("0", "Y");
            if (c1.moveToNext()) {

                Cursor c = cbohelp.getAllVisualAddByBrand("0", "Y");
                while (c.moveToNext()) {
                    sample_id_top.add("" + c.getString(c.getColumnIndex("BRAND_ID")));
                    sample_name_top.add("" + c.getString(c.getColumnIndex("BRAND_NAME")));
                }
            }
        } catch (Exception e) {
        }
        item_name = listCopy.get(0).getName();
    }

    public void setImageIntroduction() {

        if (sample_id_top.size() == 0) {
            setImage();
        } else {

            for (int i = 0; i < sample_id_top.size(); i++) {
                prepareImageArrayForGroup(sample_id_top.get(i), 0);
            }
            image_index = 0;
            if (imageArray.get(0).equals("no_image")) {
                imageArray.add("no_image");
                setReferenceNew();
            } else {
                setReferenceNew();
            }
            hader_text.setText(listCopy.get(0).getName());
            item_name = group_item_names.get(0);
            webPath = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + group_item_ids.get(0) + "/index.html");
            video_path = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + group_item_ids.get(0) + ".mp4");

            if (webPath.exists()) {
                ivWeb.setVisibility(View.VISIBLE);
            } else {
                ivWeb.setVisibility(View.GONE);
            }


            if (video_path.exists()) {
                ivPlay.setVisibility(View.VISIBLE);
            } else {
                ivPlay.setVisibility(View.GONE);
            }
        }

    }

    private void setOnClickListner() {

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selected_group_item_ids.size() > 0 && countS > 0) {
                    data.put("ITEM_NAME", selected_group_item_names);
                    data.put("ITEM_ID", selected_group_item_ids);
//                    cbohelp.deletedata(Dr_Id, "");
                    for (int i = 0; i < selected_group_item_ids.size(); i++) {
                        cbohelp.insertdata(Dr_Id, selected_group_item_ids.get(i),
                                selected_group_item_names.get(i), "0", "0", "0",
                                "1", "0");
                        //                        cbohelp.insertVisuals(Dr_Id, selected_group_item_ids.get(i), selected_group_item_names.get(i), "0", "0", "0", "1");
                    }
                    MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("SAVE", "SAVEDATA");
                    sendResponse();
                } else {
                    AppAlert.getInstance().Alert(New_Show_Sample.this, "Alert !!", "Please Select Atleast One Visual Ads", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            finish();
                        }
                    });
                }
            }
        });


        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AppAlert.getInstance().setPositiveTxt("Yes").setNagativeTxt("No").DecisionAlert(New_Show_Sample.this, "Alert !!", "Do you want to cancel?", new AppAlert.OnClickListener() {
                    @Override
                    public void onPositiveClicked(View item, String result) {
                        startActivity(new Intent(New_Show_Sample.this, NewDocPhoto.class));
                        finish();
                    }

                    @Override
                    public void onNegativeClicked(View item, String result) {

                    }
                });
            }
        });

        ivPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MyCustumApplication.getInstance().LoadURL(item_name, video_path.toString());
            }
        });

        ivWeb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyCustumApplication.getInstance().LoadURL(item_name, webPath.toString());
            }
        });


    }

    private void setButtomAdapter() {
        hader_text.setText(listCopy.get(0).getName());
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        hAdapter = new AHorizantalDocPhoto(this, listCopy, checkId);
        mRecyclerView.setAdapter(hAdapter);
        hAdapter.setOnClickListner(new RecyclerViewOnClickListner() {
            @Override
            public void onClick(View view, int adapterPosition) {
                ivPlay.setVisibility(View.GONE);
                ivWeb.setVisibility(View.GONE);
                pic_id = list.get(adapterPosition).getId();
                countS++;
                indexId = adapterPosition;
                item_name = listCopy.get(adapterPosition).getName();
                hader_text.setText(listCopy.get(adapterPosition).getName());
                setImage();
                hAdapter.update(listCopy, adapterPosition, checkId);


            }
        });
    }


    private void sendResponse() {
        Intent i = new Intent();
        i.putExtra("data", data);
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("DATA", "" + data);
        setResult(Activity.RESULT_OK, i);
        finish();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        AppAlert.getInstance().setNagativeTxt("No").setPositiveTxt("Yes").DecisionAlert(New_Show_Sample.this, "Alert !!", "Do you want to cancel?", new AppAlert.OnClickListener() {
            @Override
            public void onPositiveClicked(View item, String result) {
                startActivity(new Intent(New_Show_Sample.this, NewDocPhoto.class));
                finish();
            }

            @Override
            public void onNegativeClicked(View item, String result) {

            }
        });

    }

    public void bind_imageArray(String id, String name, String path) {
        image_index += 1;
        File path1 = new File(Environment.getExternalStorageDirectory(), path + "_" + image_index + ".jpg");
        if (path1.exists()) {
            imageArray.add(path1.toString());
            group_item_ids.add(id);
            group_item_names.add(name);
            bind_imageArray(id, name, path);
        } else {
            image_index = 0;
        }
    }


    private void setUiPageViewController(ViewPagerAdapter mAdapter1) {

        dotsCount = mAdapter1.getCount();
        dots = new ImageView[dotsCount];
        pager_indicator.removeAllViews();
        for (int i = 0; i < dotsCount; i++) {
            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(4, 0, 4, 0);

            pager_indicator.addView(dots[i], params);
        }

        dots[0].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        for (int i = 0; i < dotsCount; i++) {
            dots[i].setImageDrawable(getResources().getDrawable(R.drawable.nonselecteditem_dot));
        }

        dots[position].setImageDrawable(getResources().getDrawable(R.drawable.selecteditem_dot));

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    protected void onStart() {
        super.onStart();
        FlurryAgent.onStartSession(this, "M3GXGNKRRC8F9VPNYYY4");
    }

    @Override
    protected void onStop() {
        super.onStop();
        FlurryAgent.onEndSession(this);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    //get All image by group

    public void prepareImageArrayForGroup(String GroupId, int index) {

        if (!show_name.get(index).isEmpty()) {
//            hader_text.setText(show_name.get(index));
            indexId = index;
        }
        imageArray.clear();
        group_item_ids.clear();
        group_item_names.clear();
//
        try {
            Cursor cursor = cbohelp.getAllItemsForGroup(GroupId);

            while (cursor.moveToNext()) {
                String id = "" + cursor.getString(cursor.getColumnIndex("item_id"));
                String name = "" + cursor.getString(cursor.getColumnIndex("item_name"));

                prepareImageArrayNew(id, name);
            }

        } catch (Exception e) {
        }

        if (imageArray.size() == 0) {
            imageArray.add("no_image");
        }

    }

    private void prepareImageArrayNew(String id, String name) {

        File path = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + id + ".jpg");
        File path1 = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + id + "_" + Custom_Variables_And_Method.pub_doctor_spl_code + ".jpg");
        if (path1.exists()) {
            imageArray.add(path1.toString());
            group_item_ids.add(id);
            group_item_names.add(name);
            bind_imageArray(id, name, "cbo/product/" + id + "_" + Custom_Variables_And_Method.pub_doctor_spl_code);
        } else if (path.exists()) {
            imageArray.add(path.toString());

            group_item_ids.add(id);
            group_item_names.add(name);
            bind_imageArray(id, name, "cbo/product/" + id);
        }/* else {
            imageArray.add("no_image");
        }*/

    }


    private void setReferenceNew() {

        intro_images = (ViewPager) findViewById(R.id.pager_introduction);
        pager_indicator = (LinearLayout) findViewById(R.id.viewPagerCountDots);
        ViewPagerAdapter mAdapter1 = new ViewPagerAdapter(this, imageArray);
        intro_images.setAdapter(mAdapter1);
        intro_images.setCurrentItem(0);
        intro_images.setOnPageChangeListener(this);
        setUiPageViewController(mAdapter1);

    }

    private void setImage() {
        prepareImageArrayForGroup(show_id.get(indexId), indexId);
        tempIndex = 0;
        dotsCount = imageArray.size();
        image_index = 0;
        if (imageArray.get(0).equals("no_image")) {
            setReferenceNew();
        } else {
            setSelectedImage(tempIndex);
            setReferenceNew();
        }

    }


    public void setSelectedImage(int imageIndex) {

        String id = group_item_ids.get(imageIndex);
        String name = group_item_names.get(imageIndex);

        if (!selected_group_item_ids.contains(id)) {
            selected_group_item_ids.add(id);
            selected_group_item_names.add(name);
        }


        webPath = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + id + "/index.html");
        video_path = new File(Environment.getExternalStorageDirectory(), "cbo/product/" + id + ".mp4");

        if (webPath.exists()) {
            ivWeb.setVisibility(View.VISIBLE);
        } else {
            ivWeb.setVisibility(View.GONE);
        }


        if (video_path.exists()) {
            ivPlay.setVisibility(View.VISIBLE);
        } else {
            ivPlay.setVisibility(View.GONE);
        }

    }


}
