package com.nousdigital.ngcontentmanager.data.db.entities;

import com.nousdigital.ngcontentmanager.data.db.NGDatabase;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import lombok.Getter;
import lombok.Setter;

import static com.nousdigital.ngcontentmanager.data.db.entities.NGFile.FileType.AUDIO;
import static com.nousdigital.ngcontentmanager.data.db.entities.NGFile.FileType.IMAGE;
import static com.nousdigital.ngcontentmanager.data.db.entities.NGFile.FileType.VIDEO;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Table(database = NGDatabase.class, name = "File")
@Getter
@Setter
public class NGFile extends NGBaseModel {
    public enum FileType {
        IMAGE("image"),
        IMAGE_THUMBNAIL_TABLET("image3"),
        IMAGE_THUMBNAIL_PHONE("image4"),
        SLIDE("image2"),
        AUDIO("audio"),
        VIDEO("video"),
        FILE("file"),
        SUBTITLE("subtitle");

        private final String name;

        FileType(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @Column(name = "Id")
    @PrimaryKey
    private int id;

    @Column
    private String attribute;

    @Column
    private String filename;

    @Column
    private String href;

    @Column
    private int size;

    @Column
    private int width;

    @Column
    private int height;

    @Column
    private int duration;

    @Column
    private String language;

    @Column(name = "genericField1")
    private String title;

    //in JMB, for audio triggered slideshows, this field is misused for a JSON for the timings
    @Column(name = "genericField2")
    private String copyright;

    @Column(name = "genericField3")
    private String description;

    public boolean isAudio() {
        return AUDIO.toString().toLowerCase().equals(getAttribute());
    }

    public boolean isVideo() {
        return VIDEO.toString().toLowerCase().equals(getAttribute());
    }

    public boolean isImage() {
        return IMAGE.toString().toLowerCase().equals(getAttribute());
    }
}
