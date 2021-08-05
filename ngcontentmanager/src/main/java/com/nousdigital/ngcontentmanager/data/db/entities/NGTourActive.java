package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "TourActive")
@Getter
@Setter
public class NGTourActive extends NGBaseModel {
    @Column
    @PrimaryKey
    private int tourId;

    @Column
    @PrimaryKey
    private String language;

    @Column
    private boolean active;
}
