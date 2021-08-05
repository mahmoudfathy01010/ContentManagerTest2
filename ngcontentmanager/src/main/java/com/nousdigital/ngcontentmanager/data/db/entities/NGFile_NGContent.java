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
@Getter
@Setter
@Table(database = NGDatabase.class, name = "File_Content")
public class NGFile_NGContent {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "fileId",
                                                   foreignKeyColumnName = "Id")})
    private NGFile file;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "contentId",
                                                   foreignKeyColumnName = "Id")})
    private NGContent content;

    @Column
    private int sortOrder;
}
