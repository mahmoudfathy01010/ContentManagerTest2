package com.nousdigital.ngcontentmanager.data.repositories;

import android.content.Context;
import androidx.annotation.NonNull;

import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl.DownloadDataMessage;
import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public interface NousConductorRepository {
    Flowable<DownloadStatusEvent> downloadData(
            @NonNull DownloadDataMessage updateDataMessage,
            boolean force,
            Context context);

    Flowable<DownloadStatusEvent> downloadDataFromSyncServer(
            DownloadDataMessage downloadDataMessages,
            Context context);

    void deleteAllFiles(Boolean keepSyncServerLogFiles);

    void clearDisposablesAndDownloadStatus();

    int getBuildNumber();

    int getBuildNumberFromDb();

    Single<Integer> calculateFileSize(String language);

}
