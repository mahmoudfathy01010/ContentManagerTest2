package com.nousdigital.ngcontentmanager.data.db.entities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Stream;
import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.List;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Group")
@Getter
@Setter
public class NGGroup extends NGBaseModel {

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

    @Column(name = "flag1")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag;

    @Column(name = "flag2")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag2;

    @Column(name = "flag3")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag3;

    @Column(name = "flag4")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag4;

    public boolean flag() {
        return int_flag == 1;
    }

    public boolean flag2() {
        return int_flag2 == 1;
    }

    public boolean flag3() {
        return int_flag3 == 1;
    }

    public boolean flag4() {
        return int_flag4 == 1;
    }

    private int titleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String title;

    private int genericMultiLangField1Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField1;

    private int genericMultiLangField2Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField2;

    private int genericMultiLangField3Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField3;

    private int genericMultiLangField4Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField4;

    private int genericMultiLangField5Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField5;

    private int genericMultiLangField6Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField6;

    @Column
    @ForeignKey(references = {@ForeignKeyReference(columnName = "exhibitionId",
                                                   foreignKeyColumnName = "Id")})
    private NGExhibition exhibitionId;

    @Setter(AccessLevel.NONE)
    private List<NGContent> contentList;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private List<NGStation> stations;

    @Setter(AccessLevel.NONE)
    private List<NGGroup> parentGroupList;

    @Setter(AccessLevel.NONE)
    private List<NGGroup> childGroupList;

    @Override @WorkerThread public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        checkNotNull(lang);

        loadMultilanguageText(lang);
        loadContentList(lang);
        loadChilds(lang);
    }

    @WorkerThread public void loadParents() {
        parentGroupList = Injection.provideDao().getParentsOfGroup(id);
    }

    @WorkerThread public void loadChilds(@NonNull String lang) {
        checkNotNull(lang);
        childGroupList = Injection.provideDao().getChildsOfGroup(id);
//        Stream.of(childGroupList).forEach(g -> g.loadReferences(lang));
    }

    @WorkerThread public void loadContentList(String lang) {
        checkNotNull(lang);
        contentList = Injection.provideDao().getGroupContentList(id);
        Stream.of(contentList).forEach(ngContent -> ngContent.loadReferences(lang));
    }

    /**
     * Get all associated stations, don't load this on UI thread
     *
     * @return stations
     */
    @WorkerThread public List<NGStation> getStations() {
        if (stations == null) {
            stations = Injection.provideDao().getGroupStationList(id);
//            Stream.of(stations).forEach(s -> s.loadReferences());
        }
        return stations;
    }

    @WorkerThread public void loadMultilanguageText(@NonNull String lang) {
        title = Injection.provideDao().getInternationalText(titleId, lang);
        genericMultiLangField1 = Injection.provideDao().getInternationalText(genericMultiLangField1Id, lang);
        genericMultiLangField2 = Injection.provideDao().getInternationalText(genericMultiLangField2Id, lang);
        genericMultiLangField3 = Injection.provideDao().getInternationalText(genericMultiLangField3Id, lang);
        genericMultiLangField4 = Injection.provideDao().getInternationalText(genericMultiLangField4Id, lang);
        genericMultiLangField5 = Injection.provideDao().getInternationalText(genericMultiLangField5Id, lang);
        genericMultiLangField6 = Injection.provideDao().getInternationalText(genericMultiLangField6Id, lang);
    }
}
