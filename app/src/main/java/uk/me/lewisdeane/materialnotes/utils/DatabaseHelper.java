package uk.me.lewisdeane.materialnotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.databases.Database;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

import static uk.me.lewisdeane.materialnotes.activities.MainActivity.NoteMode;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.PATH;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.loadNotes;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mActionBarFragment;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mDeletedNotes;
import static uk.me.lewisdeane.materialnotes.activities.MainActivity.mNoteMode;


/**
 * Created by Lewis on 15/08/2014.
 */
public class DatabaseHelper {

    // Context to open database from.
    private Context mContext;

    // Create a database reference that we use to create readable and writeable databases from.
    private Database mDatabase;

    // Create a read able and write able database reference
    private SQLiteDatabase mReadableDatabase, mWriteableDatabase;

    // Create an enum associating the database columns with the title
    private enum Columns {
        PATH("PATH", 0), FOLDER("FOLDER", 1), TITLE("TITLE", 2), ITEM("ITEM", 3), TIME("TIME", 4), DATE("DATE", 5), LINK("LINK", 6), LAST_MODIFIED("LAST_MODIFIED", 7), ARCHIVE("ARCHIVE", 8);

        private final String title;
        private final int columnPos;

        private Columns(String _title, int _columnPos) {
            this.title = _title;
            this.columnPos = _columnPos;
        }
    }

    /*
    Constructor required.
      */
    public DatabaseHelper(Context _context) {
        this.mContext = _context;
    }

    public DatabaseHelper() {
    }


    /*
    Adds notes to the database with attributes from NoteItem object.

    @param - Check whether these notes are to be archived or not, array of note items to add.
    @return - null
     */
    public void addNoteToDatabase(boolean _shouldArchive, NoteItem _noteItem) {
        // Open a new write able and readable database
        open();

        // Adds a new row to the chosen database for each note item passed in.
        mWriteableDatabase.insert(Database.NOTE_TABLE, null, getContentVals(_shouldArchive, _noteItem));
        Log.i("KK", "Added - " + _noteItem.getPath());

        loadNotes();

        // Close every instance associated with the database.
        close();
    }


    /*
    Edits a note to the database.

    @param - The old note to replace, the new note to replace it.
    @return - null
     */
    public void editNoteToDatabase(NoteItem _oldNoteItem, NoteItem _newNoteItem) {
        // Open a new write able and readable database.
        open();

        // Update the record with the new data.
        mWriteableDatabase.update(Database.NOTE_TABLE, getContentVals(false, _newNoteItem), "PATH=? AND TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _oldNoteItem.getLastModified(), new String[]{_oldNoteItem.getPath(), _oldNoteItem.getTitle(), _oldNoteItem.getItem()});

        // Close all instances of the databases.
        close();
    }


    /*
    Fetches a list of notes to show.

    @param - null
    @return - returns an array list of NoteItems which are to be shown.
     */
    public ArrayList<NoteItem> getNotesFromDatabase() {

        // ArrayList storing the notes to be returned.
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();

        // Open a new write able and readable database.
        open();

        Cursor cursor = mReadableDatabase.rawQuery("SELECT * FROM " + Database.NOTE_TABLE, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                noteItems.add(getNoteItem(cursor));
                Log.i("", cursor.getString(0));
            }
            while (cursor.moveToNext());
        }

        // Close the database references.
        close();

        // Returns the filled list of notes that goes on to populate list view.
        return noteItems;
    }

    public void deleteNoteFromDatabase(NoteItem _noteItem) {
        mDeletedNotes.clear();
        open();
        mWriteableDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
        mDeletedNotes.add(_noteItem);
        if (mNoteMode == NoteMode.EVERYTHING)
            // mWriteableDatabase.insert(Database.NOTE_TABLE, null, getContentVals(true, _noteItem));
        close();
        loadNotes();
        MainActivity ma = (MainActivity) mContext;
        ma.createSnackbar(_noteItem.getTitle());
    }

    /*
    public void addNoteToDatabase(NoteItem _oldNoteItem, NoteItem _newNoteItem) {
        // Add a new write able database to stack.
        open("W");

        // If there is an old NoteItem, delete it.
        if (_oldNoteItem != null)
            mSQLiteDatabaseStack.peek().delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _oldNoteItem.getLastModified(), new String[]{_oldNoteItem.getTitle(), _oldNoteItem.getItem()});


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
        if (NOTE_MODE == NoteMode.EVERYTHING && !isInDB(_noteItem))
            mSQLiteDatabaseStack.peek().insert(Database.ARCHIVE_TABLE, null, getContentVals(_noteItem));
        close();
        loadNotes();
        MainActivity ma = (MainActivity) mContext;
        ma.createSnackbar(_noteItem.getTitle());
    }
*/

    public void deleteFolderFromDatabase(NoteItem _noteItem) {
        deleteNoteFromDatabase(_noteItem);
        deleteSubItems(_noteItem);
    }


