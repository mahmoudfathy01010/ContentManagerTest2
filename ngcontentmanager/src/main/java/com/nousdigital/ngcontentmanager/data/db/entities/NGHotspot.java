package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.WorkerThread;

import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Hotspot")
@Getter
@Setter
public class NGHotspot extends NGBaseModel {
    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private String type;

    @Column
    private long createdOn;

    @Column
    private long lastModified;

    @Column
    private String modifiedBy;

    @Column
    private float width;

    @Column
    private float height;

    @Column
    private float left;

    @Column
    private float top;

    @Column
    private boolean renderTransparent;

    @Column
    private int page;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column
    int stationId;
    private NGStation station;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    @Column
    int substationId;
    private NGStation subStation;

    @WorkerThread public void loadStations(@NonNull String lang) {
        station = Injection.provideDao().getStation(stationId);
        subStation = Injection.provideDao().getStation(stationId);
        station.loadReferences(lang);
        subStation.loadReferences(lang);
    }
}
