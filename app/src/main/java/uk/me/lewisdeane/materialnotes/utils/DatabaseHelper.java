package uk.me.lewisdeane.materialnotes.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
        if(_noteItem.getIsFolder()){
            mTempNoteItem = _noteItem;
            CustomDialog customDialog = new CustomDialog(mContext, mContext.getString(R.string.dialog_delete_title), mContext.getString(R.string.dialog_delete_content), mContext.getString(R.string.dialog_delete_confirm), mContext.getString(R.string.dialog_delete_cancel));
            customDialog.setCanceledOnTouchOutside(false);
            customDialog.setCancelable(false);
            customDialog.show();
        } else {
            open("W");
            mSQLiteDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND SUBTITLE=? AND TIME=" + _noteItem.getTime(), new String[]{_noteItem.getTitle(), _noteItem.getItem()});
            close();
        }
    }

    public static ArrayList<NoteItem> getNotesFromDatabase(){
        ArrayList<NoteItem> noteItems = new ArrayList<NoteItem>();
        open("R");
        Cursor cursor = mSQLiteDatabase.query(Database.NOTE_TABLE, new String[]{"PATH", "TITLE", "SUBTITLE", "TIME", "FOLDER"}, null, null, null, null, "TITLE COLLATE NOCASE ASC");

        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                String[] notePathArray = cursor.getString(0).split("/");
                String[] currentPathArray = MainActivity.PATH.split("/");

                ArrayList<String> notePath = new ArrayList<String>();
                ArrayList<String> currentPath = new ArrayList<String>();

                for(int i = 0; i < notePathArray.length-1; i++)
                    notePath.add(notePathArray[i]);

                for(int i = 0; i < currentPathArray.length; i++) {
                    if(currentPathArray[i].length() > 0)
                        currentPath.add(currentPathArray[i]);
                }

                boolean canAdd = false;

                if(currentPath.size() == notePath.size()){
                    canAdd = true;
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

    private static void open(String _mode){
        mDatabase = new Database(mContext);
        mSQLiteDatabase = _mode.equals("W") ? mDatabase.getWritableDatabase() : mDatabase.getReadableDatabase();
    }

    private static void close(){
        mSQLiteDatabase.close();
        mDatabase.close();
    }

    public static void onConfirmClick(){
        open("W");

        mSQLiteDatabase.delete(Database.NOTE_TABLE, "TITLE=? AND SUBTITLE=? AND TIME=" + mTempNoteItem.getTime(), new String[]{mTempNoteItem.getTitle(), mTempNoteItem.getItem()});

        mSQLiteDatabase.execSQL("DELETE FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getTempPath(mTempNoteItem)  + "%'");
        close();
    }

    public static void onCancelClick(){
        MainActivity.mMainFragment.reloadData();
    }

    public static String getSubitems(NoteItem _noteItem){
        String subitems = "";
        open("R");
        Cursor cursor = mSQLiteDatabase.rawQuery("SELECT TITLE FROM " + Database.NOTE_TABLE + " WHERE PATH LIKE '" + getTempPath(_noteItem) +"%'", null);

        if(cursor != null && cursor.moveToFirst() && cursor.getColumnCount() > 0){
            cursor.moveToFirst();
            do{
                subitems += cursor.getString(0) + ", ";
            } while (cursor.moveToNext());
        }

        cursor.close();
        close();

        return subitems.length() == 0 ? mContext.getString(R.string.no_subitems) : subitems.substring(0, subitems.length()-2);
    }

    private static String getTempPath(NoteItem _noteItem){
        String temp = MainActivity.PATH.length() > 0 ? MainActivity.PATH + "/" + _noteItem.getTitle() + "/" : _noteItem.getTitle() + "/";

        for(int i = 0; i < temp.length(); i++){
            if((temp.charAt(i)+"").equals("'") && !(temp.charAt(i-1)+"").equals("\'"))
                temp = temp.substring(0, i) + '\'' +  temp.substring(i);
        }

        return temp;
    }
}
