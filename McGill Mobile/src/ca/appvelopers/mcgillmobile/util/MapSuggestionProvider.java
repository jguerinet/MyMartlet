package ca.appvelopers.mcgillmobile.util;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SearchRecentSuggestionsProvider;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import ca.appvelopers.mcgillmobile.App;
import ca.appvelopers.mcgillmobile.object.Place;

/**
 * Created by Quang on 9/27/2014.
 */
public class MapSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "ca.appvelopers.mcgillmobile.util.MapSuggestionProvider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/locations");
    private List<Place> places;
    private ArrayList<Integer> levenshteinDistances;
    private HashMap<Integer, Place> distanceMap;
    private static final int SEARCH_SUGGEST = 1;
    private static final UriMatcher uriMatcher;
    private static final String[] SEARCH_SUGGEST_COLUMNS = {
            SearchManager.SUGGEST_COLUMN_TEXT_1,
            SearchManager.SUGGEST_COLUMN_INTENT_DATA
    };
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SEARCH_SUGGEST);
        uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY + "/*", SEARCH_SUGGEST);
    }
    public MapSuggestionProvider() {
        setupSuggestions(AUTHORITY, SearchRecentSuggestionsProvider.DATABASE_MODE_QUERIES);
    }

    @Override
    public boolean onCreate() {
        places = App.getPlaces();
        distanceMap = new HashMap<Integer, Place>();
        levenshteinDistances = new ArrayList<Integer>();
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        switch (uriMatcher.match(uri)) {
            case SEARCH_SUGGEST:
                String query = uri.getLastPathSegment().toLowerCase();
                if (SearchManager.SUGGEST_URI_PATH_QUERY.equals(query)) {
                    return null;
                }
                computeLevenshteinDistances(query);
                MatrixCursor cursor = new MatrixCursor(SEARCH_SUGGEST_COLUMNS);
                for (int i = 0; i < 3; i++) {
                    cursor.addRow(new Object[] {
                            distanceMap.get(levenshteinDistances.get(i)).getName(),  distanceMap.get(levenshteinDistances.get(i)).getName()
                    });
                }

                return cursor;
            default:
                throw new IllegalArgumentException("Unknown Uri: " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }

    private void computeLevenshteinDistances(String query) {
        for (Place place : places) {
            Integer distance = levenshteinDistance(query, place.getName().toLowerCase());
            distanceMap.put(distance, place);
            levenshteinDistances.add(distance);
        }
        Collections.sort(levenshteinDistances);
    }
    private int levenshteinDistance(String query,String placeName) {
        int[][] distance = new int[query.length() + 1][placeName.length() + 1];

        for (int i = 0; i <= query.length(); i++)
            distance[i][0] = i;
        for (int j = 1; j <= placeName.length(); j++)
            distance[0][j] = j;

        for (int i = 1; i <= query.length(); i++)
            for (int j = 1; j <= placeName.length(); j++)
                distance[i][j] = minimum(
                        distance[i - 1][j] + 1,
                        distance[i][j - 1] + 1,
                        distance[i - 1][j - 1] + ((query.charAt(i - 1) == placeName.charAt(j - 1)) ? 0 : 1));

        return distance[query.length()][placeName.length()];
    }
    private int minimum(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }
}
