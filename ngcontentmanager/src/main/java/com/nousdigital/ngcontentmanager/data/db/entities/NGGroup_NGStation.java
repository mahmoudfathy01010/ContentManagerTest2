package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
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
@Table(database = NGDatabase.class, name = "Group_Station")
@Getter
@Setter
public class NGGroup_NGStation extends NGBaseModel {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "groupId",
                                                   foreignKeyColumnName = "Id")})
    private NGGroup group;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "stationId",
                                                   foreignKeyColumnName = "Id")})
    private NGStation station;
}
