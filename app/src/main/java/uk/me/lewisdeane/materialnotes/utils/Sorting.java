package uk.me.lewisdeane.materialnotes.utils;

/**
 * Created by Lewis on 03/09/2014.
 */
public class Sorting {

    // Empty constructor to prevent instantiation.
    private Sorting() {
    }

    // Define our possible sorting modes
    public static enum SORT_MODE {
        FOLDER_FIRST_ALPHABETICAL("FOLDER DESC, TITLE ASC"),
        FOLDER_LAST_ALPHABETICAL("FOLDER ASC, TITLE ASC"),
        FOLDER_FIRST_REVERSE_ALPHABETICAL("FOLDER DESC, TITLE DESC"),
        FOLDER_LAST_REVERSE_ALPHABETICAL("FOLDER ASC, TITLE DESC"),
        FOLDER_FIRST_RECENT("FOLDER DESC, LAST_MODIFIED DESC"),
        FOLDER_LAST_RECENT("FOLDER ASC, LAST_MODIFIED DESC");

        private final String mSort;

        private SORT_MODE(String _sort) {
            this.mSort = "ORDER BY " + _sort;
        }
    }

    // Our sorting mode chosen from preferences.
    public static String mSortMode = getSortMode();

    // Gets the Sorting Mode from preferences.
    private static String getSortMode(){
        return SORT_MODE.FOLDER_FIRST_ALPHABETICAL.mSort;
    }
}
