/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2020
 */

/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2020
 */

package com.nousdigital.ngcontentmanager.data.api;

import com.nousdigital.ngcontentmanager.data.api.dto.SyncResponseDto;
import com.nousdigital.ngcontentmanager.data.api.dto.SyncSlotDto;

import io.reactivex.Single;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2019
 */

// These calls here are for the automatic update of the inhouse guides
// against the sync server. An automatic update can also be forced
// from the admin screen.

public interface NGSyncServerService {
    //Check if a new version exists
    //eg: https://cachingtest.nousdigital.net/cms/rest/sync/info?buildNumber=xy mit A
    @GET("cms/")
    Call<SyncResponseDto> getLatestBuildFromSyncserver(@Query(value = "buildNumber", encoded = true) int buildNumber);

    //Get Sync Slot
    @GET("api/sync/slot/")
    Single<SyncSlotDto> getSyncSlot(
            @Query(value = "buildVersion", encoded = true) String buildVersion,
            @Query(value = "deviceId", encoded = true) String deviceId);

    //Call4:
    @GET("api/sync/datetime")
    Single<SyncSlotDto> getUTCSyncServer();
}
