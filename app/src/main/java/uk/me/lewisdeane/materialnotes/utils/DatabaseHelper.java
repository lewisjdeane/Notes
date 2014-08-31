package uk.me.lewisdeane.materialnotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import uk.me.lewisdeane.materialnotes.R;
import uk.me.lewisdeane.materialnotes.activities.MainActivity;
import uk.me.lewisdeane.materialnotes.databases.Database;
import uk.me.lewisdeane.materialnotes.objects.NoteItem;

/**
 * Created by Lewis on 15/08/2014.
 */
public class DatabaseHelper{

    private static Context mContext;
    private static Database mDatabase;
    private static SQLiteDatabase mSQLiteDatabase;
    private static NoteItem mTempNoteItem;

    public DatabaseHelper(Context _context){
        mContext = _context;
    }

    public void addNoteToDatabase(NoteItem _oldNoteItem, NoteItem _newNoteItem){
        open("W");
        if(_oldNoteItem != null)
            mSQLiteDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _oldNoteItem.getLastModified(), new String[]{_oldNoteItem.getTitle(), _oldNoteItem.getItem()});
        mSQLiteDatabase.insert(Database.NOTE_TABLE, null, getContentVals(_newNoteItem));
        close();
    }

    public void deleteNoteFromDatabase(NoteItem _noteItem){
        if(_noteItem.getIsFolder()){
            mTempNoteItem = _noteItem;

            CustomDialog.Builder builder = new CustomDialog.Builder(mContext, mContext.getString(R.string.dialog_delete_title), mContext.getString(R.string.dialog_delete_confirm));
            builder.content(mContext.getString(R.string.dialog_delete_content));
            builder.negativeText(mContext.getString(R.string.dialog_delete_cancel));
            builder.positiveColor("#4285F4");

            CustomDialog customDialog = builder.build();
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.setCancelable(false);
            customDialog.show();
            customDialog.setClickListener(new CustomDialog.ClickListener() {
                @Override
                public void onConfirmClick() {
                    open("W");
                    mSQLiteDatabase.delete(MainActivity.NOTE_MODE != MainActivity.NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + mTempNoteItem.getLastModified(), new String[]{mTempNoteItem.getTitle(), mTempNoteItem.getItem()});
                    if(MainActivity.NOTE_MODE != MainActivity.NoteMode.ARCHIVE)
                        mSQLiteDatabase.insert(Database.ARCHIVE_TABLE, null, getContentVals(mTempNoteItem));
                    deleteSubItems(mTempNoteItem);
                    close();
                    MainActivity.loadNotes();
                }

                @Override
                public void onCancelClick() {
                    MainActivity.loadNotes();
                }
            });
        } else {
            open("W");
            mSQLiteDatabase.delete(MainActivity.NOTE_MODE == MainActivity.NoteMode.EVERYTHING ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + _noteItem.getLastModified(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
            if(MainActivity.NOTE_MODE == MainActivity.NoteMode.EVERYTHING)
                mSQLiteDatabase.insert(Database.ARCHIVE_TABLE, null, getContentVals(_noteItem));
            close();
            MainActivity.loadNotes();
        }
    }

    private void deleteSubItems(NoteItem _noteItem){
        Database database = new Database(mContext);
        SQLiteDatabase sqLiteDatabase = database.getReadableDatabase();

        String table = MainActivity.NOTE_MODE != MainActivity.NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        Cursor cursor = sqLiteDatabase.rawQuery("SELECT * FROM " + table + " WHERE PATH LIKE '" + getTempPath(_noteItem) + "%'", null);
        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                NoteItem.Builder builder = new NoteItem.Builder(cursor.getString(0), cursor.getString(1).equals("true"), cursor.getString(2));
                builder.item(cursor.getString(3))
                        .time(cursor.getString(4))
                        .date(cursor.getString(5))
                        .link(cursor.getString(6))
                        .lastModified(cursor.getLong(7));
                NoteItem noteItem = builder.build();

                mSQLiteDatabase.delete(table, "TITLE=? AND ITEM=? AND LAST_MODIFIED=" + noteItem.getLastModified(), new String[]{noteItem.getTitle(), noteItem.getItem()});
                if(MainActivity.NOTE_MODE == MainActivity.NoteMode.EVERYTHING)
                    mSQLiteDatabase.insert(Database.ARCHIVE_TABLE, null, getContentVals(noteItem));
            } while(cursor.moveToNext());
        }

        cursor.close();
        sqLiteDatabase.close();
        database.close();
    }

    public static ArrayList<NoteItem> getNotesFromDatabase(String _search){
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        open("R");
        //Cursor cursor = mSQLiteDatabase.query(MainActivity.NOTE_MODE != MainActivity.NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE, new String[]{"PATH", "FOLDER", "TITLE", "ITEM", "TIME", "DATE", "LINK", "LAST_MODIFIED"}, null, null, null, null, "TITLE COLLATE NOCASE ASC");

        String table = MainActivity.NOTE_MODE != MainActivity.NoteMode.ARCHIVE ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        String query = "SELECT * FROM " + table + (_search.length() > 0 ? " WHERE TITLE LIKE '%" + _search + "%' AND PATH LIKE '" + MainActivity.PATH + "%' ORDER BY FOLDER DESC, TITLE ASC" : " ORDER BY FOLDER DESC, TITLE ASC");

        Cursor cursor = mSQLiteDatabase.rawQuery(query, null);

        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                NoteItem.Builder builder = new NoteItem.Builder(cursor.getString(0), cursor.getString(1).equals("true"), cursor.getString(2));
                builder.item(cursor.getString(3))
                        .time(cursor.getString(4))
                        .date(cursor.getString(5))
                        .link(cursor.getString(6))
                        .lastModified(cursor.getLong(7));

                if(getPrevPath(cursor.getString(0)).equals(MainActivity.PATH) || _search.length() > 0)
                    noteItems.add(builder.build());

            } while(cursor.moveToNext());
        }


        /*
        Put folders at top, and apply sorting method here.
         */

        // Remove items without times or dates
        if(MainActivity.NOTE_MODE == MainActivity.NoteMode.UPCOMING) {
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

    private ContentValues getContentVals(NoteItem _noteItem){
        ContentValues contentValues = new ContentValues();

        String path = _noteItem.getPath() != null ? _noteItem.getPath() : MainActivity.PATH + _noteItem.getTitle();

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

    private static void open(String _mode){
        mDatabase = new Database(mContext);
        mSQLiteDatabase = _mode.equals("W") ? mDatabase.getWritableDatabase() : mDatabase.getReadableDatabase();
    }

    private static void close(){
        mSQLiteDatabase.close();
        mDatabase.close();
    }

    public static String getSubItems(NoteItem _noteItem){
        String subItems = "";
        open("R");

        String table = MainActivity.NOTE_MODE == MainActivity.NoteMode.EVERYTHING ? Database.NOTE_TABLE : Database.ARCHIVE_TABLE;

        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT TITLE, PATH FROM " + table + " WHERE PATH LIKE '" + getTempPath(_noteItem) +"%'", null);

        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                if (cursor.getString(1).startsWith(_noteItem.getPath()) && Misc.getOccurences(cursor.getString(1), "/")-1 == Misc.getOccurences(_noteItem.getPath(), "/"))
                    subItems += cursor.getString(0) + ", ";
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return subItems.length() == 0 ? mContext.getString(R.string.no_subitems) : subItems.substring(0, subItems.length()-2);
    }

    private static String getTempPath(NoteItem _noteItem){
        String temp = _noteItem.getPath() + "/";

        for(int i = 0; i < temp.length(); i++){
            if((temp.charAt(i)+"").equals("'") && !(temp.charAt(i-1)+"").equals("\'"))
                temp = temp.substring(0, i) + '\'' +  temp.substring(i);
        }
        return temp;
    }

    public static String getPrevPath(String _path){
        for(int i = _path.length()-2; i >= 0; i--){
            if((_path.charAt(i)+"").equals("/"))
                return _path.substring(0, i+1);
        }
        return "/";
    }
}
