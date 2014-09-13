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
    private Stack<Database> mDatabases;

    // Create a read able and write able database reference
    private Stack<SQLiteDatabase> mReadableDatabases, mWriteableDatabases;

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
        getTopWriteable().insert(Database.NOTE_TABLE, null, getContentVals(_shouldArchive, _noteItem));

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
        getTopWriteable().update(Database.NOTE_TABLE, getContentVals(false, _newNoteItem), "PATH=? AND TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _oldNoteItem.getLastModified(), new String[]{_oldNoteItem.getPath(), _oldNoteItem.getTitle(), _oldNoteItem.getItem()});

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

        Cursor cursor = getTopReadable().rawQuery("SELECT * FROM " + Database.NOTE_TABLE, null);

        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            cursor.moveToFirst();
            do {
                // If the note doesn't have time or date when in upcoming mode, do not add it to our arraylist.
                NoteItem noteItem = getNoteItem(cursor);
                if(!(noteItem.getTime().length() == 0 && noteItem.getDate().length() == 0 && mNoteMode == NoteMode.UPCOMING))
                    noteItems.add(getNoteItem(cursor));
            }
            while (cursor.moveToNext());
        }

        // Close the database references.
        close();

        // Returns the filled list of notes that goes on to populate list view.
        return noteItems;
    }


    /*
    Deletes the NoteItem passed in from the database.

    @param - The note to be deleted.
    @return - null
     */
    public void deleteNoteFromDatabase(NoteItem _noteItem) {
        // Clear the list of previously deleted notes.
        mDeletedNotes.clear();

        // Add note to deleted note list which is used when undoing delete.
        mDeletedNotes.add(_noteItem);

        // Initialise reference to database things.
        open();

        // Update the record if note should be archived, otherwise delete the record.
        if (mNoteMode != NoteMode.ARCHIVE) {
            getTopWriteable().update(Database.NOTE_TABLE, getContentVals(true, _noteItem), "PATH=? AND TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getPath(), _noteItem.getTitle(), _noteItem.getItem()});
        } else {
            getTopWriteable().delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
        }

        // Close things associated with database.
        close();

        // Reload notes from database with correct data and create a snack bar to allow undoing.
        loadNotes();
        MainActivity ma = (MainActivity) mContext;
        ma.createSnackbar(_noteItem.getTitle());
    }


    /*
    Deletes a folder item from the database, separate method used as sub items may need to be deleted.

    @param - Folder to be deleted.
    @return - null
     */
    public void deleteFolderFromDatabase(NoteItem _noteItem) {
        // Deletes the folder note from the database
        deleteNoteFromDatabase(_noteItem);

        // Deletes sub items from database.
        deleteSubItems(_noteItem);
    }


    /*
    Deletes sub items of the passed in note from the database.

    @param - Note whose sub items should be deleted.
    @return - null.
     */
    private void deleteSubItems(NoteItem _noteItem) {
        // Open the database objects.
        open();

        // Query the database for notes that match the path start of passed in note.
        Cursor cursor = getTopReadable().rawQuery("SELECT * FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getCorrectedPath(_noteItem) + "%'", null);

        // If there exists rows in the database that match criteria delete them/update them.
        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {

            // Move to first row of records/
            cursor.moveToFirst();
            do {
                // Build a note item from the data provided in the cursor.
                NoteItem noteItem = getNoteItem(cursor);

                // Delete it based on note type.
                if (noteItem.getIsFolder())
                    deleteFolderFromDatabase(noteItem);
                else
                    deleteNoteFromDatabase(noteItem);

            } while (cursor.moveToNext());
        }

        // Remove references to database from stack.
        close(cursor);
    }


    /*
    Creates a dialog asking whether all archived notes should be deleted, called when FAB clicked in archive mode.

    @param - null.
    @return - null.
     */
    public void deleteArchiveDatabase() {

        // Build a dialog with specified options.
        CustomDialog.Builder builder = new CustomDialog.Builder(this.mContext, this.mContext.getString(R.string.dialog_delete_title), this.mContext.getString(R.string.dialog_delete_confirm));
        builder.content(this.mContext.getString(R.string.dialog_delete_content));
        builder.negativeText(this.mContext.getString(R.string.dialog_delete_cancel));
        builder.positiveColor("#4285F4");

        // Apply properties to created dialog.
        CustomDialog customDialog = builder.build();
        customDialog.setCancelable(false);
        customDialog.setCanceledOnTouchOutside(false);
        customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
                // Open database references
                open();

                // Delete all notes where archive field contains true.
                getTopWriteable().delete(Database.NOTE_TABLE, "ARCHIVE=?", new String[]{"true"});

                // Reload notes from database.
                loadNotes();

                // Close database references to prevent memory leaks.
                close();
            }

            @Override
            public void onCancelClick() {

            }
        });

        // Show dialog.
        customDialog.show();
    }


    /*
    Method that checks whether the specified note exists in the database.

    @param - boolean containing which mode to search for note in, Note to check if exists.
    @return - boolean containing whether or not the note was found in database.
    */
    private boolean isInDB(boolean _archiveMode, NoteItem _noteItem){
        // Open references to database.
        open();

        // Check to see if note exists in database.
        Cursor cursor = getTopReadable().rawQuery("SELECT * FROM " + Database.NOTE_TABLE + " WHERE TITLE=? AND PATH=? AND ARCHIVE=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getPath(), _archiveMode+""});

        // If results are returned then return true, otherwise return false.
        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0)
            return true;

        return false;
    }


    /*
    Gets the content values from the passed in note.

    @param - boolean containing whether or not to put in archive, note to be added.
    @return - Content values of the note.
     */
    private ContentValues getContentVals(boolean _shouldArchive, NoteItem _noteItem) {
        // Creates a new ContentValues from the NoteItem.
        ContentValues contentValues = new ContentValues();

        // Get the path of the note to be added.
        String path = _noteItem.getPath() != null ? _noteItem.getPath() : PATH + _noteItem.getTitle();

        // Set appropriate fields.
        contentValues.put("PATH", path);
        contentValues.put("FOLDER", _noteItem.getIsFolder() ? "true" : "false");
        contentValues.put("TITLE", _noteItem.getTitle());
        contentValues.put("ITEM", _noteItem.getItem());
        contentValues.put("TIME", _noteItem.getTime());
        contentValues.put("DATE", _noteItem.getDate());
        contentValues.put("LINK", _noteItem.getLink());
        contentValues.put("LAST_MODIFIED", _noteItem.getLastModified());
        contentValues.put("ARCHIVE", _shouldArchive + "");

        // Return the content values.
        return contentValues;
    }


    /*
    Opens a new database and subsequent readable and writeable references.

    @param - Database can be passed in to reference database object from.
    @return - null
     */
    private void open(Database... _databases) {
        // If param is null create and add a new database to the stack, otherwise reference database from parameter
        if (_databases == null)
            mDatabases.add(new Database(this.mContext));
        else
            mDatabases.add(_databases[0]);

        // Add new readable and writeable databases to the stack.
        mReadableDatabases.add(getTopDatabase().getReadableDatabase());
        mWriteableDatabases.add(getTopDatabase().getWritableDatabase());
    }

    /*
    Closes everything to do with the database and can close any cursors if needed.

    @param - can be left empty or pass in any cursors to close.
    @return - null
     */
    private void close(Cursor... _cursors) {
        // Loop through param and close and cursors passed in.
        for (Cursor cursor : _cursors) {
            cursor.close();
        }

        // Remove top item from each stack and close each one.
        mReadableDatabases.pop().close();
        mWriteableDatabases.pop().close();
        mDatabases.pop().close();
    }


    /*
    Gets all sub items from the passed in note.

    @param - Note to find sub items of.
    @return - Returns a string containing text to show on card beneath title.
     */
    public String getSubItems(NoteItem _noteItem) {
        // Open database stuff
        open();

        // Create a new string containing sub items text.
        String subItems = "";

        // Query the database where the path begins with the folder path passed in.
        Cursor cursor = getTopReadable().rawQuery("SELECT TITLE, PATH FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getCorrectedPath(_noteItem) + "%' " + Sorting.mSortMode, null);

        // If the cursor isn't empty continue.
        if (cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0) {
            // Move to first row of cursor.
            cursor.moveToFirst();
            do {
                // If the number of slashes in the folder is one less than the notes then add the notes as these are direct sub items.
                if (cursor.getString(1).startsWith(_noteItem.getPath()) && Misc.getOccurences(cursor.getString(1), "/") - 1 == Misc.getOccurences(_noteItem.getPath(), "/"))
                    subItems += cursor.getString(0) + ", ";
            } while (cursor.moveToNext());
        }

        // Close database stuff.
        close(cursor);

        // Return the appropriate sub item to show.
        return subItems.length() == 0 ? this.mContext.getString(R.string.no_subitems) : subItems.substring(0, subItems.length() - 2);
    }

    /*
    Returns the corrected path by putting in special characters where needed as otherwise it messes up the query.

    @param - NoteItem to correct query of.
    @return - Returns the corrected string.

    ******* CHECK OTHER QUERIES TO SEE IF THIS IMPLEMENTED
     */
    private String getCorrectedPath(NoteItem _noteItem) {
        // Add suffix to the path
        String temp = _noteItem.getPath() + "/";

        // Loop through characters in the new path and handle quote characters.
        for (int i = 0; i < temp.length(); i++) {
            if ((temp.charAt(i) + "").equals("'") && !(temp.charAt(i - 1) + "").equals("\'"))
                temp = temp.substring(0, i) + '\'' + temp.substring(i);
        }

        // Returned corrected string.
        return temp;
    }

    /*
    Gets the previous path from a specified path, chops off the end of string effectively to produce the correct one.

    @param - Old path to chop down.
    @return - Altered path that has been handled.
     */
    public String getPrevPath(String _path) {
        // Loop backwards through path and return substring when reaches the next '/'.
        for (int i = _path.length() - 2; i >= 0; i--) {
            if ((_path.charAt(i) + "").equals("/"))
                return _path.substring(0, i + 1);
        }
        // Otherwise return base path.
        return "/";
    }

    /*
    Returns the query for searching for notes.

    @param - null
    @return - The query string to use.
     */
    private String getQuery() {
        // Create a string builder to make the process more efficient.
        StringBuilder builder = new StringBuilder("SELECT * FROM " + Database.NOTE_TABLE + " ");

        // Get current search text.
        String search = mActionBarFragment.mSearchBox.getText().toString().trim();

        // Based on the search text and note mode append the string being built.
        if (search.length() > 0) {
            builder.append("WHERE TITLE LIKE '%" + search + "%' ");
            if (mNoteMode.equals(NoteMode.EVERYTHING))
                builder.append("AND PATH LIKE '" + PATH + "%' ");
        } else if (mNoteMode.equals(NoteMode.EVERYTHING)) {
            builder.append("WHERE PATH LIKE '" + PATH + "%' ");
        }

        // Add on the sorting mode suffix.
        builder.append(Sorting.mSortMode);

        // Return the string.
        return builder.toString();
    }

    /*
    Gets the column position associated with the column title.

    @param - title to get column position of.
    @return - returns the integer index of the column position.
     */
    private int getColumnPos(String _title) {
        // Loop through columns.
        for (Columns column : Columns.values()) {
            // If there is a match return the column position.
            if (column.title.equals(_title))
                return column.columnPos;
        }
        return 0;
    }

    /*
    Creates a note from the cursor row provided.

    @param - cursor storing current row to obtain values from.
    @return - note item to be built from the cursor values.
     */
    private NoteItem getNoteItem(Cursor _cursor) {
        // Build a note from the cursors values.
        NoteItem.Builder builder = new NoteItem.Builder(_cursor.getString(getColumnPos("PATH")), _cursor.getString(getColumnPos("FOLDER")).equals("true"), _cursor.getString(getColumnPos("TITLE")));
        builder.item(_cursor.getString(getColumnPos("ITEM")))
                .time(_cursor.getString(getColumnPos("TIME")))
                .date(_cursor.getString(getColumnPos("DATE")))
                .link(_cursor.getString(getColumnPos("LINK")))
                .lastModified(_cursor.getLong(getColumnPos("LAST_MODIFIED")));

        // Return the built note.
        return builder.build();
    }

    /*
    Gets the most recently added readable database from the stack.

    @param - null
    @return - The database that is at the top.
     */
    private SQLiteDatabase getTopReadable() {
        return mReadableDatabases.peek();
    }

    /*
    Gets the most recently added writeable database from the stack.

    @param - null
    @return - The database that is at the top.
     */
    private SQLiteDatabase getTopWriteable() {
        return mWriteableDatabases.peek();
    }

    /*
    Gets the most recently added database from the stack.

    @param - null
    @return - The database that is at the top.
     */
    private Database getTopDatabase(){
        return mDatabases.peek();
    }
}