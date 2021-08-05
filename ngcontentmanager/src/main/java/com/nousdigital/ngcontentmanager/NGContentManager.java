package com.nousdigital.ngcontentmanager;

import android.content.Context;
import androidx.annotation.NonNull;

import com.annimon.stream.Stream;
import com.google.common.base.Strings;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.nousdigital.ngcontentmanager.data.db.entities.*;
import com.nousdigital.ngcontentmanager.data.db.enums.StationSearchFilter;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepository;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl.DownloadDataMessage;
import com.nousdigital.ngcontentmanager.exceptions.NGContentManagerException;
import com.nousdigital.ngcontentmanager.utils.NetworkUtils;
import com.nousdigital.ngcontentmanager.utils.SDCardUtils;
import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;
import com.nousdigital.ngcontentmanager.utils.events.EventTypes;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class NGContentManager {
    private NGContentManager() {
    }

    private static NGContentManager INSTANCE;
    @Getter @Setter
    private Context context;
    private NousConductorRepository nousConductorRepository;
    private boolean downloadPending;

    private String version = "";

    /**
     * Initialize NGContentManager
     *
     * @param configuration mandatory: context & apiUri
     */
    public static void init(Configuration configuration) {
        checkNotNull(configuration);
        checkNotNull(configuration.getContext());
        checkNotNull(configuration.getApiUri());
        INSTANCE = new NGContentManager();
        INSTANCE.context = configuration.getContext();
        INSTANCE.nousConductorRepository = Injection.provideNousConductorRepository(
                        configuration.apiUri,
                        configuration.contentUri,
                        configuration.syncServerUri,
                        configuration.getContext());
        INSTANCE.initDb();
        setStatics(configuration);
    }

    private static void setStatics(Configuration configuration) {
        Statics.DB_PATH =
                INSTANCE.context.getDatabasePath(NGDatabase.NAME + NGDatabase.DATABASE_EXTENSION).getPath();
        if (!Strings.isNullOrEmpty(configuration.language)) {
            setLanguage(configuration.language);
        }
        Statics.CONTENT_URL = configuration.getContentUri();
    }

    /**
     * Only content with this language will be downloaded/used. Default: en
     *
     * @param language
     */
    public static void setLanguage(String language) {
        checkNotNull(language);
        Statics.CURRENT_LANGUAGE = language;
    }

    public static NGContentManager instance() {
        if (INSTANCE == null) {
            Timber.e("You must call NGContentManager#init first");
        }
        checkNotNull(INSTANCE);
        return INSTANCE;
    }

    public int getBuildNumber() {
        return INSTANCE.nousConductorRepository.getBuildNumber();
    }

    public int getBuildNumberFromDb() {
        return INSTANCE.nousConductorRepository.getBuildNumberFromDb();
    }


    @Builder
    @Getter
    public static class Configuration {
        private Context context;
        private String apiUri;
        private String contentUri;
        private String syncServerUri;
        private String language;
    }

    private long downloadFinishedTimeStamp = 0;

    /**
     * Update database and content data
     * <p>
     * When subscribing a second time to this while the download is still running, it does not start
     * the download again but instead returns the current status of the previous download session
     *
     * @param force if true, the db and files are removed and downloaded again (useful i.e. after language change)
     * @return progress updates
     */
    public Flowable<Integer> downloadDatabaseAndFiles(boolean force) {
        if(INSTANCE.downloadPending){
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.DOWNLOAD_PENDING, !initDb()));
        }
        else if (!NetworkUtils.isConnected(context)) {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.NOT_CONNECTED, !initDb()));
        } else if (SDCardUtils.isSDCardEnable()) {
            INSTANCE.downloadPending = true;
            final AtomicLong currentProgress = new AtomicLong(0);
            return nousConductorRepository.
                    downloadData(DownloadDataMessage.builder()
                    .downloadOnlyDb(false).build(), force, context)
                    .map(progress -> calculateUpdateProgressInPercent(currentProgress, progress))
                    .doAfterTerminate(this::cleanUpAfterDownloadingFiles);
        } else {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.SD_CARD_DISABLED, !initDb()));
        }
    }

    /**
     * Update database and content data FROM THE SYNCSERVER
     * <p>
     * When subscribing a second time to this while the download is still running, it does not start
     * the download again but instead returns the current status of the previous download session
     *
     * @return progress updates
     */
    public Flowable<Integer> downloadDatabaseAndFilesFromSyncServer() {
        if(INSTANCE.downloadPending){
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.DOWNLOAD_PENDING, !initDb()));
        }
        else if (!NetworkUtils.isConnected(context)) {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.NOT_CONNECTED, !initDb()));
        } else if (SDCardUtils.isSDCardEnable()) {
            final AtomicLong currentProgress = new AtomicLong(0);
            return nousConductorRepository.
                    downloadDataFromSyncServer(DownloadDataMessage.builder()
                            .downloadOnlyDb(false).build(), context)
                    .map(progress -> calculateUpdateProgressInPercent(currentProgress, progress))
                    .doAfterTerminate(this::cleanUpAfterDownloadingFiles);
        } else {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.SD_CARD_DISABLED, !initDb()));
        }
    }

    public Single<Integer> calculateFileSize(String language) {
        return nousConductorRepository.calculateFileSize(language);
    }

    private String removeSubpath(String subpath, String href) {
        return href.replace(subpath, "");
    }

    private List<File> getFilesOnSDCard(String pathOnSD) {
        File list[] = new File(pathOnSD).listFiles();
        if(list != null) {
            return Arrays.asList(list);
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * We need to figure out if we just recently had a successful download in order to avoid
     * downloading again.
     * This might come handy when app is in background while the download finishes and user comes back
     * and view is attached
     *
     * @return
     */
    private boolean lastDownloadIsCompleteAndStillValid() {
        //if last download was successful and less then 30min ago return true
        return System.currentTimeMillis() - downloadFinishedTimeStamp < (1000 * 60 * 30);
    }

    /**
     * Update database
     * <p>
     * When subscribing a second time to this while the download is still running, it does not start
     * the download again but instead returns the current status of the previous download session
     *
     * @param force if true, the db and files are removed and downloaded again (useful i.e. after language change)
     * @return progress updates
     */
    public Flowable<Integer> downloadDatabase(boolean force) {
        if(INSTANCE.downloadPending){
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.DOWNLOAD_PENDING, !initDb()));
        }
        else if (!NetworkUtils.isConnected(context)) {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.NOT_CONNECTED, !initDb()));
        } else if (SDCardUtils.isSDCardEnable()) {
            final AtomicLong currentProgress = new AtomicLong(0);
            return nousConductorRepository.downloadData(DownloadDataMessage.builder()
                    .downloadOnlyDb(true).build(), force, context)
                    .map(progress -> calculateUpdateProgressInPercent(currentProgress, progress))
                    .doOnError(throwable -> Timber.d("BOO"))
                    .doAfterTerminate(this::cleanUpAfterDownloadingFiles);
        } else {
            return Flowable.error(new NGContentManagerException(NGContentManagerException.Type.SD_CARD_DISABLED, !initDb()));
        }
    }

    public boolean initDb() {
        if (NGDatabase.exists(context)) {
            FlowManager.init(context);
            return true;
        }
        return false;
    }

    @NonNull public Action cleanUpAfterDownloadingFiles() {
        return () -> {
            Timber.d("cleaning up after downloading db and/or files");
            if (NGDatabase.exists(context)) {
                downloadFinishedTimeStamp = System.currentTimeMillis();
            }
            clearDisposables();
            INSTANCE.downloadPending = false;
        };
    }

    private int calculateUpdateProgressInPercent(AtomicLong currentProgress, DownloadStatusEvent progress) {
        long writtenBytes = currentProgress.addAndGet(progress.getBytesRead());
        int percentage = (int) (writtenBytes * 100 / progress.getTotalBytes());
        Timber.d("DOWNLOAD_PROGRESS: %s percent, total = %s/%sMb", percentage,
                (writtenBytes / 1000.0),
                (progress.getTotalBytes() / 1000.0));
        return percentage;
    }

    /**
     * Delete all data
     * 1) database
     * 2) asset data on sdcard
     *
     * @return Single
     */
    public Single<Boolean> deleteAllFiles(Boolean keepSyncServerLog) {
        return Single.create(emitter -> {
            Timber.d("Start deleting all data");
            nousConductorRepository.deleteAllFiles(keepSyncServerLog);
            emitter.onSuccess(true);
        });
    }

    /**
     * Interrupt download if running
     *
     * @return Disposable -> clear to avoid memory leaks
     */
    public Disposable interruptDownload() {
        Timber.d("Interrupting downloading");
        INSTANCE.downloadPending = false;
        return Completable.fromAction(() ->
                EventBus.getDefault().post(EventTypes.INTERRUPT_DOWNLOAD))
                .subscribe(() -> clearDisposables());
    }

    /**
     * Clear all disposables
     */
    public void clearDisposables() {
        Timber.d("Clear disposables from repository");
        nousConductorRepository.clearDisposablesAndDownloadStatus();
    }

    //DB / Cache QUERIES

    public Single<NGExhibition> getExhibition(int id) {
        return Single.create(emitter -> {
            NGExhibition exhibition = Injection.provideDao().getExhibition(id);
            exhibition.loadReferences(Statics.CURRENT_LANGUAGE);
            emitter.onSuccess(exhibition);
        });
    }

    public Single<List<NGTour>> getAllTours() {
        return Single.create(emitter -> {
            List<NGTour> tours = Injection.provideDao().getAllTours();
            Stream.of(tours).forEach(ngTour -> ngTour.loadReferences(Statics.CURRENT_LANGUAGE));
            emitter.onSuccess(tours);
        });
    }

    public Single<List<NGTourActive>> getActiveToursList() {
        return Single.create(emitter -> {
            List<NGTourActive> tours = Injection.provideDao().getTourActiveList();
            emitter.onSuccess(tours);
        });
    }

    public Single<List<NGStation>> getStationsForType(String type, boolean loadReferences) {
        return Single.create(emitter -> {
            List<NGStation> stations = Injection.provideDao().getStationsWithType(type);
            if (loadReferences) {
                loadStationReferences(stations);
            }
            emitter.onSuccess(stations);
        });
    }

    public Single<List<NGStation>> getStationsForTypes(String type1, String type2, int flag2, int flag3, boolean loadReferences) {
        return Single.create(emitter -> {
            List<NGStation> stations = Injection.provideDao().getStationsWithTypes(type1, type2, flag2, flag3);
            if (loadReferences) {
                loadStationReferences(stations);
            }
            emitter.onSuccess(stations);
        });
    }

    private void loadStationReferences(List<NGStation> stations){
        Stream.of(stations)
                .forEach(ngStation -> {
                    ngStation.loadReferences(Statics.CURRENT_LANGUAGE);
                    loadStationReferences(ngStation.getChildStations());
                });
    }

    public Single<NGTour> getTour(int id) {
        return Single.create(emitter -> {
            NGTour tour = Injection.provideDao().getTour(id);
            tour.loadReferences(Statics.CURRENT_LANGUAGE);
            emitter.onSuccess(tour);
        });
    }

    public Single<List<NGTour>> getTours(List<Integer> tourIds, List<String> tourTypes) {
        return Single.create(emitter -> {
            List<NGTour> tours= Injection.provideDao().getTours(tourIds, tourTypes);
            loadTourReferences(tours);
            emitter.onSuccess(tours);
        });
    }

    private void loadTourReferences(List<NGTour> tours){
        Stream.of(tours)
                .forEach(ngTour -> {
                    ngTour.loadReferences(Statics.CURRENT_LANGUAGE);
                });
    }

    public Observable<NGStation> searchStations(String searchTerm, StationSearchFilter filter) {
        return Observable.create(emitter -> {
            Stream.of(Injection.provideDao().searchStation(searchTerm, filter, Statics.CURRENT_LANGUAGE))
                    .forEach(ngStation -> {
                        ngStation.loadParentTours();
                        ngStation.loadInternationalTextFields(Statics.CURRENT_LANGUAGE);
                        emitter.onNext(ngStation);
                    });
            emitter.onComplete();
        });
    }

    public Single<NGStation> getStation(int id) {
        return Single.create(emitter -> {
            NGStation station = Injection.provideDao().getStation(id);
            if (station != null) {
                emitter.onSuccess(station);
            } else {
                emitter.onError(new Exception("Station with id " + id + " does not exist in db"));
            }
        });
    }

    public Single<NGStation> getStationByNumber(int number) {
        return Single.create(emitter -> {
            NGStation station = Injection.provideDao().getStationByNumber(number);
            if (station != null) {
                emitter.onSuccess(station);
            } else {
                emitter.onError(new Exception("Station with number " + number + " does not exist in db"));
            }
        });
    }

    public Single<NGStation> getStationByCode(int code) {
        return Single.create(emitter -> {
            NGStation station = Injection.provideDao().getStationByCode(code);
            if (station != null) {
                emitter.onSuccess(station);
            } else {
                emitter.onError(new Exception("Station with number " + code + " does not exist in db"));
            }
        });
    }

    public Single<List<NGStation>> getStations(List<Integer> stationIds) {
        return Single.create(emitter -> {
            List<NGStation> stations = Injection.provideDao().getStations(stationIds);
            loadStationReferences(stations);
            emitter.onSuccess(stations);
        });
    }

    public Single<List<NGStation>> getStationsWithDuplicates(List<Integer> stationIds) {
        return Single.create(emitter -> {
            List<NGStation> stations = Injection.provideDao().getStations(stationIds);
            loadStationReferences(stations);

            Map<Integer, NGStation> stationsMap = new HashMap();
            for (NGStation st : stations) stationsMap.put(st.getId(), st);

            ArrayList listWithDuplicateIds = new ArrayList();
            for (Integer i : stationIds) listWithDuplicateIds.add(stationsMap.get(i));
            emitter.onSuccess(listWithDuplicateIds);
        });
    }

    public Single<List<NGMap>> getMaps(){
        return Single.create(emitter ->{
            List<NGMap> maps = Injection.provideDao().getMaps();
            emitter.onSuccess(maps);
        });
    }

}


