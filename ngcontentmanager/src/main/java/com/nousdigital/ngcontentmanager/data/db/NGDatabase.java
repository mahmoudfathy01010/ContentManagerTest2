package com.nousdigital.ngcontentmanager.data.db;

import android.content.Context;
import androidx.annotation.NonNull;

import com.raizlabs.android.dbflow.annotation.Database;

import java.io.File;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Database(name = NGDatabase.NAME, version = NGDatabase.VERSION)
public class NGDatabase {

    public static final String NAME = "NGDatabase";

    public static final int VERSION = 1;

    public static final String DATABASE_EXTENSION = ".db";

    public static boolean exists(@NonNull Context context) {
        return context.getDatabasePath(NAME + DATABASE_EXTENSION).exists();
    }

    public static void delete(@NonNull Context context) {
        File dbFullPath = context.getDatabasePath(NAME + DATABASE_EXTENSION);
        if (dbFullPath.exists()) {
            File file = new File(dbFullPath.getParent());
            File[] files = file.listFiles();
            for (File f : files) {
                f.delete();
            }
        }
    }
}
