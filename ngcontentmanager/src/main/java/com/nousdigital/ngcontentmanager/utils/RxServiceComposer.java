package com.nousdigital.ngcontentmanager.utils;

import com.nousdigital.ngcontentmanager.utils.schedulers.SchedulerProvider;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.functions.Function;
import retrofit2.HttpException;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class RxServiceComposer {
    public <T> ObservableTransformer<T, T> apiRequestTransformerIO() {
        return tObservable -> tObservable
                .subscribeOn(SchedulerProvider.getInstance().io())
                .compose(apiErrorTransformer());
    }

    public <T> ObservableTransformer<T, T> apiErrorTransformer() {
        return observable -> observable.onErrorResumeNext((Function<Throwable, ObservableSource<? extends T>>) throwable -> {
            if (throwable instanceof HttpException) {
                switch (((HttpException) throwable).code()) {
                    case 423:
                        break;
                }
                return Observable.error(throwable);
            }
            // if not the kind we're interested in, then just report the same error to onError()
            return Observable.error(throwable);
        });
    }
}
