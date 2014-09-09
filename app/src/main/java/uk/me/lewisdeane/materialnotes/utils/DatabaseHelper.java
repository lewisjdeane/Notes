package uk.me.lewisdeane.materialnotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Stack;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.databases.Database;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NOTE_MODE;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.PATH;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadNotes;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mDeletedNotes;

/**
 * Created by Lewis on 15/08/2014.
 */
public class DatabaseHelper {

    // Context to open database from.
    private static Context mContext;

    // Create a stack of databases making it easy to open and close them.
    private static Stack<SQLiteDatabase> mSQLiteDatabaseStack = new Stack<SQLiteDatabase>();
    private static Stack<Database> mDatabaseStack = new Stack<Database>();


    // Constructor
    public DatabaseHelper(Context _context) {
        mContext = _context;
    }

    // Called from NoteItem, adds the current NoteItem to the database.
    public void addNoteToDatabase(NoteItem _oldNoteItem, NoteItem _newNoteItem) {
        // Add a new writeable database to stack.
        open("W");

        // If there is an old NoteItem, delete it.
        if (_oldNoteItem != null)
            mSQLiteDatabaseStack.peek().delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _oldNoteItem.getLastModified(), new String[]{_oldNoteItem.getTitle(), _oldNoteItem.getItem()});


        /*
        SORT OUT EDITING AND RESTORING SUB NOTES
         */

        // Add the new NoteItem to the database
        mSQLiteDatabaseStack.peek().insert(Database.NOTE_TABLE, null, getContentVals(_newNoteItem));

