package com.nousdigital.ngcontentmanager.data.downloader;

import android.os.Environment;
import android.os.StatFs;
import androidx.annotation.NonNull;

import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;
import com.nousdigital.ngcontentmanager.utils.events.EventTypes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import io.reactivex.rxjava3.core.FlowableEmitter;
import timber.log.Timber;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 * <p>
 * In here we download files as a stream and store them to a specified destination.
 */
public class FileWriterService  {
    public static final String ZIP = ".zip";
    private final FlowableEmitter<DownloadStatusEvent> emitter;
    public static final String META_JSON = "meta.json";
    private final String storagePath;
    private final String fileName;
    private boolean interrupted;

    public FileWriterService(  @NonNull String storagePath, FlowableEmitter<DownloadStatusEvent> emitter) {
        if (storagePath.equals(META_JSON)) {
            //there could more meta.json, so we want to give them a unique name before storing
            this.storagePath = storagePath.replace(META_JSON, Math.random() + "_" + META_JSON);
        } else {
            this.storagePath = storagePath;
        }
        this.fileName = new File(storagePath).getName();
        this.emitter = emitter;
    }

    public Boolean download(InputStream... inputStreams) throws IOException {
        EventBus.getDefault().register(this);
        File fileToStore = new File(storagePath);
        if (fileToStore.exists()) {
            fileToStore.delete();
        } else {
            dirChecker(fileToStore.getParentFile());
        }
        Timber.d("Start downloading and storing %s", fileName);

        InputStream inputStream = inputStreams[0];
        if (storagePath.endsWith(ZIP)) {
            return unzip(inputStream, new File(storagePath).getParentFile());
        } else {
            return writeFileToDisk(fileToStore, inputStream);
        }
    }

    @NonNull
    private Boolean writeFileToDisk(File fileToStore, InputStream inputStream) throws IOException {
        OutputStream outputStream = null;

        try {
            Timber.i("Writing file %s to disk", fileToStore);
            byte[] fileReader = new byte[4096];
            outputStream = new FileOutputStream(fileToStore);

            while (!interrupted) {
                int read = inputStream.read(fileReader);

                if (read == -1) {
                    break;
                }
                outputStream.write(fileReader, 0, read);
                if (!emitter.isCancelled()) {
                    emitter.onNext(DownloadStatusEvent.builder()
                            .bytesRead(read)
                            .filePath(fileToStore.getAbsolutePath()).build());
                }
            }
            Timber.i("File %s written to disk", fileToStore);
            removeFileIfInterrupted(fileToStore);
            outputStream.flush();
            outputStream.close();
            System.gc();
        } catch (Exception e) {
            Timber.e(e, "Error download file "+ fileToStore.getPath());
            throw e;
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (outputStream != null) {
                outputStream.close();
            }
            EventBus.getDefault().unregister(this);
            return true;
        }
    }

    public boolean unzip(InputStream inputStream, File zipFile) throws IOException {
        ZipInputStream zin = new ZipInputStream(inputStream);
        long processBytes = 0;
        File file = null;
        try {
            ZipEntry zipEntry;
            while (!interrupted && (zipEntry = zin.getNextEntry()) != null) {
                Timber.v("Unzipping %s", zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    dirChecker(new File(zipFile.getParent(), zipEntry.getName()));
                } else {
                    if (checkIfEnoughDiskSpaceIsAvailable(zipEntry.getCompressedSize())) {
                        file = new File(zipFile, zipEntry.getName());
                        dirChecker(file.getParentFile());
                        FileOutputStream fOut = new FileOutputStream(file);
                        BufferedOutputStream bufOut = new BufferedOutputStream(fOut);
                        byte[] buffer = new byte[1024];
                        int read;
                        while (!interrupted && (read = zin.read(buffer)) != -1) {
                            bufOut.write(buffer, 0, read);
                        }
                        processBytes += zipEntry.getCompressedSize();
                        emitProcessedBytes(zipEntry.getCompressedSize(), file);
                        bufOut.close();
                        zin.closeEntry();
                        fOut.close();
                    } else {
                        Timber.e("Could not complete downloading file "+ zipFile.getPath());
                        return false;
                    }
                }
            }
            removeFileIfInterrupted(zipFile);
            zin.close();
            Timber.d("Unzipping complete. path: " + zipFile.getPath());
        } catch (Exception e) {
            Timber.e(e);
            removeFileIfInterrupted(zipFile);
            Timber.e(e, "Error while downloading file "+ zipFile.getPath());
            emitProcessedBytes(-processBytes, file); //substract already emitted bytes
            throw e;
        }finally {
            zin.close();
            EventBus.getDefault().unregister(this);
        }
        return true;
    }

    private void emitProcessedBytes(long processBytes, File file) {
        if (!emitter.isCancelled()) {
            String filePath = "";
            if(file != null){
                filePath = file.getAbsolutePath();
            }
            emitter.onNext(DownloadStatusEvent.builder()
                    .bytesRead(processBytes)
                    .eventType(EventTypes.DOWNLOAD_PROGRESS)
                    .filePath(filePath).build());
        }
    }

    private boolean checkIfEnoughDiskSpaceIsAvailable(long neededBytes) {
        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = stat.getBlockSizeLong() * stat.getBlockCountLong();
        return bytesAvailable > neededBytes;
    }

    public void removeFileIfInterrupted(File targetDirectory) {
        if (interrupted && targetDirectory.exists()) {
            targetDirectory.delete();
        }
    }

    private void dirChecker(File f) {
        if (!f.isDirectory()) {
            f.mkdirs();
        }
    }

    @Subscribe
    public void interrupt(EventTypes eventTypes) {
        if (eventTypes == EventTypes.INTERRUPT_DOWNLOAD) {
            interrupted = true;
            EventBus.getDefault().unregister(this);
            Timber.w("interrupted write for %s", fileName);
        }
    }
}
