package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Stream;
import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.Statics;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Tour")
@Getter
@Setter
public class NGTour extends NGBaseModel {
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
    private String duration;

    @Column(name = "flag1")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag;

    public boolean isFlag() {
        return int_flag == 1;
    }

    @ForeignKey(references = {@ForeignKeyReference(columnName = "imageFileId",
                                                   foreignKeyColumnName = "Id")})
    private NGFile imageFile;

    @Setter(AccessLevel.NONE)
    private List<NGStation> stations;

    @Setter(AccessLevel.NONE)
    private List<NGTourActive> tourActiveList;

    //multilang fields
    @Column
    private int titleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String title;

    @Column
    private int subtitleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String subtitle;

    @Column
    private int textLongId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String textLong;

    @Column
    private int descriptionId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String description;

    @Getter @Setter(AccessLevel.NONE) @Nullable
    private int sortOrder;

    @Column
    private int mlstrgenId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String tourTime;

    @Column
    private int mlstrgen2Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String listeningTime;

    public boolean isTourActive(@NonNull String lang) {
        checkNotNull(lang);
        return tourActiveList != null &&
                Stream.of(getTourActiveList())
                        .filter(value -> lang.equals(value.getLanguage()) && value.isActive()).count() == 0;
    }

    @Override @WorkerThread public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        checkNotNull(lang);
        tourActiveList = Injection.provideDao().getTourActiveList(id);
        loadStations();
        loadMultilangFields();
        loadSortOrder();
    }

    public void loadMultilangFields() {
        title = Injection.provideDao().getInternationalText(titleId, Statics.CURRENT_LANGUAGE);
        subtitle = Injection.provideDao().getInternationalText(subtitleId, Statics.CURRENT_LANGUAGE);
        textLong = Injection.provideDao().getInternationalText(textLongId, Statics.CURRENT_LANGUAGE);
        description = Injection.provideDao().getInternationalText(descriptionId, Statics.CURRENT_LANGUAGE);
        tourTime = Injection.provideDao().getInternationalText(mlstrgenId, Statics.CURRENT_LANGUAGE);
        listeningTime = Injection.provideDao().getInternationalText(mlstrgen2Id, Statics.CURRENT_LANGUAGE);
    }

    public String getTitle(String lang){
        return Injection.provideDao().getInternationalText(titleId, lang);
    }

    public void loadSortOrder(){
        sortOrder = Injection.provideDao().getSortOrder(this.id);
    }

    @WorkerThread public void loadStations() {
        stations = Injection.provideDao().getTourStations(id);
    }
}
