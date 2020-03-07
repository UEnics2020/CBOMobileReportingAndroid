package com.cbo.cbomobilereporting.ui_new.report_activities.Dashboard;

import java.io.Serializable;
import java.util.ArrayList;

public class mDatabase implements Serializable {

    private String DOC_TYPE = "";
    private String GROUP_NAME = "";
    private String GROUP_CAPTION1 = "";
    private String GROUP_CAPTION2 = "";
    private ArrayList<String> COL_NAME = new ArrayList<>();
    private ArrayList<String> COL_AMOUNT = new ArrayList<>();
    private ArrayList<String> COL_URL = new ArrayList<>();
    private ArrayList<String> COL_AMOUNT_CUM = new ArrayList<>();
    private String BG_COLOR = "#FFFFFF";

    public String getDOC_TYPE() {
        return DOC_TYPE;
    }

    public void setDOC_TYPE(String DOC_TYPE) {
        this.DOC_TYPE = DOC_TYPE;
    }

    public String getGROUP_NAME() {
        return GROUP_NAME;
    }

    public void setGROUP_NAME(String GROUP_NAME) {
        this.GROUP_NAME = GROUP_NAME;
    }

    public String getGROUP_CAPTION1() {
        return GROUP_CAPTION1;
    }

    public void setGROUP_CAPTION1(String GROUP_CAPTION1) {
        this.GROUP_CAPTION1 = GROUP_CAPTION1;
    }

    public String getGROUP_CAPTION2() {
        return GROUP_CAPTION2;
    }

    public void setGROUP_CAPTION2(String GROUP_CAPTION2) {
        this.GROUP_CAPTION2 = GROUP_CAPTION2;
    }

    public ArrayList<String> getCOL_NAME() {
        return COL_NAME;
    }

    public void setCOL_NAME(ArrayList<String> COL_NAME) {
        this.COL_NAME = COL_NAME;
    }

    public ArrayList<String> getCOL_AMOUNT() {
        return COL_AMOUNT;
    }

    public void setCOL_AMOUNT(ArrayList<String> COL_AMOUNT) {
        this.COL_AMOUNT = COL_AMOUNT;
    }

    public ArrayList<String> getCOL_URL() {
        return COL_URL;
    }

    public void setCOL_URL(ArrayList<String> COL_URL) {
        this.COL_URL = COL_URL;
    }

    public ArrayList<String> getCOL_AMOUNT_CUM() {
        return COL_AMOUNT_CUM;
    }

    public void setCOL_AMOUNT_CUM(ArrayList<String> COL_AMOUNT_CUM) {
        this.COL_AMOUNT_CUM = COL_AMOUNT_CUM;
    }

    public String getBG_COLOR() {
        return BG_COLOR;
    }

    public void setBG_COLOR(String BG_COLOR) {
        this.BG_COLOR = BG_COLOR;
    }


}
