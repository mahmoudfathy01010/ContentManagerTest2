package com.nousdigital.ngcontentmanager;

import android.content.Context;
import android.support.annotation.NonNull;

import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepository;
import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;

import io.reactivex.Flowable;

import static com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl.DownloadDataMessage;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class FakeNousConductorRepository implements NousConductorRepository {

    @Override public Flowable<DownloadStatusEvent> updateData(
            @NonNull DownloadDataMessage updateDataMessage, boolean force) {
        return null;
    }

    @Override public void deleteAllFiles(@NonNull Context context) {
        //nothing here yet
    }

    @Override public void clearDisposablesAndDownloadStatus() {
        //nothing here yet
    }
}
