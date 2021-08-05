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
@Table(database = NGDatabase.class, name = "ThreeDObject")
@Getter
@Setter
public class NG3DObject extends NGBaseModel {
    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private int stationId;

    @Column
    private String identifier;
}
