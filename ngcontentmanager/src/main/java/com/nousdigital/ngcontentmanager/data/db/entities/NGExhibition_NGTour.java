package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.*;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Exhibition_Tour")
@Getter
@Setter
public class NGExhibition_NGTour {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "exhibitionId",
                                                   foreignKeyColumnName = "Id")})
    private NGExhibition exhibition;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "tourId",
                                                   foreignKeyColumnName = "Id")})
    private NGTour tour;

    @Column
    @Getter
    private int sortOrder;
}
