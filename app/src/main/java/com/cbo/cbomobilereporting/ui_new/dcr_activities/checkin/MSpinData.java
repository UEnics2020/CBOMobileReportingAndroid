package com.cbo.cbomobilereporting.ui_new.dcr_activities.checkin;

public class MSpinData {

    private int ID = 0;
    private String P_NAME = "";
    private String LAT_LNG = "";
    private String PA_LOC = "";
    private String PA_TIME = "";


    public MSpinData(int ID, String p_NAME, String LAT_LNG, String PA_LOC) {
        this.ID = ID;
        P_NAME = p_NAME;
        this.LAT_LNG = LAT_LNG;
        this.PA_LOC = PA_LOC;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getP_NAME() {
        return P_NAME;
    }

    public void setP_NAME(String p_NAME) {
        P_NAME = p_NAME;
    }

    public String getLAT_LNG() {
        return LAT_LNG;
    }

    public void setLAT_LNG(String LAT_LNG) {
        this.LAT_LNG = LAT_LNG;
    }

    public String getPA_LOC() {
        return PA_LOC;
    }

    public void setPA_LOC(String PA_LOC) {
        this.PA_LOC = PA_LOC;
    }

    public String getPA_TIME() {
        return PA_TIME;
    }

    public void setPA_TIME(String PA_TIME) {
        this.PA_TIME = PA_TIME;
    }
}
