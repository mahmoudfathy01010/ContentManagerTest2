package com.nousdigital.ngcontentmanager;

import android.support.annotation.NonNull;

import com.nousdigital.ngcontentmanager.FakeNousConductorRepository;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepository;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class Injection {
    private static NousConductorRepository REPO;

    public static NousConductorRepository nousConductorRepository(
            @NonNull String apiUri, @NonNull String contentUri) {
        if (REPO == null) {
            REPO = new FakeNousConductorRepository();
        }
        return REPO;
    }
}
