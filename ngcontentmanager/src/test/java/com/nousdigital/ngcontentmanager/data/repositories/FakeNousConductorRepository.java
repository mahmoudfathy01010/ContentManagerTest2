package com.nousdigital.ngcontentmanager.data.repositories;

import android.support.annotation.NonNull;

import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;

import io.reactivex.Flowable;

import static com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl.DownloadDataMessage;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public final class FakeNousConductorRepository implements NousConductorRepository {

    @Override public Flowable<DownloadStatusEvent> updateData(
            @NonNull DownloadDataMessage updateDataMessage, boolean force) {
        return null;
    }

    @Override public void deleteAllFiles() {

    }

    @Override public void clearDisposablesAndDownloadStatus() {

    }
}
