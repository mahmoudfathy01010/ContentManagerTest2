package com.nousdigital.ngcontentmanager.data.api;

import com.nousdigital.ngcontentmanager.BuildConfig;
import com.nousdigital.ngcontentmanager.data.api.dto.*;

import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public interface NGApiService {
    @GET("sync/v2.0/")
    Call<SyncResponseDto> getApiSync(@Query("buildNumber") int buildNumber);

    @Streaming
    @GET(BuildConfig.CDN_URL_PREPEND+"{filePath}")
    Call<ResponseBody> downloadFile(@Path(value = "filePath", encoded = true) String filePath);

}
