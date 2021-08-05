package com.nousdigital.ngcontentmanager.data.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.nousdigital.ngcontentmanager.data.db.entities.*;
import com.nousdigital.ngcontentmanager.data.db.enums.StationSearchFilter;

import java.util.List;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public interface NGDao {

    List<NGExhibition> getAllExhibitions();

    List<String> getExhibitionLanguages(@NonNull final int id);

    List<NGTour> getExhibitionTours(@NonNull final int id);

    NGExhibition getExhibition(@NonNull int id);

    List<NGStation> getTourStations(@NonNull int id);

    List<NGTourActive> getTourActiveList();

    List<NGTourActive> getTourActiveList(@NonNull int id);

    NGStation getStation(@NonNull int id);

    List<NGStation> getStations(@NonNull List<Integer> id);

    NGStation getStationByNumber(@NonNull int number);

    NGStation getStationByCode(@NonNull int code);

    List<NGContent> getStationContentList(@NonNull int id);

    List<NGGroup> getStationGroupList(@NonNull int id);

    List<NGFile> getStationFileList(@NonNull int id, @Nullable String lang);

    List<NGStation> getChildStationsOfStation(@NonNull int id);

    List<NGFile> getContentFiles(@NonNull final int id, @Nullable final String lang);

    List<NGGame> getContentGames(@NonNull final int id, @Nullable final String lang, @Nullable final boolean loadReferences);

    NGFile getGameConfigFile(int gameId, String lang);

    NGFile getFile(int fileId);

    <T extends NGBaseModel> List<T> rawQuery(
            @NonNull Class responseClass, @NonNull String sql);

    List<NGGroup> getGroupsForExhibition(int id);

    List<NGStation> getStationsForExhibition(int id);

    List<NGStation> searchStation(@NonNull String searchTerm, StationSearchFilter filter, String currentLanguage);

    int getRandomStationId();

    List<NGGroup> getParentsOfGroup(int id);

    List<NGGroup> getChildsOfGroup(int id);

    List<NGContent> getGroupContentList(@NonNull int id);

    List<NGStation> getGroupStationList(@NonNull int id);

    @Nullable String getInternationalText(@NonNull int fieldId, @NonNull String lang);

    @Nullable NG3DObject get3DObject(@NonNull int stationId);

    List<NGHotspot> getChildStationHotspots(int id);

    Integer getVersion();

    NGTour getTour(int id);

    List<NGTour> getTours(@NonNull List<Integer> ids, List<String> tourTypes);

    List<NGTour> getAllTours();

    List<NGTour> getToursFromStation(int stationId);

    List<NGStation> getStationsWithType(String type);

    List<NGStation> getStationsWithTypes(String type1, String type2, int flag2, int flag3);

    NGMap getRecentMap();

    List<NGMap> getMaps();

    List<NGFile> getFilesOfType(NGFile.FileType file);

    int getSortOrder(int tourId);

}
