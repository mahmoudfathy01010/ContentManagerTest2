package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "InternationalText")
@Getter
@Setter
public class NGInternationalText extends NGBaseModel {

    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    @PrimaryKey
    private String language;

    @Column(collate = Collate.NOCASE)
    private String text;

    @Column(collate = Collate.NOCASE)
    private String textNormalized;
}
