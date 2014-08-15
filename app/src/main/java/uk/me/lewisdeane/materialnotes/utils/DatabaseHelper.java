package uk.me.lewisdeane.materialnotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.databases.Database;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 15/08/2014.
 */
public class DatabaseHelper {

    private Context mContext;
    private Database mDatabase;
    private SQLiteDatabase mSQLiteDatabase;

    public DatabaseHelper(Context _context){
        mContext = _context;
    }

    public void addNoteToDatabase(NoteItem _noteItem){
        open("W");
        mSQLiteDatabase.insert(Database.NOTE_TABLE, null, getContentVals(_noteItem));
        close();
    }

    public void editNoteToDatabase(NoteItem _oldNoteItem, NoteItem _newNoteItem){
        open("W");
        mSQLiteDatabase.update(Database.NOTE_TABLE, getContentVals(_newNoteItem), "TITLE=? AND SUBTITLE=? AND TIME="+_oldNoteItem.getTime(), new String[]{_oldNoteItem.getTitle(), _oldNoteItem.getItem()});
        close();
    }

    public void deleteNoteFromDatabase(NoteItem _noteItem){
        open("W");
        mSQLiteDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND SUBTITLE=? AND TIME="+_noteItem.getTime(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
        close();
    }

    public ArrayList<NoteItem> getNotesFromDatabase(){
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        open("R");
        Cursor cursor = mSQLiteDatabase.query(Database.NOTE_TABLE, new String[]{"PATH", "TITLE", "SUBTITLE", "TIME", "FOLDER"}, null, null, null, null, "TITLE COLLATE NOCASE DESC");

        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                ArrayList<String> notePath = new ArrayList<String>(Arrays.asList(cursor.getString(0).split("/")));
                notePath.remove(notePath.size()-1);

                List<String> currentPath = Arrays.asList(MainActivity.PATH.split("/"));

                boolean canAdd = true;

                if(currentPath.size() == notePath.size()){
                    for(int i = 0; i < currentPath.size(); i++){
                        if(!currentPath.get(i).equals(notePath.get(i))) {
                            canAdd = false;
                            break;
                        }
                    }
                }

                if(canAdd)
                    noteItems.add(new NoteItem(mContext, cursor.getString(1), cursor.getString(2), cursor.getString(4).equals("true") ? true : false, cursor.getLong(3)));

            } while(cursor.moveToNext());
        }

        close();
        return noteItems;
    }

    private ContentValues getContentVals(NoteItem _noteItem){
        ContentValues contentValues = new ContentValues();

        contentValues.put("PATH", MainActivity.PATH.equals("") ?  _noteItem.getTitle() : MainActivity.PATH + "/" + _noteItem.getTitle());
        contentValues.put("TITLE", _noteItem.getTitle());
        contentValues.put("SUBTITLE", _noteItem.getItem());
        contentValues.put("TIME", _noteItem.getTime());
        contentValues.put("FOLDER", _noteItem.getIsFolder() ? "true" : "false");

        return contentValues;
    }

    private void open(String _mode){
        mDatabase = new Database(mContext);
        mSQLiteDatabase = _mode.equals("W") ? mDatabase.getWritableDatabase() : mDatabase.getReadableDatabase();
    }

    private void close(){
        mSQLiteDatabase.close();
        mDatabase.close();
    }
}
