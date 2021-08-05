package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Tour_Station")
@Getter
@Setter
public class NGTour_NGStation {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "tourId",
                                                   foreignKeyColumnName = "Id")})
    private NGTour tour;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "stationId",
                                                   foreignKeyColumnName = "Id")})
    private NGStation station;

    @Column
    private int sortOrder;
}
