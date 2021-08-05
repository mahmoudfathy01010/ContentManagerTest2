package com.nousdigital.ngcontentmanager.utils.schedulers;

import androidx.annotation.NonNull;

import io.reactivex.Scheduler;

/**
 * Allow providing different types of {@link Scheduler}s.
 */
public interface BaseSchedulerProvider {

    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    io.reactivex.rxjava3.core.Scheduler ui();
}
