package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Exhibition")
@Getter
@Setter
public class NGExhibition extends NGBaseModel {
    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private long createdOn;

    @Column
    private long lastModified;

    @Column
    private String modifiedBy;

    @Column
    private String name;

    @Setter(AccessLevel.NONE)
    private List<String> supportedLanguages;

    @Setter(AccessLevel.NONE)
    @Nullable
    private List<NGGroup> groupList;

    @Setter(AccessLevel.NONE)
    @Nullable
    private List<NGTour> tourList;


    @Setter(AccessLevel.NONE)
    @Nullable
    private List<NGStation> stationList;

    @WorkerThread @Override public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        loadExhibitionLanguages();
        loadGroups();
        loadTours();
        loadStations();
    }

    public void loadExhibitionLanguages() {
        supportedLanguages = Injection.provideDao().getExhibitionLanguages(id);
    }

    @WorkerThread public void loadTours() {
        tourList = Injection.provideDao().getExhibitionTours(id);
//        Stream.of(tourList).forEach(t -> t.loadReferences(lang));
    }

    @WorkerThread public void loadGroups() {
        groupList = Injection.provideDao().getGroupsForExhibition(id);
//        Stream.of(groupList).forEach(g -> g.loadReferences(lang));
    }

    @WorkerThread public void loadStations() {
        stationList = Injection.provideDao().getStationsForExhibition(id);
    }
}
