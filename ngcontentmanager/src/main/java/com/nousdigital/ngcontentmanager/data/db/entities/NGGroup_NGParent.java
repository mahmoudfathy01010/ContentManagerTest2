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
@Table(database = NGDatabase.class, name = "Group_Parent")
@Getter
@Setter
public class NGGroup_NGParent {
    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "parentGroupId",
                                                   foreignKeyColumnName = "Id")})
    private NGGroup parent;

    @PrimaryKey
    @ForeignKey(references = {@ForeignKeyReference(columnName = "childGroupId",
                                                   foreignKeyColumnName = "Id")})
    private NGGroup child;
}
