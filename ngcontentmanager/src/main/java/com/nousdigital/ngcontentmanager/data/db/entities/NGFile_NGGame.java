/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2020
 */

package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(database = NGDatabase.class, name = "File_Game")
public class NGFile_NGGame {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "fileId",
            foreignKeyColumnName = "Id")})
    private NGFile file;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "gameId",
            foreignKeyColumnName = "Id")})
    private NGGame game;

    @Column
    private String type;
}
