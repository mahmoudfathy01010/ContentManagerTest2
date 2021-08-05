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
@Table(database = NGDatabase.class, name = "Map")
@Getter
@Setter
public class NGMap extends NGBaseModel {
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
    private float originX;

    @Column
    private float originY;

    @Column
    private float scaleX;

    @Column
    private float scaleY;

    @Column
    private String label;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "imageFileId",
                                                   foreignKeyColumnName = "Id")})
    private NGFile imageFile;

    @Column
    private double width;

    @Column
    private double height;
}
