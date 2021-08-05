/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2020
 */

package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

@Table(database = NGDatabase.class, name = "GameActive")
@Getter
@Setter
public class NGGameActive extends NGBaseModel {
    @Column
    @PrimaryKey
    private int gameId;

    @Column
    @PrimaryKey
    private String language;

    @Column
    private boolean active;
}
