package com.nousdigital.ngcontentmanager.data.db.dependencyResolvers;

import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class DependecyResolverFactory {
    public static <T extends BaseModel> T getResolver(T dataModel) {
        return null;
    }
}
