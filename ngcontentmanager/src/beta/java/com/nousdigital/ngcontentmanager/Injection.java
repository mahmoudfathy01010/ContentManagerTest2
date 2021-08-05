package com.nousdigital.ngcontentmanager;

import android.support.annotation.NonNull;

import com.nousdigital.ngcontentmanager.data.db.NGDao;
import com.nousdigital.ngcontentmanager.data.db.NGDaoImpl;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepository;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class Injection {

    private static NousConductorRepository NC_REPO;
    private static NGDao NG_DAO;

    public static NousConductorRepository provideNousConductorRepository(@NonNull String apiUri,
                                                                         @NonNull
                                                                                 String contentUri) {
        if (NC_REPO == null) {
            NC_REPO = new NousConductorRepositoryImpl(apiUri, contentUri);
        }
        return NC_REPO;
    }

    public static NGDao provideDao() {
        if (NG_DAO == null) {
            NG_DAO = new NGDaoImpl();
        }
        return NG_DAO;
    }
}