    private void deleteSubItems(NoteItem _noteItem) {
        open();

        Cursor cursor = mReadableDatabase.rawQuery("SELECT * FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getCorrectedPath(_noteItem) + "%'", null);
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

                mWriteableDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + noteItem.getLastModified(), new String[]{noteItem.getTitle(), noteItem.getItem()});
                mDeletedNotes.add(noteItem);

                // if (mNoteMode == NoteMode.EVERYTHING)
                //    mWriteableDatabase.insert(Database.ARCHIVE_TABLE, null, getContentVals(noteItem));

            } while (cursor.moveToNext());
        }

        cursor.close();
        close();
    }

    /*

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
*/
    public void deleteArchiveDatabase() {
        open();
        CustomDialog.Builder builder = new CustomDialog.Builder(this.mContext, this.mContext.getString(R.string.dialog_delete_title), this.mContext.getString(R.string.dialog_delete_confirm));
        builder.content(this.mContext.getString(R.string.dialog_delete_content));
        builder.negativeText(this.mContext.getString(R.string.dialog_delete_cancel));
        builder.positiveColor("#4285F4");

        CustomDialog customDialog = builder.build();
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                mWriteableDatabase.delete(Database.NOTE_TABLE, "ARCHIVE=?", new String[]{"true"});
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

    /*
    private boolean isInDB(NoteItem _noteItem){
        open("R");

        Cursor cursor = mSQLiteDatabaseStack.peek().rawQuery("SELECT * FROM " + Database.ARCHIVE_TABLE + " WHERE TITLE=? AND PATH=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getPath()});
        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0)
            return true;
        return false;
    }

    */

    private ContentValues getContentVals(boolean _shouldArchive, NoteItem _noteItem) {
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
        contentValues.put("ARCHIVE", _shouldArchive + "");

        return contentValues;
    }


    /*
    Opens a new database and subsequent readable and writeable references.

    @param - null
    @return - null
     */
    private void open() {
        mDatabase = new Database(this.mContext);
        mReadableDatabase = mDatabase.getReadableDatabase();
        mWriteableDatabase = mDatabase.getWritableDatabase();
    }

    /*
    Closes everything to do with the database and can close any cursors if needed.

    @param - can be left empty or pass in any cursors to close.
    @return - null
     */
    private void close(Cursor... _cursors) {
        for (Cursor cursor : _cursors) {
            cursor.close();
        }
        mReadableDatabase.close();
        mWriteableDatabase.close();
        mDatabase.close();
    }

    public String getSubItems(NoteItem _noteItem) {
        String subItems = "";
        open();
        Cursor cursor = mReadableDatabase.rawQuery("SELECT TITLE, PATH FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getCorrectedPath(_noteItem) + "%' " + Sorting.mSortMode, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                if (cursor.getString(1).startsWith(_noteItem.getPath()) && Misc.getOccurences(cursor.getString(1), "/") - 1 == Misc.getOccurences(_noteItem.getPath(), "/"))
                    subItems += cursor.getString(0) + ", ";
            } while (cursor.moveToNext());
        }

        close(cursor);

        return subItems.length() == 0 ? this.mContext.getString(R.string.no_subitems) : subItems.substring(0, subItems.length() - 2);
    }

    private String getCorrectedPath(NoteItem _noteItem) {
        String temp = _noteItem.getPath() + "/";

        for (int i = 0; i < temp.length(); i++) {
            if ((temp.charAt(i) + "").equals("'") && !(temp.charAt(i - 1) + "").equals("\'"))
                temp = temp.substring(0, i) + '\'' + temp.substring(i);
        }
        return temp;
    }

    public String getPrevPath(String _path) {
        for (int i = _path.length() - 2; i >= 0; i--) {
            if ((_path.charAt(i) + "").equals("/"))
                return _path.substring(0, i + 1);
        }
        return "/";
    }

    /*
    Returns the query for searching for notes.

    @param - null
    @return - The query string to use.
     */
    private String getQuery() {
        StringBuilder builder = new StringBuilder("SELECT * FROM " + Database.NOTE_TABLE + " ");

        String search = mActionBarFragment.mSearchBox.getText().toString().trim();

        if (search.length() > 0) {
            builder.append("WHERE TITLE LIKE '%" + search + "%' ");
            if (mNoteMode.equals(NoteMode.EVERYTHING))
                builder.append("AND PATH LIKE '" + PATH + "%' ");
        } else if (mNoteMode.equals(NoteMode.EVERYTHING)) {
            builder.append("WHERE PATH LIKE '" + PATH + "%' ");
        }

        builder.append(Sorting.mSortMode);

        return builder.toString();
    }

    private int getColumnPos(String _title) {
        for (Columns column : Columns.values()) {
            if (column.title.equals(_title))
                return column.columnPos;
        }
        return 0;
    }

    public void updateDatabase(Database database) {
        mDatabase = database;
        mReadableDatabase = mDatabase.getReadableDatabase();
        mWriteableDatabase = mDatabase.getWritableDatabase();

        Cursor cursor = mReadableDatabase.rawQuery("SELECT * FROM " + Database.NOTE_TABLE, null);

        if (cursor != null && cursor.getColumnCount() > 0 && cursor.moveToFirst()) {
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                NoteItem noteItem = getNoteItem(cursor);
                mWriteableDatabase.insert(Database.NOTE_TABLE, null, getContentVals(false, noteItem));
            }
        }

        mWriteableDatabase.execSQL("DROP TABLE IF EXISTS ARCHIVE_TABLE");

        close(cursor);
    }

    private NoteItem getNoteItem(Cursor _cursor) {
        NoteItem.Builder builder = new NoteItem.Builder(_cursor.getString(getColumnPos("PATH")), _cursor.getString(getColumnPos("FOLDER")).equals("true"), _cursor.getString(getColumnPos("TITLE")));
        builder.item(_cursor.getString(getColumnPos("ITEM")))
                .time(_cursor.getString(getColumnPos("TIME")))
                .date(_cursor.getString(getColumnPos("DATE")))
                .link(_cursor.getString(getColumnPos("LINK")))
                .lastModified(_cursor.getLong(getColumnPos("LAST_MODIFIED")));

        return builder.build();
    }
}
