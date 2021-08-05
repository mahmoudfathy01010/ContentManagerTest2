package com.nousdigital.ngcontentmanager.data.repositories;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.google.common.base.Strings;
import com.google.common.io.Files;
import com.nousdigital.ngcontentmanager.BuildConfig;
import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.Statics;
import com.nousdigital.ngcontentmanager.data.api.NGApiService;
import com.nousdigital.ngcontentmanager.data.api.NGSyncServerService;
import com.nousdigital.ngcontentmanager.data.api.dto.*;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.nousdigital.ngcontentmanager.data.downloader.AuthInterceptor;
import com.nousdigital.ngcontentmanager.data.downloader.DownloadService;
import com.nousdigital.ngcontentmanager.exceptions.NGContentManagerException;
import com.nousdigital.ngcontentmanager.utils.MoshiUtils;
import com.nousdigital.ngcontentmanager.utils.SDCardUtils;
import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipFile;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Consumer;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.nousdigital.ngcontentmanager.data.downloader.FileWriterService.META_JSON;
import static com.nousdigital.ngcontentmanager.exceptions.NGContentManagerException.Type.NOT_ENOUGH_SPACE;
import static com.nousdigital.ngcontentmanager.utils.JWTUtils.getJwtTokenForApi;
import static com.nousdigital.ngcontentmanager.utils.JWTUtils.getJwtTokenForSyncServer;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public final class NousConductorRepositoryImpl implements NousConductorRepository {
    private final String contentUri;

    private String syncServerUri;
    private NGSyncServerService syncServerRetrofitService;
    private String jwtSyncServer = "";
    private String jwtApi= "";

    private final NGApiService apiRetrofitService; //Goes against API
    private CompositeDisposable downloadDisposables = new CompositeDisposable();
    private Flowable<DownloadStatusEvent> downloadStatusFlowable;
    private Flowable<DownloadStatusEvent> automaticUpdateStatusFlowable;

    private Context context;

    private int buildNumber = 0;

    public NousConductorRepositoryImpl(@NonNull String apiUri,
                                       @NonNull String contentUri,
                                       String syncServerUri,
                                       Context c) {
        checkNotNull(apiUri);
        checkNotNull(contentUri);
        checkNotNull(c);

        context  = c;

        jwtApi = getJwtTokenForApi();
        Timber.d("Created JWT for api: " + jwtApi);
        this.contentUri = contentUri;
        this.apiRetrofitService = createRetrofitInstance(apiUri, jwtApi, null).create(NGApiService.class);

        //If its an inhouse build, we have a sync server uri and create a service for the sync server
        if (syncServerUri != null) {
            jwtSyncServer = getJwtTokenForSyncServer();
            Timber.d("Created JWT for sync server: " + jwtApi);
            this.syncServerUri = syncServerUri;
            this.syncServerRetrofitService = createRetrofitInstance(syncServerUri, jwtApi, jwtSyncServer).create(NGSyncServerService.class);
        }
    }

    public int getBuildNumber() {
        return this.buildNumber;
    }

    public int getBuildNumberFromDb(){
        if (context != null && NGDatabase.exists(context)) {
            FlowManager.init(context);
            return Injection.provideDao().getVersion();
        }
        else return 0;
    }


    @Override
    public Single<Integer> calculateFileSize(String language){
        return Single.create(emitter -> {
            apiRetrofitService.getApiSync(0).
                    enqueue(new Callback<SyncResponseDto>() {
                        @Override
                        public void onResponse(Call<SyncResponseDto> call, retrofit2.Response<SyncResponseDto> response) {
                            SyncResponseDto dto = response.body();
                            int filesize = calculateFileSize(dto, language);
                            emitter.onSuccess(filesize);
                        }

                        @Override
                        public void onFailure(Call<SyncResponseDto> call, Throwable t) {
                            emitter.onError(t);
                        }
                    });
        });
    }

    private int calculateFileSize(SyncResponseDto dto, String language) {
        int totalSize = 0;
        for (SyncResponseDto.ZipFileEntry file : dto.getZipFiles())
        {
            if (file.getLanguage().equals(language) || file.isLanguageIndependent()){
                totalSize += file.getSize();
            }
        }
        return totalSize;
    }

    public Flowable<DownloadStatusEvent> downloadData(DownloadDataMessage downloadDataMessages,
                                                      boolean force, @NonNull Context context) {
        checkNotNull(context,
                "You must initialize NGContentManager with context before using this method");
        this.context = context;
        if (downloadStatusFlowable == null) {
            downloadStatusFlowable = Flowable.create(emitter -> {
                        if (force) {
                            deleteAllFiles(true);
                        }
                        if (NGDatabase.exists(context)) {
                            FlowManager.init(context);
                            downloadDataMessages.setBuildNumber(Injection.provideDao().getVersion());
                        } else {
                            downloadDataMessages.setBuildNumber(0);
                        }
                        downloadDataMessages.setDownloadStatusEventEmitter(emitter);
                        Timber.d(downloadDataMessages.getBuildNumber()+" Build Number");
                        this.buildNumber = downloadDataMessages.getBuildNumber();
                        makeContentSyncCall(downloadDataMessages);

                    },
                    BackpressureStrategy.LATEST);
        }
        return downloadStatusFlowable;
    }

    /*get latest data from the SYNCSERVER */
    @Override
    public Flowable<DownloadStatusEvent> downloadDataFromSyncServer(
            DownloadDataMessage downloadDataMessages, @NonNull Context context){

        checkNotNull(context,
                "You must initialize NGContentManager with context before using this method");
        this.context = context;
        if (automaticUpdateStatusFlowable == null){
            automaticUpdateStatusFlowable = Flowable.create(emitter -> {
                        if (NGDatabase.exists(context)) {
                            FlowManager.init(context);
                            downloadDataMessages.setBuildNumber(Injection.provideDao().getVersion());
                        } else {
                            downloadDataMessages.setBuildNumber(0);
                        }
                        downloadDataMessages.setDownloadStatusEventEmitter(emitter);
                        this.buildNumber = downloadDataMessages.getBuildNumber();
                        Timber.d("Starting automatic update routine to update to build number %s",
                                downloadDataMessages.getBuildNumber());
                        makeSyncServerSyncCall(downloadDataMessages);
                    },
                    BackpressureStrategy.LATEST);

        }
        return automaticUpdateStatusFlowable;
    }

    /**
     * Delete all files from the ext. directory + the database
     */
    @Override
    public void deleteAllFiles(Boolean keepSyncServerLogFiles) {
        NGDatabase.delete(context);
        FlowManager.close();
        File parentDir = context.getExternalFilesDir(null);

        File[] subDirs = new File[0];
        if(keepSyncServerLogFiles) {
            IOFileFilter f = FileFilterUtils.notFileFilter(
                    FileFilterUtils.nameFileFilter("logging"));

            subDirs = parentDir.listFiles((FileFilter) f);
        } else {
            subDirs = parentDir.listFiles();
        }

        Stream.of(subDirs).forEach(d -> {
            try {
                FileUtils.deleteDirectory(d);
            } catch (IOException e) {
                Timber.e(e);
            }
        });
        Timber.d("Finished deleting data");
    }


    public void makeContentSyncCall(DownloadDataMessage downloadDataMessages) {
        apiRetrofitService
                .getApiSync(downloadDataMessages.buildNumber)
                .enqueue(new Callback<SyncResponseDto>() {
            @Override
            public void onResponse(Call<SyncResponseDto> call, retrofit2.Response<SyncResponseDto> response) {
                onSyncUpdateResponse(response, downloadDataMessages);
            }

            @Override
            public void onFailure(Call<SyncResponseDto> call, Throwable t) {
                resolveSyncResponseError(t, downloadDataMessages.downloadStatusEventEmitter);
            }
        });
    }

    public void makeSyncServerSyncCall(DownloadDataMessage downloadDataMessages) {
        syncServerRetrofitService
                .getLatestBuildFromSyncserver(downloadDataMessages.buildNumber)
                .enqueue(new Callback<SyncResponseDto>() {
            @Override
            public void onResponse(Call<SyncResponseDto> call, Response<SyncResponseDto> response) {
                logResponse(response, "SYNCSERVER");
                onSyncUpdateResponse(response, downloadDataMessages);
            }

            @Override
            public void onFailure(Call<SyncResponseDto> call, Throwable t) {
                resolveSyncResponseError(t, downloadDataMessages.downloadStatusEventEmitter);
            }
        });
    }

    private void resolveSyncResponseError(Throwable t, final FlowableEmitter<DownloadStatusEvent> emitter) {
        Timber.e(t);
        if (!emitter.isCancelled()) {
            emitter.onError(new NGContentManagerException(t, !NGDatabase.exists(context)));
        }
    }

    private void onSyncUpdateResponse(Response<SyncResponseDto> response,
                                      DownloadDataMessage downloadDataMessages) {
        switch (response.code()) {
            case 200:
                SyncResponseDto dto = response.body();
                if (dto.getBuildNumber() != downloadDataMessages.getBuildNumber()) {
                    downloadAndStoreNewSyncContent(response.body(), downloadDataMessages);
                    break;
                }
            case 204:
                //nothing has changed
                Timber.d("No data/content changes, terminate sync");
                FlowManager.init(context);
                downloadDataMessages.downloadStatusEventEmitter.onError(
                        new NGContentManagerException(NGContentManagerException.Type.NO_UPDATE));
                break;
            case 202:
                //server is busy with building new content
                Timber.w("Server is busy building content - skip update");
                FlowManager.init(context);
                downloadDataMessages.downloadStatusEventEmitter.onError(
                        new NGContentManagerException(NGContentManagerException.Type.CMS_BUSY,
                        !NGDatabase.exists(context)));
                break;
            case 403:
                Timber.w("Unauthorized/Forbidden");
                FlowManager.init(context);
                downloadDataMessages.downloadStatusEventEmitter.onError(
                        new NGContentManagerException(NGContentManagerException.Type.UNAUTHORIZED,
                                !NGDatabase.exists(context)));
                break;
            case 500:
                Timber.w("Internal Server Error");
                FlowManager.init(context);
                downloadDataMessages.downloadStatusEventEmitter.onError(
                        new NGContentManagerException(NGContentManagerException.Type.INTERNAL_SERVER_ERROR,
                                !NGDatabase.exists(context)));
                break;
            default:
                //TODO: Decide how to handle this case in app...Throw exception here?
                // Often related to no access due to rights/tokens/...
                FlowManager.init(context);
                downloadDataMessages.downloadStatusEventEmitter.onComplete();
        }
    }

    private void logResponse(Response<SyncResponseDto> response,
                                      String tag) {
        switch (response.code()) {
            case 200:
                Timber.tag(tag).i("Response 200: success, start downloading data");
                break;
            case 204:
                //nothing has changed
                Timber.tag(tag).i("Response 204: No data/content changes, terminate sync");
                break;
            case 202:
                Timber.tag(tag).i("Response 202: Server is busy building content - skip update");
                break;
            case 403:
                Timber.tag(tag).i("Response 403: Unauthorized/Forbidden, cannot update");
                break;
            default:
                Timber.tag(tag).i("Response"+ response.code() +  ": Unhandled response");
                break;
        }
    }

    private void downloadAndStoreNewSyncContent(final SyncResponseDto dto,
                                                DownloadDataMessage downloadDataMessages) {

        //list of all existing files, to determine later if there is any files to delete from disk
        List<String> existingFilePaths = getListOfExistingFilesIfNotDeltaUpdate(dto);

        List<FileToDownload> listOfFilesToDownload = new ArrayList<>();

        // if we also want to download zip and other files
        if (!downloadDataMessages.downloadOnlyDb) {
            //if inhouse guide: load media files in ALL languages
            if (BuildConfig.FLAVOR == "inhouse")
            listOfFilesToDownload.addAll(createListOfFilesToDownload(
                    dto,
                    SDCardUtils.getSDCardProjectFilePath().getPath(),
                    false));
            else {
                //load media files only in current language
                listOfFilesToDownload.addAll(createListOfFilesToDownload(
                        dto,
                        SDCardUtils.getSDCardProjectFilePath().getPath(),
                        true));
            }
        }

        //last thing we always want to download is the sqlite db file
        listOfFilesToDownload.add(FileToDownload.builder()
                .downloadPath(dto.getSqliteFile())
                .storagePath(Statics.DB_PATH).build());

        final long totalByteSize = calculateTotalDownloadFileSize(dto, listOfFilesToDownload);
        if (isEnoughSpaceOnSDCard(totalByteSize)) {
            List<Flowable<DownloadStatusEvent>> downloadFlowableList = new ArrayList<>();
            //add download Flowables for each file to list
            Stream.of(listOfFilesToDownload).forEach(fileToDownload -> {
                if (!Strings.isNullOrEmpty(fileToDownload.downloadPath)) {
                    DownloadService downloadService = new DownloadService(contentUri);
                    downloadFlowableList.add(downloadService.getDownloadFlowable(fileToDownload)
                            .doOnNext(getDownloadStatusEventConsumer(downloadDataMessages,
                                    existingFilePaths, totalByteSize))
                    );
                }
            });
            subscribeToDownloadFlowable(downloadDataMessages, downloadFlowableList, existingFilePaths);
        } else {
            downloadDataMessages.downloadStatusEventEmitter.onError(
                    new NGContentManagerException(NOT_ENOUGH_SPACE,
                    !NGDatabase.exists(context)));
        }
    }

    private boolean isEnoughSpaceOnSDCard(long requiredBytes) {
        return requiredBytes < SDCardUtils.freeSpace();
    }

    @NonNull
    private Consumer<DownloadStatusEvent> getDownloadStatusEventConsumer(DownloadDataMessage updateDataMessages,
                                                                         List<String> existingFilePaths,
                                                                         long totalByteSize) {
        return downloadStatus -> {
            downloadStatus.setTotalBytes(totalByteSize);
            updateDataMessages.downloadStatusEventEmitter.onNext(downloadStatus);
            if (existingFilePaths.size() > 0
                    && existingFilePaths.contains(downloadStatus.getFilePath())) {
                existingFilePaths.remove(downloadStatus.getFilePath());
            }
            checkIfUnzippedFileIsMetaJsonAndDelete(downloadStatus);
        };
    }

    private List<FileToDownload> createListOfFilesToDownload(
            SyncResponseDto dto,
            String externalDirPath,
            boolean onlyDownloadFilesInCurrentLanguage) {
        return Stream.of(dto.getZipFiles())
                .filter(z -> onlyDownloadFilesInCurrentLanguage ?
                        z.isLanguageIndependent() || Statics.CURRENT_LANGUAGE.equals(z.getLanguage())
                        : true)
                .map(z -> FileToDownload.builder()
                        .downloadPath(z.getHref())
                        .storagePath(externalDirPath + z.getHref())
                        .fileSize(z.getSize())
                        .build())
                .toList();
    }

    private List<String> getListOfExistingFilesIfNotDeltaUpdate(SyncResponseDto dto) {
        if (!dto.isDeltaUpdate()) {
            File parentDir = SDCardUtils.getSDCardProjectFilePath();
            return Stream
                    .of(Files.fileTraverser()
                    .breadthFirst(parentDir))
                    .filter(f -> f.isFile() && !f.getName().contains(Statics.SYNCSERVER_LOGFILE_NAME))
                    .map(f -> f.getPath())
                    .toList();
        }
        return Collections.emptyList();
    }

    private void checkIfUnzippedFileIsMetaJsonAndDelete(DownloadStatusEvent downloadStatus) {
        if (!Strings.isNullOrEmpty(downloadStatus.getFilePath()) &&
                downloadStatus.getFilePath().endsWith(META_JSON)) {
            removeFilesOnMetaListFromDisk(downloadStatus.getFilePath());
        }
    }

    private void removeFilesOnMetaListFromDisk(String jsonName) {
        MetaDto dto = readMetaJson(jsonName);
        String sdCardPath = SDCardUtils.getSDCardProjectFilePath().getPath();
        if (dto != null && dto.getDeleted() != null) {
            Stream.of(dto.getDeleted()).forEach(pathToDelete -> {
                String path = sdCardPath + pathToDelete;
                File deleteFile = new File(path);
                if (deleteFile.exists()) {
                    deleteFile.delete();
                }
            });
        }
    }

    private @Nullable
    MetaDto readMetaJson(String jsonName) {
        MetaDto dto = null;
        try {
            File metaJson = new File(jsonName);
            FileInputStream stream = new FileInputStream(metaJson);
            try {
                FileChannel fc = stream.getChannel();
                MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                String jsonStr = Charset.defaultCharset().decode(bb).toString();
                dto = MoshiUtils.deserialize(jsonStr, MetaDto.class);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                stream.close();
                metaJson.delete();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return dto;
    }

    private void subscribeToDownloadFlowable(DownloadDataMessage updateDataMessages,
                                             List<Flowable<DownloadStatusEvent>> list,
                                             List<String> redundantFilePaths) {
        downloadDisposables.add(Flowable.concat(list)
                .doOnComplete(() -> {
                    System.gc();
                    Timber.d("All files downloaded successfully");
                    //Close and reinit flow manager, otherwise old version of Db remains in RAM
                    FlowManager.close();
                    FlowManager.init(context);
                    Timber.d("Database initialized");
                    Stream.of(redundantFilePaths).forEach(path -> new File(path).delete());
                    updateDataMessages.downloadStatusEventEmitter.onComplete();
                })
                .subscribe(objects -> {
                }, throwable -> {
                    if (!updateDataMessages.downloadStatusEventEmitter.isCancelled()) {
                        if (throwable.getMessage().contains("SSL")) {
                            updateDataMessages.downloadStatusEventEmitter.onError(
                                    new NGContentManagerException(NGContentManagerException.Type.CONNECTION_ABORT));
                        } else {
                            if(throwable.getCause() instanceof IOException){
                                updateDataMessages.downloadStatusEventEmitter.onError(
                                        new NGContentManagerException(NGContentManagerException.Type.NOT_CONNECTED, !NGDatabase.exists(context)));
                            }else{
                                updateDataMessages.downloadStatusEventEmitter.onError(
                                        new NGContentManagerException(throwable, !NGDatabase.exists(context)));
                            }
                        }

                    }
                }));
    }

    /**
     * calculate total fileSize
     * notice that this file size includes sizes of zips, which means that the downloaded and
     * extracted file-sizes are different
     */
    @NonNull
    private long calculateTotalDownloadFileSize(SyncResponseDto syncResponseDto,
                                                List<FileToDownload> filteredFileList) {
        return syncResponseDto.getSqliteFileSize() + Stream.of(filteredFileList)
                .mapToLong(z -> z.getFileSize()).sum();
    }

    private synchronized Retrofit createRetrofitInstance(String baseUri, String jwtApiToken, String jwtSyncServerToken) {
        if(BuildConfig.DEBUG || BuildConfig.FLAVOR == "inhouse"){
            Timber.d("Creating unsafe OK Http Client");
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .client(createUNSAVEOkHttpClient(jwtApiToken, jwtSyncServerToken))
                    .baseUrl(baseUri)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .validateEagerly(BuildConfig.DEBUG)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            return retrofit.build();
        } else {
            Retrofit.Builder retrofit = new Retrofit.Builder()
                    .client(createOkHttpClient(jwtApiToken, jwtSyncServerToken))
                    .baseUrl(baseUri)
                    .addConverterFactory(MoshiConverterFactory.create())
                    .validateEagerly(BuildConfig.DEBUG)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());
            return retrofit.build();
        }
    }


    private OkHttpClient createUNSAVEOkHttpClient(String jwtApiToken, String jwtSyncServerToken) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws CertificateException {
                        }

                        @Override
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                            return new java.security.cert.X509Certificate[]{};
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            HttpLoggingInterceptor loggingInterceptor
                    = new HttpLoggingInterceptor(message -> Timber.tag("HTTP").v(message));
            AuthInterceptor authInterceptor = new AuthInterceptor(jwtApiToken, jwtSyncServerToken);
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(authInterceptor);
            List<Protocol> protocols = new ArrayList<>();
            protocols.add(Protocol.HTTP_1_1);
            protocols.add(Protocol.HTTP_2);
            builder.protocols(protocols);
            builder.connectTimeout(60, TimeUnit.SECONDS);
            builder.writeTimeout(60, TimeUnit.SECONDS);
            builder.readTimeout(2, TimeUnit.MINUTES);

            builder.sslSocketFactory(sslSocketFactory, (X509TrustManager)trustAllCerts[0]);
            builder.hostnameVerifier((hostname, session) -> true);

            return builder.build();
        } catch (Exception e){
            Timber.d("Creatinging unsafe OK Http Client failed");

            throw new RuntimeException(e);
        }


    }

    private OkHttpClient createOkHttpClient(String jwtApiToken, String jwtSyncServerToken) {
        HttpLoggingInterceptor loggingInterceptor
                = new HttpLoggingInterceptor(message -> Timber.tag("HTTP").v(message));
        AuthInterceptor authInterceptor = new AuthInterceptor(jwtApiToken, jwtSyncServerToken);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor);
        builder.connectTimeout(2, TimeUnit.SECONDS);
        builder.writeTimeout(2, TimeUnit.SECONDS);
        builder.readTimeout(2, TimeUnit.MINUTES);
        return builder.build();
    }


    public void clearDisposablesAndDownloadStatus() {
        if (downloadDisposables != null) {
            downloadDisposables.clear();
        }
        downloadStatusFlowable = null;
    }

    @Builder
    @Getter
    @Setter
    public static class DownloadDataMessage {
        private int buildNumber;
        private String getBuildFileName(){
            return "guide-"+buildNumber+".json";
        }
        private FlowableEmitter<DownloadStatusEvent> downloadStatusEventEmitter;
        private boolean downloadOnlyDb;
        private boolean downloadLanguageDependentFiles;
    }

    @Builder
    @Getter
    public static class FileToDownload {
        private String downloadPath;
        private String storagePath;
        private long fileSize;
    }
//
//    @Builder
//    @Getter
//    @Setter
//    public static class SyncBuildNumberMessage {
//        private int localBuildNumber;
//        private FlowableEmitter<SyncBuildNumberEvent> buildUpdateEventEmitter;
//    }

}
