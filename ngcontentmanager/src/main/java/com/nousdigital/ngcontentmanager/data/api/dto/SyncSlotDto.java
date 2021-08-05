/*
 * created by Silvana Podaras
 * © NOUS Wissensmanagement GmbH, 2019
 */

package com.nousdigital.ngcontentmanager.data.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class SyncSlotDto {
    private int id;
    private String createdAt;
    private String syncDateTime;
    private String deviceId; //.Net GUID
    private int buildVersion;
}
