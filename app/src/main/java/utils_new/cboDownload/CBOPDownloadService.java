package utils_new.cboDownload;

import okhttp3.ResponseBody;
import retrofit2.http.POST;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

public interface CBOPDownloadService {


    @Streaming
    @POST
    Observable<ResponseBody> download(@Url String url);
}
