package com.nousdigital.ngcontentmanager.utils.events;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
@Getter
@Builder
public class DownloadStatusEvent {
    @Setter
    private long totalBytes;
    private long bytesRead;
    private String filePath;
    private EventTypes eventType;
}
