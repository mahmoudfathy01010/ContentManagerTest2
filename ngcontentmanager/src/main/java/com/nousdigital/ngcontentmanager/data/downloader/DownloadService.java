package com.nousdigital.ngcontentmanager.data.downloader;

import androidx.annotation.NonNull;

import com.nousdigital.ngcontentmanager.BuildConfig;
import com.nousdigital.ngcontentmanager.NGContentManager;
import com.nousdigital.ngcontentmanager.data.api.NGApiService;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.nousdigital.ngcontentmanager.data.repositories.NousConductorRepositoryImpl.FileToDownload;
import com.nousdigital.ngcontentmanager.exceptions.NGContentManagerException;
import com.nousdigital.ngcontentmanager.utils.events.DownloadStatusEvent;
import com.nousdigital.ngcontentmanager.utils.events.EventTypes;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.moshi.MoshiConverterFactory;
import timber.log.Timber;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.nousdigital.ngcontentmanager.utils.JWTUtils.getJwtTokenForApi;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 * <p>
 * Download and store files
 */
public class DownloadService {
    private final String basePath;
    private Call<ResponseBody> responseBodyCall;
    private static HashMap<String, NGApiService> serviceMap = new HashMap<>();
    private int iteration = 0;
    private Disposable _downloadDisposable;

    public DownloadService(@NonNull String basePath) {
        this.basePath = basePath;
        EventBus.getDefault().register(this);
    }

    public Flowable<DownloadStatusEvent> getDownloadFlowable(FileToDownload fileToDownload) {
        checkNotNull(fileToDownload.getDownloadPath());
        checkNotNull(fileToDownload.getStoragePath());
        return Flowable.create(emitter -> {
            callApiAndInitiateDownload(fileToDownload, emitter);
        }, BackpressureStrategy.LATEST);
    }

    private void callApiAndInitiateDownload(FileToDownload fileToDownload, @io.reactivex.rxjava3.annotations.NonNull FlowableEmitter<DownloadStatusEvent> emitter) {
        NGApiService ngApiService = getApiService();
        doBeforeDownload();
        responseBodyCall = ngApiService.downloadFile(fileToDownload.getDownloadPath());
        Timber.d("Initiate download call for %s", fileToDownload.getDownloadPath());
        responseBodyCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                downloadFile(response, fileToDownload, emitter);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                handleException(fileToDownload, emitter, t);
            }
        });
    }

    private void doBeforeDownload() {
        if (_downloadDisposable != null) {
            _downloadDisposable.dispose();
        }
        if (responseBodyCall != null) {
            responseBodyCall.cancel();
        }
    }

    @NotNull
    private NGApiService getApiService() {
        NGApiService ngApiService = serviceMap.get(basePath);
        if (ngApiService == null) {
            ngApiService = getDownloadRetrofitInstance(basePath).create(NGApiService.class);
            serviceMap.put(basePath, ngApiService);
        }
        return ngApiService;
    }

    public void downloadFailed(Throwable t, FlowableEmitter<DownloadStatusEvent> emitter) {
        Timber.e(t);
        EventBus.getDefault().unregister(this);
        if (!emitter.isCancelled()) {
            emitter.onError(new NGContentManagerException(t,
                    !NGDatabase.exists(NGContentManager.instance().getContext())));
        }
    }

    public void downloadFile(@NonNull Response<ResponseBody> response,
                             @NonNull FileToDownload fileToDownload,
                             @NonNull FlowableEmitter<DownloadStatusEvent> emitter) {
        String downloadPath = fileToDownload.getDownloadPath();
        String storagePath = fileToDownload.getStoragePath();
        EventBus.getDefault().unregister(this);
        if (response.isSuccessful()) {
            Timber.d("server contacted and has file %s", downloadPath);
            FileWriterService service = new FileWriterService(storagePath, emitter);
            _downloadDisposable = Observable.fromCallable(() -> {
                try {
                    iteration = 0; //lets reset it here
                    if (service.download(response.body().byteStream())) {
                        emitter.onComplete();
                    } else {
                        emitter.onError(new NGContentManagerException("could not complete download",
                                !NGDatabase.exists(NGContentManager.instance().getContext())));
                    }
                } catch (Exception e) {
                    handleException(fileToDownload, emitter, e);
                }finally {
                    System.gc();
                }
                return false;
            })
            .subscribeOn(Schedulers.io())
            .subscribe();
        } else {
            Timber.e("server contact failed");
            if (!emitter.isCancelled()) {
                emitter.onError(new NGContentManagerException("server contact failed",
                        !NGDatabase.exists(NGContentManager.instance().getContext())));
            }
        }
    }

    private void handleException(@NonNull FileToDownload fileToDownload, @NonNull FlowableEmitter<DownloadStatusEvent> emitter, Throwable e) {
        if (iteration == 3 || !(e instanceof IOException)) {
            emitter.onError(new NGContentManagerException(e,
                    !NGDatabase.exists(NGContentManager.instance().getContext())));
        } else {
            iteration += 1;
            Observable.fromCallable(() -> {
                try {
                    int sleep = 10000 + new Random().nextInt(5000);
                    Timber.d("Waiting for " + sleep + "ms after failed download, attempt " + iteration);
                    Thread.sleep(10000 + new Random().nextInt(5000)); //lets wait 10s and try again
                    callApiAndInitiateDownload(fileToDownload, emitter);
                } catch (InterruptedException ex) {
                    Timber.e(e);
                }
                return false;
            })
                    .subscribeOn(Schedulers.io()).subscribe();
        }
    }

    @Subscribe
    public void interrupt(EventTypes eventTypes) {
        if (eventTypes == EventTypes.INTERRUPT_DOWNLOAD && responseBodyCall != null) {
            responseBodyCall.cancel();
        }
    }

    public Retrofit getDownloadRetrofitInstance(String contentUri) {
        return new Retrofit.Builder()
                .baseUrl(contentUri)
                .addConverterFactory(MoshiConverterFactory.create())
                .validateEagerly(BuildConfig.DEBUG)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttpDownloadClientBuilder().build())
                .build();
    }

    public OkHttpClient.Builder getOkHttpDownloadClientBuilder() {
        HttpLoggingInterceptor loggingInterceptor
                = new HttpLoggingInterceptor(message -> Timber.tag("CDN-DOWNLOAD").v(message));
        AuthInterceptor authInterceptor = new AuthInterceptor(getJwtTokenForApi(), null);
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);
        OkHttpClient.Builder httpClientBuilder = new OkHttpClient.Builder();
        httpClientBuilder.addInterceptor(loggingInterceptor);
        httpClientBuilder.addInterceptor(authInterceptor);
        // You might want to increase the timeout
        httpClientBuilder.connectTimeout(3, TimeUnit.MINUTES);
        httpClientBuilder.writeTimeout(3, TimeUnit.MINUTES);
        httpClientBuilder.retryOnConnectionFailure(true);
        httpClientBuilder.readTimeout(3, TimeUnit.MINUTES);
        List<Protocol> protocols = new ArrayList<>();
        protocols.add(Protocol.HTTP_1_1);
        httpClientBuilder.protocols(protocols);
        return httpClientBuilder;
    }

}
