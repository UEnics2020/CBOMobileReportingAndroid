package bill.NewOrder;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.cbo.cbomobilereporting.MyCustumApplication;

import java.util.ArrayList;

import cbomobilereporting.cbo.com.cboorder.DBHelper.DBHelper;
import cbomobilereporting.cbo.com.cboorder.Enum.eTax;
import cbomobilereporting.cbo.com.cboorder.Model.mTax;

public class BillItemDB extends DBHelper {
    public String getTableQuery() {
//        //ITEM_ID":"1","ITEM_NAME":"Ab-Lol","SGST_PERCENT":"0","CGST_PERCENT":"0"
//        return "create table " + this.getTable() + " (main_id integer primary key," +
//                " ITEM_ID text,ITEM_NAME text,SGST_PERCENT text,CGST_PERCENT text,GST_TYPE text,STOCK text)";

//priyeshnew
        //ITEM_ID":"1","ITEM_NAME":"Ab-Lol","SGST_PERCENT":"0","CGST_PERCENT":"0"
        return "create table " + this.getTable() + " (main_id integer primary key," +
                " ITEM_ID text,ITEM_NAME,BRAND text,CATEGORY text,SUB_CATEGORY text,SGST_PERCENT text,CGST_PERCENT text,GST_TYPE text,STOCK text)";

    }

    public BillItemDB(Context c) {
        super(c);
    }

    public String getTable() {
        return "BILL_ITEM";
    }

    public int getTableVersion() {
        /*return 2;*/
        //priyesh
        return 3;
    }

    public void onTableCreate(SQLiteDatabase db) {
        db.execSQL(this.getTableQuery());
    }

    public void onTableUpdate(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
                db.execSQL("ALTER TABLE " + this.getTable() + " ADD COLUMN STOCK text DEFAULT '0'");
            case 2:
                db.execSQL("ALTER TABLE " + this.getTable() + " ADD COLUMN BRAND text DEFAULT ''");
                db.execSQL("ALTER TABLE " + this.getTable() + " ADD COLUMN CATEGORY text DEFAULT ''");
                db.execSQL("ALTER TABLE " + this.getTable() + " ADD COLUMN SUB_CATEGORY text DEFAULT ''");
        }
    }

    public void insert(mBillItem item) {

        ContentValues contentValues = new ContentValues();
        contentValues.put("ITEM_ID", item.getId());
        contentValues.put("ITEM_NAME", item.getName());
        contentValues.put("GST_TYPE", item.getGST().getType().name());
        contentValues.put("SGST_PERCENT", item.getGST().getSGST());
        contentValues.put("CGST_PERCENT", item.getGST().getCGST());
        contentValues.put("STOCK", item.getStock());

        getDatabase().insert(this.getTable(), (String) null, contentValues);
    }

    public void insert(ArrayList<mBillItem> list) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (mBillItem item : list) {
                contentValues.put("ITEM_ID", item.getId());
                contentValues.put("ITEM_NAME", item.getName());
                contentValues.put("GST_TYPE", item.getGST().getType().name());
                contentValues.put("SGST_PERCENT", item.getGST().getSGST());
                contentValues.put("CGST_PERCENT", item.getGST().getCGST());
                contentValues.put("STOCK", item.getStock());
                db.insert(this.getTable(), (String) null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    //priyesh
    public void insertNew(ArrayList<mBillItem> list) {
        SQLiteDatabase db = getDatabase();
        db.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (mBillItem item : list) {
                contentValues.put("ITEM_ID", item.getId());
                contentValues.put("ITEM_NAME", item.getName());
                contentValues.put("BRAND", item.getBRAND());
                contentValues.put("CATEGORY", item.getCATEGORY());
                contentValues.put("SUB_CATEGORY", item.getSUB_CATEGORY());
                contentValues.put("GST_TYPE", item.getGST().getType().name());
                contentValues.put("SGST_PERCENT", item.getGST().getSGST());
                contentValues.put("CGST_PERCENT", item.getGST().getCGST());
                contentValues.put("STOCK", item.getStock());
                db.insert(this.getTable(), (String) null, contentValues);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<mBillItem> items(String filter_text) {
        ArrayList<mBillItem> items = new ArrayList();
        String Qry = "select * from " + this.getTable();
        if (!filter_text.equals("")) {
            Qry = Qry + " where ITEM_NAME LIKE '%" + filter_text + "%'";
        }

        Cursor res = this.getDatabase().rawQuery(Qry, (String[]) null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            mBillItem item = new mBillItem().setId(res.getString(res.getColumnIndex("ITEM_ID")))
                    .setName(res.getString(res.getColumnIndex("ITEM_NAME")))
                    .setStock(res.getDouble(res.getColumnIndex("STOCK")));

            mTax GST = new mTax(eTax.valueOf(res.getString(res.getColumnIndex("GST_TYPE"))));
            GST.setSGST(res.getDouble(res.getColumnIndex("SGST_PERCENT")))
                    .setCGST(res.getDouble(res.getColumnIndex("CGST_PERCENT")));

            item.setGST(GST);
            items.add(item);
            res.moveToNext();
        }

        return items;
    }

    public ArrayList<mBillItem> itemsNew(String filter_text) {
        ArrayList<mBillItem> items = new ArrayList();

        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("KEY_FILTER",filter_text);

        String Qry = "select * from " + this.getTable() + " where ITEM_NAME !=''";

        if (!filter_text.equals("")) {
//            Qry = Qry + " where ITEM_NAME LIKE '%" + filter_text + "%'";
//            Qry = Qry + " where ITEM_NAME||BRAND||CATEGORY||SUB_CATEGORY LIKE '%" + filter_text + "%'";

            String[] parts = filter_text.split(" ");
            for (int i = 0; i < parts.length; i++) {
                Qry = Qry + " and ITEM_NAME||BRAND||CATEGORY||SUB_CATEGORY LIKE '%" + parts[i] + "%'";
            }

        }


        Cursor res = this.getDatabase().rawQuery(Qry, null);
        res.moveToFirst();
        while (!res.isAfterLast()) {
            mBillItem item = new mBillItem().setId(res.getString(res.getColumnIndex("ITEM_ID")))
                    .setName(res.getString(res.getColumnIndex("ITEM_NAME")))
                    .setBRAND(res.getString(res.getColumnIndex("BRAND")))
                    .setCATEGORY(res.getString(res.getColumnIndex("CATEGORY")))
                    .setSUB_CATEGORY(res.getString(res.getColumnIndex("SUB_CATEGORY")))
                    .setStock(res.getDouble(res.getColumnIndex("STOCK")));

            mTax GST = new mTax(eTax.valueOf(res.getString(res.getColumnIndex("GST_TYPE"))));
            GST.setSGST(res.getDouble(res.getColumnIndex("SGST_PERCENT")))
                    .setCGST(res.getDouble(res.getColumnIndex("CGST_PERCENT")));

            item.setGST(GST);
            items.add(item);
            res.moveToNext();
        }

        return items;
    }


}

