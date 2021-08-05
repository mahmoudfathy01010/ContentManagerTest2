package com.nousdigital.ngcontentmanager.utils;

import com.squareup.moshi.JsonDataException;
import com.squareup.moshi.Moshi;

import java.io.IOException;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class MoshiUtils {
    public static <T> T deserialize(String serialized, Class c) {
        final Moshi moshi = new Moshi.Builder().build();
        try {
            return (T) moshi.adapter(c).fromJson(serialized);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JsonDataException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String toJsonString(Object obj, Class c) {
        final Moshi moshi = new Moshi.Builder().build();
        return moshi.adapter(c).toJson(obj);
    }
}
