package uk.me.lewisdeane.materialnotes.databases;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Lewis on 19/07/2014.
 */
public class Database extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 6;

    public static final String NOTE_TABLE = "NoteTable";
    public static final String ARCHIVE_TABLE = "ArchiveTable";

    private static final String NOTE_TABLE_CREATE = "create table " + NOTE_TABLE
            + " (PATH TEXT, FOLDER TEXT, TITLE TEXT, ITEM TEXT, TIME TEXT, DATE TEXT, TAGS TEXT, LINK TEXT, LAST_MODIFIED LONG);";

    private static final String ARCHIVE_TABLE_CREATE = "create table " + ARCHIVE_TABLE
            + " (PATH TEXT, FOLDER TEXT, TITLE TEXT, ITEM TEXT, TIME TEXT, DATE TEXT, TAGS TEXT, LINK TEXT, LAST_MODIFIED LONG);";

    public Database(Context context) {
        super(context, "Database", null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(NOTE_TABLE_CREATE);
        db.execSQL(ARCHIVE_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ARCHIVE_TABLE);
        this.onCreate(db);
    }

}
