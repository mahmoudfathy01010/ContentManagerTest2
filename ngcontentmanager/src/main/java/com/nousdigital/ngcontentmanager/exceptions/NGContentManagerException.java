package com.nousdigital.ngcontentmanager.exceptions;

import static com.nousdigital.ngcontentmanager.exceptions.NGContentManagerException.Type.GENERIC;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class NGContentManagerException extends Exception {
    private final Type type;
    private final boolean noDatabase;

    public enum Type {
        SD_CARD_DISABLED, DOWNLOAD_PENDING, NOT_ENOUGH_SPACE, NOT_CONNECTED, GENERIC,
        CMS_BUSY, NO_UPDATE, UNAUTHORIZED, INTERNAL_SERVER_ERROR,
        CONNECTION_ABORT
    }

    public NGContentManagerException(Type type) {
        this.type = type;
        noDatabase = false;
    }

    public NGContentManagerException(Type type, boolean noDatabase) {
        this.type = type;
        this.noDatabase = noDatabase;
    }

    public NGContentManagerException(Throwable throwable, boolean noDatabase) {
        super(throwable);
        this.type = GENERIC;
        this.noDatabase = noDatabase;
    }

    public NGContentManagerException(String error, boolean noDatabase) {
        super(new Exception(error));
        this.type = GENERIC;
        this.noDatabase = noDatabase;
    }

    public Type getType() {
        return type;
    }

    public boolean isNoDatabase() {
        return noDatabase;
    }
}
