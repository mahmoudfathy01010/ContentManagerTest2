/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2020
 */

package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

@Table(database = NGDatabase.class, name = "Game")
@Getter
@Setter
public class NGGame extends NGBaseModel {
    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private String type;

    @Column
    private String category;

    @Column
    private int styleFileId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String styleFile;

    @Column
    private int gameFileId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String gameFile;

    @Column
    private int htmlFileId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String htmlFile;

    @Column
    private int reportScore;

    @Column
    private String reportUrl;

    private String configFile;

    @Column
    private int titleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String title;

    @Column
    private int descriptionId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String description;

    @Override @WorkerThread
    public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        checkNotNull(lang);
        loadInternationalTextFields(lang);
        //load files
        htmlFile = Injection.provideDao().getFile(htmlFileId).getHref();
        gameFile = Injection.provideDao().getFile(gameFileId).getHref();
        configFile = Injection.provideDao().getGameConfigFile(id, lang).getHref();
    }

    @WorkerThread
    public void loadInternationalTextFields(@NonNull String lang) {
        title = Injection.provideDao().getInternationalText(titleId, lang);
        description = Injection.provideDao().getInternationalText(descriptionId, lang);
    }

}
