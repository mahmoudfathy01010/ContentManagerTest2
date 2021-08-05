package com.nousdigital.ngcontentmanager.utils;

import android.graphics.Bitmap;

import com.nousdigital.ngcontentmanager.NGContentManager;
import com.nousdigital.ngcontentmanager.Statics;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class FileUtils {

    private static String syncFilesPath;

    public static String getAssetPath(String fileName) {
        checkContextExists();
        return Statics.CONTENT_URL + "content" + fileName;
//        return Statics.CONTENT_URL + "content/ezb" + fileName;
    }

    public static File getInternalFile(String name) {
        return new File(SDCardUtils.getSDCardProjectFilePath().getPath()
                + "/content/internal/" + name);
    }

    public static String getSyncFilePath() {
        if (syncFilesPath == null) {
            syncFilesPath = SDCardUtils.getSDCardProjectFilePath() + "/sync";
        }
        return syncFilesPath;
    }

    private static void checkContextExists() {
        checkNotNull(NGContentManager.instance().getContext(),
                "You must initiated NGContentManager with context before using this method");
    }

    public static void storeBitmapFile(Bitmap image, String fileName) {
        checkNotNull(NGContentManager.instance().getContext(),
                "You must initiated NGContentManager with context before using this method");
        File pictureFile = createOutputMediaFile(fileName);
        if (pictureFile == null) {
            Timber.d("Error creating media file, check storage permissions: ");
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            image.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            Timber.d("File not found: %s", e.getMessage());
        } catch (IOException e) {
            Timber.d(e);
        }
    }

    public static File createOutputMediaFile(String fileName) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(SDCardUtils.getSDCardProjectFilePath().getPath()
                + "/content/internal/");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        // Create a media file name
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return mediaFile;
    }

    public static boolean exists(String fileName) {
        return new File(SDCardUtils.getSDCardProjectFilePath().getPath()
                + "/content/" + fileName).exists();
    }
}
