package com.nousdigital.ngcontentmanager.data.api.dto;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Getter
@Setter
public class SyncResponseDto implements Parcelable {
    private int buildNumber;
    private String sqliteFile;
    private long sqliteFileSize;
    private List<ZipFileEntry> zipFiles;
    // if false, last sync is too old. Delete all files in sync folder
    private boolean isDeltaUpdate;

    protected SyncResponseDto(Parcel in) {
        buildNumber = in.readInt();
        sqliteFile = in.readString();
        sqliteFileSize = in.readLong();
        zipFiles = in.createTypedArrayList(ZipFileEntry.CREATOR);
        isDeltaUpdate = in.readByte() != 0;
    }

    public static final Creator<SyncResponseDto> CREATOR = new Creator<SyncResponseDto>() {
        @Override
        public SyncResponseDto createFromParcel(Parcel in) {
            return new SyncResponseDto(in);
        }

        @Override
        public SyncResponseDto[] newArray(int size) {
            return new SyncResponseDto[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(buildNumber);
        dest.writeString(sqliteFile);
        dest.writeLong(sqliteFileSize);
        dest.writeTypedList(zipFiles);
        dest.writeByte((byte) (isDeltaUpdate ? 1 : 0));
    }

    @Getter
    @Setter
    @Builder
    public static class ZipFileEntry implements Parcelable {
        public static final String ALL_LANGUAGES = "all";
        private String language;
        private String href;
        private long size;

        ZipFileEntry(){
            language = "";
            href = "";
            size = 0;
        }

        ZipFileEntry(String lang, String h, long l){
            language = lang;
            href = h;
            size = l;
        }

        protected ZipFileEntry(Parcel in) {
            language = in.readString();
            href = in.readString();
            size = in.readLong();
        }

        public static final Creator<ZipFileEntry> CREATOR = new Creator<ZipFileEntry>() {
            @Override
            public ZipFileEntry createFromParcel(Parcel in) {
                return new ZipFileEntry(in);
            }

            @Override
            public ZipFileEntry[] newArray(int size) {
                return new ZipFileEntry[size];
            }
        };

        /**
         * Could be images i.e. - so content that does not depend on a specific language
         *
         * @return
         */
        public boolean isLanguageIndependent() {
            return Strings.isNullOrEmpty(language) || ALL_LANGUAGES.equals(language);
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(language);
            dest.writeString(href);
            dest.writeLong(size);
        }
    }
}
