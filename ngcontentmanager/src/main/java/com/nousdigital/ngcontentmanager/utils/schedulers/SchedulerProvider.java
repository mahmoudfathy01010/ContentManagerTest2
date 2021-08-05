package com.nousdigital.ngcontentmanager.utils.schedulers;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import io.reactivex.Scheduler;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


/**
 * Provides different types of schedulers.
 */
public class SchedulerProvider implements BaseSchedulerProvider {

    @Nullable
    private static SchedulerProvider INSTANCE;

    // Prevent direct instantiation.
    private SchedulerProvider() {
    }

    public static synchronized SchedulerProvider getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SchedulerProvider();
        }
        return INSTANCE;
    }

    @Override
    @NonNull
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @Override
    @NonNull
    public Scheduler io() {
        return Schedulers.io();
    }

    @Override
    @NonNull
    public io.reactivex.rxjava3.core.Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
