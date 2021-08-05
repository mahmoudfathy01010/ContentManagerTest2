package com.nousdigital.ngcontentmanager.utils;


import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import com.nousdigital.ngcontentmanager.NGContentManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */

public final class SDCardUtils {

    private SDCardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static File getSDCardProjectFilePath() {
        checkNotNull(NGContentManager.instance().getContext(),
                "You must initiated NGContentManager with context before using this method");
        return NGContentManager.instance().getContext().getExternalFilesDir(null);
    }

    /**
     * Return whether sdcard is enabled.
     *
     * @return true : enabled<br>false : disabled
     */
    public static boolean isSDCardEnable() {
        checkNotNull(NGContentManager.instance().getContext(),
                "You must initiated NGContentManager with context before using this method");
        return !getSDCardPaths(NGContentManager.instance().getContext()).isEmpty();
    }

    /**
     * Return the paths of sdcard.
     *
     * @return the paths of sdcard
     */
    public static List<String> getSDCardPaths(Context context) {
        StorageManager storageManager = (StorageManager) context
                .getSystemService(Context.STORAGE_SERVICE);
        List<String> paths = new ArrayList<>();
        try {
            Method getVolumePathsMethod = StorageManager.class.getMethod("getVolumePaths");
            getVolumePathsMethod.setAccessible(true);
            Object invoke = getVolumePathsMethod.invoke(storageManager);
            paths = Arrays.asList((String[]) invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public static long freeSpace() {
        return Environment.getExternalStorageDirectory().getUsableSpace();
    }

    public static String readFileAsString(File file) {
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
            }
            br.close();
        } catch (IOException e) {
            Timber.e(e);
        }
        return text.toString();
    }

    public static String readFileAsString(String path) {
        File file = new File(SDCardUtils.getSDCardProjectFilePath().getPath()
                + path);
        return readFileAsString(file);
    }
}
