package utils_new.cboUtils;


import com.cbo.cbomobilereporting.ui_new.transaction_activities.retaileradd.MRetailerAdd;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface UploadAPIs {

    @Multipart
    @POST("DRReg/Upload")
    Call<ResponseBody> uploadImage(@Part MultipartBody.Part file);

}