        // Clear the database stack
        close();
    }

    public void deleteNoteFromDatabase(NoteItem _noteItem) {
        mDeletedNotes.clear();
        open("W");
        mSQLiteDatabaseStack.peek().delete(NOTE_MODE == NoteMode.EVERYTHING ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
        mDeletedNotes.add(_noteItem);
        if (NOTE_MODE == NoteMode.EVERYTHING)
            mSQLiteDatabaseStack.peek().insert(Database.ARCHIVE_TABLE, null, getContentVals(_noteItem));
        close();
        loadNotes();
        MainActivity ma = (MainActivity) mContext;
        ma.createSnackbar(_noteItem.getTitle());
    }

    public void deleteFolderFromDatabase(NoteItem _noteItem) {
        deleteNoteFromDatabase(_noteItem);
        deleteSubItems(_noteItem);
    }

    private void deleteSubItems(NoteItem _noteItem) {
        open("R");

        String table = NOTE_MODE != NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        Cursor cursor = mSQLiteDatabaseStack.peek().rawQuery("SELECT * FROM " + table + " WHERE PATH LIKE '" + getTempPath(_noteItem) + "%'", null);
        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                NoteItem.Builder builder = new NoteItem.Builder(cursor.getString(0), cursor.getString(1).equals("true"), cursor.getString(2));
                builder.item(cursor.getString(3))
                        .time(cursor.getString(4))
                        .date(cursor.getString(5))
                        .link(cursor.getString(6))
                        .lastModified(cursor.getLong(7));
                NoteItem noteItem = builder.build();

                mSQLiteDatabaseStack.peek().delete(table, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + noteItem.getLastModified(), new String[]{noteItem.getTitle(), noteItem.getItem()});
                mDeletedNotes.add(noteItem);
                if (NOTE_MODE == NoteMode.EVERYTHING)
                    mSQLiteDatabaseStack.peek().insert(Database.ARCHIVE_TABLE, null, getContentVals(noteItem));
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();
    }

    public static ArrayList<NoteItem> getNotesFromDatabase(String _search) {
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        open("R");

        String table = NOTE_MODE != NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        String query = getQuery(NOTE_MODE, table, _search);

        Cursor cursor = mSQLiteDatabaseStack.peek().rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                NoteItem.Builder builder = new NoteItem.Builder(cursor.getString(0), cursor.getString(1).equals("true"), cursor.getString(2));
                builder.item(cursor.getString(3))
                        .time(cursor.getString(4))
                        .date(cursor.getString(5))
                        .link(cursor.getString(6))
                        .lastModified(cursor.getLong(7));

                if (getPrevPath(cursor.getString(0)).equals(PATH) || !NOTE_MODE.equals(NoteMode.EVERYTHING) || _search.length() > 0)
                    noteItems.add(builder.build());

            } while (cursor.moveToNext());
        }

        // Remove items without times or dates
        if (NOTE_MODE == NoteMode.UPCOMING) {
            for (int i = 0; i < noteItems.size(); i++) {
                if (noteItems.get(i).getTime().length() == 0 && noteItems.get(i).getDate().length() == 0) {
                    noteItems.remove(i);
                    i--;
                }
            }
        }

        close();
        return noteItems;
    }

    public void deleteArchiveDatabase() {
        open("W");
        CustomDialog.Builder builder = new CustomDialog.Builder(mContext, mContext.getString(R.string.dialog_delete_title), mContext.getString(R.string.dialog_delete_confirm));
        builder.content(mContext.getString(R.string.dialog_delete_content));
        builder.negativeText(mContext.getString(R.string.dialog_delete_cancel));
        builder.positiveColor("#4285F4");

        CustomDialog customDialog = builder.build();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                mSQLiteDatabaseStack.peek().delete(Database.ARCHIVE_TABLE, null, null);
                loadNotes();
                close();
            }

            @Override
            public void onCancelClick() {
                close();
            }
        });
        customDialog.show();
    }

    private ContentValues getContentVals(NoteItem _noteItem) {
        // Creates ContentValues from the NoteItem.
        ContentValues contentValues = new ContentValues();

        String path = _noteItem.getPath() != null ? _noteItem.getPath() : PATH + _noteItem.getTitle();

        contentValues.put("PATH", path);
        contentValues.put("FOLDER", _noteItem.getIsFolder() ? "true" : "false");
        contentValues.put("TITLE", _noteItem.getTitle());
        contentValues.put("ITEM", _noteItem.getItem());
        contentValues.put("TIME", _noteItem.getTime());
        contentValues.put("DATE", _noteItem.getDate());
        contentValues.put("LINK", _noteItem.getLink());
        contentValues.put("LAST_MODIFIED", _noteItem.getLastModified());

        return contentValues;
    }

    private static void open(String... _mode) {
        for (String mode : _mode) {
            mDatabaseStack.add(new Database(mContext));
            mSQLiteDatabaseStack.add(mode.equals("W") ? mDatabaseStack.peek().getWritableDatabase() : mDatabaseStack.peek().getReadableDatabase());
        }
    }

    private static void close() {
        for (int i = 0; i < mDatabaseStack.size(); i++) {
            mSQLiteDatabaseStack.pop().close();
            mDatabaseStack.pop().close();
        }
    }

    public static String getSubItems(NoteItem _noteItem) {
        String subItems = "";
        open("R");

        String table = NOTE_MODE == NoteMode.EVERYTHING ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        Cursor cursor = mSQLiteDatabaseStack.peek().rawQuery("SELECT TITLE, PATH FROM " + table + " WHERE PATH LIKE '" + getTempPath(_noteItem) + "%' " + Sorting.mSortMode, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getString(1).startsWith(_noteItem.getPath()) && Misc.getOccurences(cursor.getString(1), "/") - 1 == Misc.getOccurences(_noteItem.getPath(), "/"))
                    subItems += cursor.getString(0) + ", ";
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return subItems.length() == 0 ? mContext.getString(R.string.no_subitems) : subItems.substring(0, subItems.length() - 2);
    }

    private static String getTempPath(NoteItem _noteItem) {
        String temp = _noteItem.getPath() + "/";

        for (int i = 0; i < temp.length(); i++) {
            if ((temp.charAt(i) + "").equals("'") && !(temp.charAt(i - 1) + "").equals("\'"))
                temp = temp.substring(0, i) + '\'' + temp.substring(i);
        }
        return temp;
    }

    public static String getPrevPath(String _path) {
        for (int i = _path.length() - 2; i >= 0; i--) {
            if ((_path.charAt(i) + "").equals("/"))
                return _path.substring(0, i + 1);
        }
        return "/";
    }

    private static String getQuery(NoteMode _noteMode, String _table, String _search){
        StringBuilder builder = new StringBuilder("SELECT * FROM " + _table + " ");

        if(_search.length() > 0) {
            builder.append("WHERE TITLE LIKE '%" + _search + "%' ");
            if(_noteMode.equals(NoteMode.EVERYTHING))
                builder.append("AND PATH LIKE '" + PATH + "%' ");
        } else if(_noteMode.equals(NoteMode.EVERYTHING)){
            builder.append("WHERE PATH LIKE '" + PATH + "%' ");
        }

        builder.append(Sorting.mSortMode);

        return builder.toString();
    }
}
