package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.CallSuper;
import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import lombok.Getter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Getter
public abstract class NGBaseModel {

    private boolean referencesLoaded = false;

    /**
     * Load data from db
     */
    @WorkerThread @CallSuper public void loadReferences() {
        //overwrite if necessary
        //call async
        referencesLoaded = true;
    }

    /**
     * Load language specific data from db
     *
     * @param lang
     */
    @WorkerThread @CallSuper public void loadReferences(@NonNull String lang) {
        //overwrite if necessary
        //call async
        loadReferences();
    }
}
