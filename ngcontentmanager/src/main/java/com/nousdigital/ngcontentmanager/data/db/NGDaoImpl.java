package com.nousdigital.ngcontentmanager.data.db;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.google.common.base.Strings;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.nousdigital.ngcontentmanager.Statics;
import com.nousdigital.ngcontentmanager.data.db.entities.*;
import com.nousdigital.ngcontentmanager.data.db.enums.StationSearchFilter;
import com.raizlabs.android.dbflow.sql.language.From;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.sql.queriable.StringQuery;
import com.raizlabs.android.dbflow.structure.database.FlowCursor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Felix Tutzer
 * © NOUS Wissensmanagement GmbH, 2018
 */
public class NGDaoImpl implements NGDao {
    private final LoadingCache<Object, NGBaseModel> exhibitionLoader;

    private <T> LoadingCache<T, NGBaseModel> createLoader(CacheLoader cacheLoader) {
        return CacheBuilder.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .build(cacheLoader);
    }

    public NGDaoImpl() {
        this.exhibitionLoader = createLoader(createExhibitionLoader());
    }

    @NonNull private CacheLoader createExhibitionLoader() {
        return new CacheLoader() {
            @Override public Object load(Object id) {
                return loadExhibitionFromDb((int) id);
            }
        };
    }

    private NGExhibition loadExhibitionFromDb(int id) {
        return SQLite.select()
                .from(NGExhibition.class)
                .where(NGExhibition_Table.Id.eq(id)).querySingle();
    }

    //EXHIBITION
    public NGExhibition getExhibition(@NonNull int id) {
        try {
            return (NGExhibition) exhibitionLoader.get(id);
        } catch (ExecutionException e) {
            return loadExhibitionFromDb(id);
        }
    }

    @Override public List<NGExhibition> getAllExhibitions() {
        return Stream.of(SQLite.select()
                .from(NGExhibition.class)
                .queryList())
                .filter(value -> value != null).toList();
    }

