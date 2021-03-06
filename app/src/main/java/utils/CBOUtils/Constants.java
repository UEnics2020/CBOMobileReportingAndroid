package utils.CBOUtils;

import android.content.Context;
import android.content.SharedPreferences;

import utils.AppConstant;
import utils.MyConnection;
import utils_new.Custom_Variables_And_Method;

/**
 * Created by kuldeep.dwivedi on 11/29/2014.
 */
public class Constants {

    public  static String HIDE_STATUS;



    public static String getSIDE_STATUS(Context context){
        SharedPreferences pref=context.getSharedPreferences(Custom_Variables_And_Method.FMCG_PREFRENCE, context.MODE_PRIVATE);
        HIDE_STATUS=pref.getString("fmcg_value", "N");
        return HIDE_STATUS;
    }

    public interface ACTION {
        public static String MAIN_ACTION = "com.cbo.foregroundservice.action.main";
        public static String STARTFOREGROUND_ACTION = "com.cbo.foregroundservice.action.startforeground";
        public static String LIVE_TRACKING_ACTION = "com.cbo.foregroundservice.action.startforegroundtracking";
        public static String STOPFOREGROUND_ACTION = "com.cbo.foregroundservice.action.stopforeground";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 101;
    }
}
