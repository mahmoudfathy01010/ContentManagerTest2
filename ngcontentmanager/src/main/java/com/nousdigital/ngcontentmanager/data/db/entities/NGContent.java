package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Stream;
import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.Statics;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Content")
@Getter
@Setter
public class NGContent extends NGBaseModel implements Serializable {

    public final static String VISUALLY_IMPAIRED_CONTENT = "5";

    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private String type;

    @Column
    private long createdOn;

    @Column
    private long lastModified;

    @Column
    private String modifiedBy;

    @Column
    private Integer copyrightId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String copyright;

    @Column
    private int titleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String title;

    @Column
    private int descriptionId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String description;

    @Column
    private String labelInternal;

    @Column
    private int flagMask;

    @Setter(AccessLevel.NONE)
    private List<NGFile> fileList;

    @Setter(AccessLevel.NONE)
    private List<NGGame> gameList;

    @Column
    private String fieldgen1;

    @Column
    private int nrgen1;

    @Column
    private Integer mlgen1Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String mlgen1;

    @Column
    private Integer mlgen2Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String mlgen2;

    @Override public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        title = Injection.provideDao().getInternationalText(titleId, Statics.CURRENT_LANGUAGE);
        description = Injection.provideDao().getInternationalText(descriptionId, Statics.CURRENT_LANGUAGE);
        if(copyrightId != null) {
            copyright = Injection.provideDao().getInternationalText(copyrightId, Statics.CURRENT_LANGUAGE);
        }
        if(mlgen1Id != null) {
            mlgen1 = Injection.provideDao().getInternationalText(mlgen1Id, Statics.CURRENT_LANGUAGE);
        }
        if(mlgen2Id != null) {
            mlgen2 = Injection.provideDao().getInternationalText(mlgen2Id, Statics.CURRENT_LANGUAGE);
        }
        loadFiles(lang);
    }

    @WorkerThread public void loadFiles(@Nullable String lang) {
        fileList = Injection.provideDao().getContentFiles(id, lang);
    }

    @WorkerThread public void loadGame(@NonNull String lang, @NonNull Boolean loadReferences) {
        gameList = Injection.provideDao().getContentGames(id, lang, loadReferences);
    }
}