    public List<String> getExhibitionLanguages(final int id) {
        List<NGExhibitionLanguage> languages = SQLite.select(NGExhibitionLanguage_Table.language)
                .from(NGExhibitionLanguage.class)
                .where(NGExhibitionLanguage_Table.exhibitionId.eq(id))
                .queryList();
        if (languages.size() > 0) {
            return Stream.of(languages)
                    .map(lang -> lang.getLanguage())
                    .filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGTour> getExhibitionTours(final int id) {
        List<NGExhibition_NGTour> tours = SQLite.select(NGExhibition_NGTour_Table.tourId)
                .from(NGExhibition_NGTour.class)
                .where(NGExhibition_NGTour_Table.exhibitionId.eq(id))
                .queryList();
        if (tours.size() > 0) {
            return Stream.of(tours)
                    .map(t -> t.getTour())
                    .filter(value -> value != null)
                    .toList();
        }
        return Collections.emptyList();
    }

    //GROUPS
    @Override public List<NGGroup> getGroupsForExhibition(final int id) {
        return Stream.of(SQLite.select()
                .from(NGGroup.class)
                .where(NGGroup_Table.exhibitionId.eq(id))
                .queryList())
                .filter(value -> value != null)
                .toList();
    }

    @Override public List<NGStation> getStationsForExhibition(int id) {
        return Stream.of(SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.exhibitionId.eq(id))
                .queryList()).filter(value -> value != null).toList();
    }

    @Override public List<NGGroup> getParentsOfGroup(final int id) {
        List<NGGroup_NGParent> parents = SQLite.select(NGGroup_NGParent_Table.parentGroupId)
                .from(NGGroup_NGParent.class)
                .where(NGGroup_NGParent_Table.childGroupId.eq(id))
                .queryList();
        if (parents.size() > 0) {
            return Stream.of(parents)
                    .map(p -> p.getParent()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGGroup> getChildsOfGroup(final int id) {
        List<NGGroup_NGParent> children = SQLite.select(NGGroup_NGParent_Table.childGroupId)
                .from(NGGroup_NGParent.class)
                .where(NGGroup_NGParent_Table.parentGroupId.eq(id))
                .queryList();
        if (children.size() > 0) {
            return Stream.of(children)
                    .map(c -> c.getChild()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGContent> getGroupContentList(final int id) {
        List<NGGroup_NGContent> contents = SQLite.select(NGGroup_NGContent_Table.contentId)
                .from(NGGroup_NGContent.class)
                .where(NGGroup_NGContent_Table.groupId.eq(id))
                .queryList();
        if (contents.size() > 0) {
            return Stream.of(contents)
                    .map(lang -> lang.getContent()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGStation> getGroupStationList(final int id) {
        List<NGGroup_NGStation> stations = SQLite.select(NGGroup_NGStation_Table.stationId)
                .from(NGGroup_NGStation.class)
                .where(NGGroup_NGStation_Table.groupId.eq(id))
                .queryList();
        if (stations.size() > 0) {
            return Stream.of(stations)
                    .map(s -> s.getStation()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGGroup> getStationGroupList(final int id) {
        List<NGGroup_NGStation> groups = SQLite.select(NGGroup_NGStation_Table.groupId)
                .from(NGGroup_NGStation.class)
                .where(NGGroup_NGStation_Table.stationId.eq(id))
                .queryList();
        if (groups.size() > 0) {
            return Stream.of(groups)
                    .map(g -> g.getGroup()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    //INTERNATIONAL TEXT
    @Override public String getInternationalText(final int id, @NonNull String lang) {
        NGInternationalText txt = SQLite.select()
                .from(NGInternationalText.class)
                .where(NGInternationalText_Table.Id.eq(id))
                .and(NGInternationalText_Table.language.eq(lang))
                .querySingle();
        if (txt != null) {
            return txt.getText();
        } else if (!Statics.DEFAULT_LANGUAGE.equals(lang)) {
            return getInternationalText(id, Statics.DEFAULT_LANGUAGE);
        }
        return null;
    }

    @Override public List<NGHotspot> getChildStationHotspots(final int id) {
        return Stream.of(SQLite.select()
                .from(NGHotspot.class)
                .where(NGHotspot_Table.substationId.eq(id))
                .queryList()).filter(value -> value != null).toList();
    }

    //TOUR
    @Override public List<NGStation> getTourStations(final int id) {
        List<NGTour_NGStation> tour_stations = SQLite.select(NGTour_NGStation_Table.stationId)
                .from(NGTour_NGStation.class)
                .where(NGTour_NGStation_Table.tourId.eq(id))
                .orderBy(NGTour_NGStation_Table.sortOrder, true)
                .queryList();
        if (tour_stations.size() > 0) {
            return Stream.of(tour_stations)
                    .map(t -> t.getStation()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGTour> getAllTours() {
        return Stream.of(SQLite.select()
                .from(NGTour.class)
                .queryList())
                .filter(value -> value != null).toList();
    }

    @Override public List<NGTour> getToursFromStation(final int stationId) {
        List<NGTour_NGStation> tour_stations = SQLite.select(NGTour_NGStation_Table.tourId)
                .from(NGTour_NGStation.class)
                .where(NGTour_NGStation_Table.stationId.eq(stationId))
                .queryList();
        if (tour_stations.size() > 0) {
            return Stream.of(tour_stations)
                    .map(t -> t.getTour()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override
    public List<NGStation> getStationsWithType(String type) {
        return Stream.of(SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.type.eq(type))
                .queryList())
                .filter(value -> value != null).toList();
    }

    @Override
    public List<NGStation> getStationsWithTypes(String type1, String type2, int flag2, int flag3) {
        return Stream.of(SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.type.eq(type1))
                .or(NGStation_Table.type.eq(type2))
                .and(NGStation_Table.flag2.eq(flag2))
                .and(NGStation_Table.flag3.eq(flag3))
                .orderBy(NGStation_Table.genericField, true)
                .queryList())
                .filter(value -> value != null).toList();
    }

    @Override
    public NGMap getRecentMap() {
        return SQLite.select()
                .from(NGMap.class)
                .orderBy(NGMap_Table.Id, false)
                .querySingle();
    }

    @Override
    public List<NGMap> getMaps() {
        return SQLite.select()
                .from(NGMap.class)
                .orderBy(NGMap_Table.Id, false)
                .queryList();
    }

    @Override
    public List<NGFile> getFilesOfType(NGFile.FileType file) {
        return SQLite.select()
                .from(NGFile.class)
                .where(NGFile_Table.attribute.eq(file.getName())).and(NGFile_Table.href.like("%.zip"))
                .queryList();
    }

    @Override
    public int getSortOrder(int tourId) {
        NGExhibition_NGTour tour = SQLite
                .select(NGExhibition_NGTour_Table.sortOrder)
                .from(NGExhibition_NGTour.class)
                .where(NGExhibition_NGTour_Table.tourId.eq(tourId))
                .querySingle();

        if (tour != null){
            return tour.getSortOrder();
        } else return 0;
    }

    @Override public List<NGTourActive> getTourActiveList() {
        return Stream.of(SQLite.select()
                .from(NGTourActive.class)
                .queryList()).filter(value -> value != null).toList();
    }

    @Override public List<NGTourActive> getTourActiveList(final int id) {
        return Stream.of(SQLite.select()
                .from(NGTourActive.class)
                .where(NGTourActive_Table.tourId.eq(id))
                .queryList()).filter(value -> value != null).toList();
    }

    @Override public NGTour getTour(int id) {
        return SQLite.select()
                .from(NGTour.class)
                .where(NGTour_Table.Id.eq(id))
                .querySingle();
    }

    public List<NGTour> getTours(final List<Integer> ids, List<String> types) {
        List<NGTour> tours;
        if (types != null && types.size() > 0){
            tours = SQLite.select()
                    .from(NGTour.class)
                    .where(NGTour_Table.Id.in(ids))
                    .and(NGTour_Table.type.in(types))
                    .queryList();
        } else {
            tours = SQLite.select()
                    .from(NGTour.class)
                    .where(NGTour_Table.Id.in(ids))
                    .queryList();
        }
        if (tours.size() > 0){
            return Stream.of(tours)
                    .sortBy(t -> t.getId()).toList();
        }
        return Collections.emptyList();
    }


    //STATION
    public NGStation getStation(final int id) {
        return SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.Id.eq(id)).querySingle();
    }

    public List<NGStation> getStations(final List<Integer> ids) {
        List<NGStation> stations =  SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.Id.in(ids))
                .queryList();
        if (stations.size() > 0){
            return Stream.of(stations)
                    .sortBy(st -> st.getId()).toList();
        }
        return Collections.emptyList();
    }

    public NGStation getStationByNumber(final int number) {
        return SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.number.eq(number)).querySingle();
    }

    public NGStation getStationByCode(final int code) {
        return SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.code.eq(Integer.toString(code))).querySingle();
    }

    @Override public List<NGContent> getStationContentList(final int id) {
        List<NGStation_NGContent> stationContents = SQLite.select(NGStation_NGContent_Table.contentId)
                .from(NGStation_NGContent.class)
                .where(NGStation_NGContent_Table.stationId.eq(id))
                .orderBy(NGStation_NGContent_Table.sortOrder, true)
                .queryList();
        if (stationContents.size() > 0) {
            return Stream.of(stationContents)
                    .sortBy(sc -> sc.getSortOrder())
                    .map(c -> c.getContent()).filter(value -> value != null).toList();
        }
        return Collections.emptyList();
    }

    @Override public List<NGFile> getStationFileList(final int id, String lang) {
        List<NGFile_NGStation> stationFiles = SQLite.select(NGFile_NGStation_Table.fileId,
                NGFile_NGStation_Table.sortOrder)
                .from(NGFile_NGStation.class)
                .where(NGFile_NGStation_Table.stationId.eq(id))
                .orderBy(NGFile_NGStation_Table.sortOrder, true)
                .queryList();
        if (stationFiles.size() > 0) {
            return Stream.of(stationFiles)
                    .map(f -> f.getFile())
                    .filter(f -> Strings.isNullOrEmpty(f.getLanguage()) ||
                            Strings.isNullOrEmpty(lang) ?
                            true : f.getLanguage().equals(lang))
                    .toList();
        }
        return Collections.emptyList();
    }

    //STATION
    public int getRandomStationId() {
        StringQuery query =
                new StringQuery(NGStation.class, "SELECT Id FROM Station Where type = '1' ORDER BY RANDOM() LIMIT 1;");
        return ((NGStation) query.queryList().get(0)).getId();
    }

    public List<NGStation> getChildStationsOfStation(@NonNull int id) {
        return Stream.of(SQLite.select()
                .from(NGStation.class)
                .where(NGStation_Table.parentId.eq(id))
                .queryList()).filter(value -> value != null).toList();
    }


    @Override
    public List<NGStation> searchStation(
            @NonNull String searchTerm, StationSearchFilter filter, String lang) {
        Where<NGStation> where = null;
        From<NGStation> query = SQLite.select()
                .from(NGStation.class);
        if (filter == StationSearchFilter.ALL || filter == StationSearchFilter.TITLE) {
            where = searchStationByTitle(searchTerm, lang, query);
        }
        if (filter == StationSearchFilter.ALL || filter == StationSearchFilter.ARTIST) {
            where = searchStationByArtist(searchTerm, where, query);
        }
        if (filter == StationSearchFilter.ALL || filter == StationSearchFilter.COUNTRY) {
            where = searchStationByCountry(searchTerm, where, query);
        }
        return Stream.of(where.queryList()).filter(value -> value != null).toList();
    }

    @NonNull public Where<NGStation> searchStationByCountry(
            @NonNull String searchTerm, Where<NGStation> where, From<NGStation> query) {
        List<Locale> allLocales = Arrays.asList(Locale.getAvailableLocales());
        List<String> locales = Stream.of(allLocales)
                .filter(value -> value.getDisplayCountry(Locale.ENGLISH)
                        .toLowerCase().contains(searchTerm.toLowerCase()))
                .map(locale -> locale.getCountry().toLowerCase()).distinct().toList();
        for (String locale : locales) {
            if (where != null) {
                where.or(NGStation_Table.choice1.like(locale));
            } else {
                where = query.where(NGStation_Table.choice1.like(locale));
            }
        }
        return where;
    }

    @NonNull public Where<NGStation> searchStationByArtist(
            @NonNull String searchTerm, Where<NGStation> where, From<NGStation> query) {
        if (where != null) {
            where.or(NGStation_Table.inventoryNumber.like(makeLikeString(searchTerm)));
        } else {
            where = query.where(NGStation_Table.inventoryNumber.like(makeLikeString(searchTerm)));
        }
        where.or(NGStation_Table.inventoryNumberNormalized.like(makeLikeString(searchTerm)));
        where.or(NGStation_Table.creatorDeath.like(makeLikeString(searchTerm)));
        where.or(NGStation_Table.creatorDeathNormalized.like(makeLikeString(searchTerm)));
        return where;
    }

    @NonNull public Where<NGStation> searchStationByTitle(
            @NonNull String searchTerm, String lang, From<NGStation> query) {
        Where<NGStation> where;
        Where<NGInternationalText> titleWhere = SQLite.select(NGInternationalText_Table.Id)
                .from(NGInternationalText.class)
                .where(NGInternationalText_Table.text.like(makeLikeString(searchTerm)))
                .or(NGInternationalText_Table.textNormalized.like(makeLikeString(searchTerm)))
                .and(NGInternationalText_Table.language.eq(lang));
        where = query.where(NGStation_Table.titleId.in(titleWhere));
        return where;
    }

    private String makeLikeString(String term) {
        return "%" + term + "%";
    }


    @Nullable @Override public NG3DObject get3DObject(@NonNull int stationId) {
        return SQLite.select()
                .from(NG3DObject.class)
                .where(NG3DObject_Table.stationId.eq(stationId))
                .querySingle();
    }

    //Loading content files - images are ALWAYS returned, no matter which language
    public List<NGFile> getContentFiles(@NonNull final int contentId, final String lang) {
        List<NGFile_NGContent> ngFile_ngContents = SQLite.select(NGFile_NGContent_Table.fileId)
                .from(NGFile_NGContent.class)
                .where(NGFile_NGContent_Table.contentId.eq(contentId))
                .orderBy(NGFile_NGContent_Table.sortOrder, true)
                .queryList();
        if (ngFile_ngContents.size() > 0) {
            return Stream.of(ngFile_ngContents)
                    .map(ngFile_ngContent -> ngFile_ngContent.getFile())
                    .filter(f -> f.getAttribute().equals("image") ||
                    Strings.isNullOrEmpty(f.getLanguage()) ||
                            Strings.isNullOrEmpty(lang) ?
                            true : f.getLanguage().equals(lang))
                    .filter(value -> value != null)
                    .toList();
        }
        return Collections.emptyList();
    }

    @Override public NGFile getFile(@NonNull int fileId) {
        return SQLite.select()
                .from(NGFile.class)
                .where(NGFile_Table.Id.eq(fileId))
                .querySingle();
    }

    //CONTENT Games
    public List<NGGame> getContentGames(@NonNull final int contentId,
                                        final String lang,
                                        final boolean loadReferences) {
        List<NGContent_NGGame> ngGame_ngContents = SQLite.select(NGContent_NGGame_Table.gameId)
                .from(NGContent_NGGame.class)
                .where(NGContent_NGGame_Table.contentId.eq(contentId))
                .orderBy(NGContent_NGGame_Table.sortOrder, true)
                .queryList();
        if (ngGame_ngContents.size() > 0) {
            List<NGGame> games = Stream.of(ngGame_ngContents)
                    .map(ngGame_ngContent -> ngGame_ngContent.getGame())
                    .filter(value -> value != null)
                    .toList();
            if (lang != null && loadReferences){
                for(NGGame g : games){
                    g.loadReferences(lang);
                }
            }
            return games;
        }
        return Collections.emptyList();
    }

    /**
     * Get the config file for a game
     * @param gameId
     * @param lang: Preferred language of the config file.
     *            If not available, default language will be used.
     * @return NGFile if found, else null
     */
    @Override public NGFile getGameConfigFile(int gameId, String lang){
        List<NGFile_NGGame> ngFile_ngGames = SQLite.select()
                .from(NGFile_NGGame.class)
                .where(NGFile_NGGame_Table.gameId.eq(gameId))
                .queryList();

        List<NGFile> gameConfigs = Stream.of(ngFile_ngGames)
                .map(f -> f.getFile())
                .filter(f -> f != null)
                .filter(f -> f.getLanguage().equals(lang))
                .toList();

        if (gameConfigs != null && gameConfigs.size() > 0){
            return gameConfigs.get(0);
        } else if (!Statics.DEFAULT_LANGUAGE.equals(lang)) {
            return getGameConfigFile(gameId, Statics.DEFAULT_LANGUAGE);
        } else {
            return null;
        }
    }



    //RAW

    @Override public <T extends NGBaseModel> List<T> rawQuery(
            @NonNull Class responseClass, @NonNull String sql) {
        StringQuery query = new StringQuery(responseClass, sql);
        return Stream.of(query.queryList()).filter(value -> value != null).toList();
    }


    @Override public Integer getVersion() {
        NGVersion version = SQLite.select()
                .from(NGVersion.class)
                .querySingle();
        if (version == null) {
            return 0;
        }
        return version.getVersion();
    }
}
