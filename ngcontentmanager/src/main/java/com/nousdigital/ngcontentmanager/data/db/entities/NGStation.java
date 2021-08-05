package com.nousdigital.ngcontentmanager.data.db.entities;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;
import com.nousdigital.ngcontentmanager.Injection;
import com.nousdigital.ngcontentmanager.Statics;
import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Collate;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.ForeignKeyReference;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "Station")
@Getter
@Setter
public class NGStation extends NGBaseModel implements Parcelable {

    public NGStation(){

    }

    public static final String INTRODUCTION_STATION_TYPE = "2";
    public static final String POI_STATION_TYPE = "1";
    public static final String SLIDESHOW_STATION_TYPE = "4";
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
    private String code;

    @Column
    private int number;

    @Column
    private String inventoryNumber;

    @Column(collate = Collate.NOCASE)
    private String inventoryNumberNormalized;

    @Column
    private String inventoryNumberCombined;

    @Column
    private String inventoryNumberPraefix;

    @Column
    private String room;

    @Column
    private String labelInternal;

    @Column
    private String genericField;

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

    @Column(name = "flag5")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag5;

    @Column(name = "flag6")
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    int int_flag6;

    public boolean flag1() {
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

    public boolean flag5() {
        return int_flag5 == 1;
    }

    public boolean flag6() {
        return int_flag6 == 1;
    }

    @Column
    private float geoLatitude;

    @Column
    private float geoLongitude;

    @Column
    private int mapX;

    @Column
    private int mapY;

    @Column
    private String mapId;

    @Column
    private int creatorNameId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String creatorName;

    @Column
    private int creationPlaceId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String creationPlace;

    @Column
    private int creationDateId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String creationDate;

    @Column(collate = Collate.NOCASE)
    private String creationPlaceNormalized;

    @Column
    private String creatorBirth;

    @Column(collate = Collate.NOCASE)
    private String creatorBirthNormalized;

    @Column(collate = Collate.NOCASE)
    private String creatorDeath;

    @Column(collate = Collate.NOCASE)
    private String creatorDeathNormalized;

    @Column(collate = Collate.NOCASE)
    private String creationDateNormalized;

    @Column @Nullable
    private Integer flagMask;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "parentId",
                                                   foreignKeyColumnName = "Id")})
    @Getter
    private NGStation parentStation;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "exhibitionId",
                                                   foreignKeyColumnName = "Id")})
    @Getter
    private NGExhibition parentExhibition;

    @Getter
    private List<NGTour> parentTours;

    @Column @Nullable
    private String referenceId;

    @Setter(AccessLevel.NONE)
    private List<NGStation> childStations;

    @Setter(AccessLevel.NONE)
    private List<NGContent> contentList;

    @ForeignKey(references = {@ForeignKeyReference(columnName = "beaconId",
                                                   foreignKeyColumnName = "Id")})
    @Getter
    private NGBeacon beacon;

    @Getter
    private NG3DObject threeDObject;

    //multilang fields
    @Column
    private int titleId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String title;

    @Column
    private int parentSortOrder;

    @Column
    private int genericMultiLangField1Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField1;

    @Column
    private int genericMultiLangField2Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField2;

    @Column
    private int genericMultiLangField3Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField3;

    @Column
    private int genericMultiLangField4Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField4;

    @Column
    private int genericMultiLangField5Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField5;

    @Column
    private int genericMultiLangField6Id;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String genericMultiLangField6;

    @Column
    private int copyrightTextId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String copyrightText;

    @Column
    private int longTextId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String longText;

    @Column
    private int locationTextId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String locationText;

    @Column
    private int shortTextId;
    @Getter @Setter(AccessLevel.NONE) @Nullable
    private String shortText;

    @Getter @Nullable
    private List<NGFile> fileList;

    @Getter @Nullable
    private List<NGGroup> groupList;

    @Getter @Nullable
    private List<NGHotspot> hotspotList;

    @Getter @Nullable @Column(name = "choice1", collate = Collate.NOCASE)
    private String century;

    @Getter @Nullable @Column(name = "choice2", collate = Collate.NOCASE)
    private String materialAndTechnique;

    @Getter @Nullable
    private List<String> choice3;

    @Getter @Nullable
    private List<String> choice4;

    @Override @WorkerThread
    public void loadReferences(@NonNull String lang) {
        super.loadReferences(lang);
        checkNotNull(lang);
        loadAllFiles();
        // IF I LOAD FILES WITH LANGUAGE, IF THE SETTING IS NON-GERMAN (=NON-DEFAULT?), I NEVER GET IMAGES*/
        //loadFiles(lang);
        loadInternationalTextFields(lang);
        loadContentList();
        loadChildStations();
        loadGroups();
        loadHotspots();
    }

    @WorkerThread public void loadFiles() {
        fileList = Injection.provideDao().getStationFileList(id, Statics.CURRENT_LANGUAGE);
    }

    @WorkerThread public void loadFiles(@NonNull String lang) {
        fileList = Injection.provideDao().getStationFileList(id, lang);
    }

    @WorkerThread public void loadAllFiles() {
        fileList = Injection.provideDao().getStationFileList(id, null);
    }

    @WorkerThread public void loadHotspots() {
        hotspotList = Injection.provideDao().getChildStationHotspots(id);
    }

    @WorkerThread public void loadGroups() {
        groupList = Injection.provideDao().getStationGroupList(id);
    }

    @WorkerThread public List<NGTour> loadParentTours() {
        parentTours = Injection.provideDao().getToursFromStation(id);
        return parentTours;
    }

    @WorkerThread public List<NGStation> loadChildStations() {
        childStations = Injection.provideDao().getChildStationsOfStation(id);
        return childStations;
    }

    @WorkerThread public void loadContentList() {
        contentList = Injection.provideDao().getStationContentList(id);
        if (contentList != null) {
            Stream.of(contentList).filter(ngContent -> ngContent != null)
                    .forEach(ngContent -> ngContent.loadReferences(Statics.CURRENT_LANGUAGE));
        }
    }

    @WorkerThread public void loadContentList(@NonNull String language) {
        contentList = Injection.provideDao().getStationContentList(id);
        if (contentList != null) {
            Stream.of(contentList).filter(ngContent -> ngContent != null)
                    .forEach(ngContent -> ngContent.loadReferences(language));
        }
    }

    @WorkerThread public void loadInternationalTextFields() {
        loadInternationalTextFields(Statics.CURRENT_LANGUAGE);
    }

    @WorkerThread public void loadInternationalTextFields(@NonNull String lang) {
        loadTitle(lang);
        copyrightText = Injection.provideDao().getInternationalText(copyrightTextId, lang);
        longText = Injection.provideDao().getInternationalText(longTextId, lang);
        shortText = Injection.provideDao().getInternationalText(shortTextId, lang);
        locationText = Injection.provideDao().getInternationalText(locationTextId, lang);
        creatorName = Injection.provideDao().getInternationalText(creatorNameId, lang);
        creationPlace = Injection.provideDao().getInternationalText(creationPlaceId, lang);
        creationDate = Injection.provideDao().getInternationalText(creationDateId, lang);

        loadGenericField1(lang);
        loadGenericField2(lang);
        loadGenericField3(lang);
        loadGenericField4(lang);
        loadGenericField5(lang);
        loadGenericField6(lang);
    }

    public void loadTitle(@NonNull String lang) {
        title = Injection.provideDao().getInternationalText(titleId, lang);
    }

    public void loadGenericField6(@NonNull String lang) {
        genericMultiLangField6 = Injection.provideDao().getInternationalText(genericMultiLangField6Id, lang);
    }

    public void loadGenericField1(@NonNull String lang) {
        genericMultiLangField1 = Injection.provideDao().getInternationalText(genericMultiLangField1Id, lang);
    }

    public void loadGenericField2(@NonNull String lang) {
        genericMultiLangField2 = Injection.provideDao().getInternationalText(genericMultiLangField2Id, lang);
    }

    public void loadGenericField3(@NonNull String lang) {
        genericMultiLangField3 = Injection.provideDao().getInternationalText(genericMultiLangField3Id, lang);
    }

    public void loadGenericField4(@NonNull String lang) {
        genericMultiLangField4 = Injection.provideDao().getInternationalText(genericMultiLangField4Id, lang);
    }

    public void loadGenericField5(@NonNull String lang) {
        genericMultiLangField5 = Injection.provideDao().getInternationalText(genericMultiLangField5Id, lang);
    }

    @WorkerThread public void load3DObject() {
        threeDObject = Injection.provideDao().get3DObject(id);
    }

    @WorkerThread public String getStationThumbnailPath(boolean isTablet) {
        if (fileList == null) {
            loadFiles(Statics.CURRENT_LANGUAGE);
        }
        if (fileList != null) {
            Optional<String> mainImageName = getMainImageNameOptional();
            if (mainImageName.isPresent()) {
                Optional<String> url = findMainImageThumbnailPathFromAllFiles(isTablet,
                        mainImageName.get());
                return url.isPresent() ? url.get() : getMainImagePath();
            }
        }
        return null;
    }

    private Optional<String> findMainImageThumbnailPathFromAllFiles(boolean isTablet, String mainImageName) {
        return Stream.of(fileList)
                .filter(f -> isTablet ?
                        f.getAttribute().equals(NGFile.FileType.IMAGE_THUMBNAIL_TABLET.getName())
                        : f.getAttribute().equals(NGFile.FileType.IMAGE_THUMBNAIL_PHONE.getName()))
                .filter(file -> file.getAttribute().equals(NGFile.FileType.IMAGE_THUMBNAIL_PHONE.getName()))
                .map(f -> f.getHref())
                .findFirst();
    }

    @NonNull public Optional<String> getMainImageNameOptional() {
        return Stream.of(fileList)
                .filter(f -> f.getAttribute().equals(NGFile.FileType.IMAGE.getName()))
                .map(f -> f.getFilename())
                .findFirst();

    }

    @WorkerThread public String getMainImagePath() {
        if (fileList == null) {
            loadFiles(Statics.CURRENT_LANGUAGE);
        }
        if (fileList != null) {
            Optional<NGFile> mainImageOptional = getMainImageOptional();
            return mainImageOptional.isPresent() ? mainImageOptional.get().getHref() : "";
        }
        return null;
    }

    private Optional<NGFile> getMainImageOptional() {
        if (fileList == null) {
            loadFiles(Statics.CURRENT_LANGUAGE);
        }
        return Stream.of(fileList)
                .filter(f -> f.getAttribute().equals(NGFile.FileType.IMAGE.getName()))
                .findFirst();
    }

    public boolean isIntroductionStation() {
        return INTRODUCTION_STATION_TYPE.equals(type);
    }

    public boolean isPoiStation() {
        return POI_STATION_TYPE.equals(type);
    }

    public boolean isSlideshowStation() {
        return SLIDESHOW_STATION_TYPE.equals(type);
    }

    public List<NGFile> getSlideshowList() {
        List<NGFile> slidesShowList = new ArrayList<>();
        Optional<NGFile> mainImageOptional = getMainImageOptional();
        if (mainImageOptional.isPresent()) {
            slidesShowList.add(mainImageOptional.get());
        }
        slidesShowList.addAll(Stream.of(fileList)
                .filter(f -> f.getAttribute().equals(NGFile.FileType.SLIDE.getName()))
                .toList());
        return slidesShowList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NGStation ngStation = (NGStation) o;
        return id == ngStation.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

//    /**
//     * Parcelable implementation
//     */
    protected NGStation(Parcel in) {
        id = in.readInt();
        type = in.readString();
        createdOn = in.readLong();
        lastModified = in.readLong();
        modifiedBy = in.readString();
        code = in.readString();
        number = in.readInt();
        inventoryNumber = in.readString();
        inventoryNumberNormalized = in.readString();
        inventoryNumberCombined = in.readString();
        inventoryNumberPraefix = in.readString();
        room = in.readString();
        labelInternal = in.readString();
        genericField = in.readString();
        int_flag = in.readInt();
        int_flag2 = in.readInt();
        int_flag3 = in.readInt();
        int_flag4 = in.readInt();
        int_flag5 = in.readInt();
        int_flag6 = in.readInt();
        geoLatitude = in.readFloat();
        geoLongitude = in.readFloat();
        mapX = in.readInt();
        mapY = in.readInt();
        mapId = in.readString();
        creatorNameId = in.readInt();
        creatorName = in.readString();
        creationPlaceId = in.readInt();
        creationPlace = in.readString();
        creationDateId = in.readInt();
        creationDate = in.readString();
        creationPlaceNormalized = in.readString();
        creatorBirth = in.readString();
        creatorBirthNormalized = in.readString();
        creatorDeath = in.readString();
        creatorDeathNormalized = in.readString();
        creationDateNormalized = in.readString();
        if (in.readByte() == 0) {
            flagMask = null;
        } else {
            flagMask = in.readInt();
        }
        parentStation = in.readParcelable(NGStation.class.getClassLoader());
        referenceId = in.readString();
        childStations = in.createTypedArrayList(NGStation.CREATOR);
        titleId = in.readInt();
        title = in.readString();
        parentSortOrder = in.readInt();
        genericMultiLangField1Id = in.readInt();
        genericMultiLangField1 = in.readString();
        genericMultiLangField2Id = in.readInt();
        genericMultiLangField2 = in.readString();
        genericMultiLangField3Id = in.readInt();
        genericMultiLangField3 = in.readString();
        genericMultiLangField4Id = in.readInt();
        genericMultiLangField4 = in.readString();
        genericMultiLangField5Id = in.readInt();
        genericMultiLangField5 = in.readString();
        genericMultiLangField6Id = in.readInt();
        genericMultiLangField6 = in.readString();
        copyrightTextId = in.readInt();
        copyrightText = in.readString();
        longTextId = in.readInt();
        longText = in.readString();
        locationTextId = in.readInt();
        locationText = in.readString();
        shortTextId = in.readInt();
        shortText = in.readString();
        century = in.readString();
        materialAndTechnique = in.readString();
        choice3 = in.createStringArrayList();
        choice4 = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(type);
        dest.writeLong(createdOn);
        dest.writeLong(lastModified);
        dest.writeString(modifiedBy);
        dest.writeString(code);
        dest.writeInt(number);
        dest.writeString(inventoryNumber);
        dest.writeString(inventoryNumberNormalized);
        dest.writeString(inventoryNumberCombined);
        dest.writeString(inventoryNumberPraefix);
        dest.writeString(room);
        dest.writeString(labelInternal);
        dest.writeString(genericField);
        dest.writeInt(int_flag);
        dest.writeInt(int_flag2);
        dest.writeInt(int_flag3);
        dest.writeInt(int_flag4);
        dest.writeInt(int_flag5);
        dest.writeInt(int_flag6);
        dest.writeFloat(geoLatitude);
        dest.writeFloat(geoLongitude);
        dest.writeInt(mapX);
        dest.writeInt(mapY);
        dest.writeString(mapId);
        dest.writeInt(creatorNameId);
        dest.writeString(creatorName);
        dest.writeInt(creationPlaceId);
        dest.writeString(creationPlace);
        dest.writeInt(creationDateId);
        dest.writeString(creationDate);
        dest.writeString(creationPlaceNormalized);
        dest.writeString(creatorBirth);
        dest.writeString(creatorBirthNormalized);
        dest.writeString(creatorDeath);
        dest.writeString(creatorDeathNormalized);
        dest.writeString(creationDateNormalized);
        if (flagMask == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(flagMask);
        }
        dest.writeParcelable(parentStation, flags);
        dest.writeString(referenceId);
        dest.writeTypedList(childStations);
        dest.writeInt(titleId);
        dest.writeString(title);
        dest.writeInt(parentSortOrder);
        dest.writeInt(genericMultiLangField1Id);
        dest.writeString(genericMultiLangField1);
        dest.writeInt(genericMultiLangField2Id);
        dest.writeString(genericMultiLangField2);
        dest.writeInt(genericMultiLangField3Id);
        dest.writeString(genericMultiLangField3);
        dest.writeInt(genericMultiLangField4Id);
        dest.writeString(genericMultiLangField4);
        dest.writeInt(genericMultiLangField5Id);
        dest.writeString(genericMultiLangField5);
        dest.writeInt(genericMultiLangField6Id);
        dest.writeString(genericMultiLangField6);
        dest.writeInt(copyrightTextId);
        dest.writeString(copyrightText);
        dest.writeInt(longTextId);
        dest.writeString(longText);
        dest.writeInt(locationTextId);
        dest.writeString(locationText);
        dest.writeInt(shortTextId);
        dest.writeString(shortText);
        dest.writeString(century);
        dest.writeString(materialAndTechnique);
        dest.writeStringList(choice3);
        dest.writeStringList(choice4);
    }

    public String getTitle(String lang){
        return Injection.provideDao().getInternationalText(titleId, lang);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NGStation> CREATOR = new Creator<NGStation>() {
        @Override
        public NGStation createFromParcel(Parcel in) {
            return new NGStation(in);
        }

        @Override
        public NGStation[] newArray(int size) {
            return new NGStation[size];
        }
    };
}
