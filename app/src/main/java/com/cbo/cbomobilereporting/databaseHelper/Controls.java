package com.cbo.cbomobilereporting.databaseHelper;

import com.cbo.cbomobilereporting.MyCustumApplication;
import com.cbo.cbomobilereporting.ui_new.dcr_activities.Enum.CallType;

public class Controls {

    private static Controls ourInstance;

    public static Controls getInstance() {
        if (ourInstance == null)
        {
            ourInstance = new Controls();
        }
        return ourInstance;
    }

    private Controls() {
    }

    private Boolean GPSRequired;

    private Boolean RouteWise;

    private Boolean giftCampaignWise;
    private Boolean offlineCallAllowed;
    private Boolean sampleGiftAllowedInDcr;

    public Boolean IsGPSRequired() {
        if (GPSRequired == null){
            GPSRequired = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("gps_needed","N").equalsIgnoreCase("Y");
        }
        return GPSRequired;
    }

    public void setGpsRequired(String GPSRequired) {
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("gps_needed",GPSRequired );
        this.GPSRequired = null;
    }

    public Boolean IsRouteWise() {
        if (RouteWise == null){
            RouteWise = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("root_needed","N").equalsIgnoreCase("Y");
        }
        return RouteWise;
    }

    public void setRouteWise(String RouteWise) {
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("root_needed",RouteWise );
        this.RouteWise = null;
    }

    public Boolean IsGiftCampaignWiseReqd() {
        if (giftCampaignWise == null){
            giftCampaignWise = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("GIFTCAMPWISEYN","N").equalsIgnoreCase("Y");
        }
        return giftCampaignWise;
    }

    public void setGiftCampaignWiseReqd(String giftCampaignWise) {
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("GIFTCAMPWISEYN",giftCampaignWise );
        this.giftCampaignWise = null;
    }


    public Boolean IsOfflineCallAllowed() {
        if (offlineCallAllowed == null){
            offlineCallAllowed = MyCustumApplication.getInstance().getDataFrom_FMCG_PREFRENCE("OFFLINE_CALLYN","N").equalsIgnoreCase("Y");
        }
        return offlineCallAllowed;
    }

    public void setOfflineCallAllowed(String offlineCallAllowed) {
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("OFFLINE_CALLYN",offlineCallAllowed );
        this.offlineCallAllowed = null;
    }

    public Boolean IsSampleGiftAllowedInDcrCall(CallType callType) {

//        if (isSampleGiftAllowedInDcr == null) {
        sampleGiftAllowedInDcr = MyCustumApplication.getInstance()
                .getDataFrom_FMCG_PREFRENCE("DCRCALLGIFT_SAMPLEYN", "")
                .contains(callType.getValue()) ||
                MyCustumApplication.getInstance()
                        .getDataFrom_FMCG_PREFRENCE("DCRCALLGIFT_SAMPLEYN", "").equals("");
//        }
        return sampleGiftAllowedInDcr;
    }

    public void setSampleGiftAllowedInDcrCall(String sampleGiftAllowedInDcrCall) {
        MyCustumApplication.getInstance().setDataInTo_FMCG_PREFRENCE("DCRCALLGIFT_SAMPLEYN", sampleGiftAllowedInDcrCall);
       //this.sampleGiftAllowedInDcr = null;
    }


}
