package edu.csulb.android.notes.ExtraClass;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.os.Build;

import java.util.Date;


/**
 * Created by KEYUR on 25-02-2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NOTES = "NotesStorage.db";
    private static final String TABLE_NOTES = "Notes";
    public static final String COLUMN_ID = "_id";
    private static final String COLUMN_ID_DATATYPE = "INTEGER";
    private static final String COLUMN_ID_CONSTRAINT = "PRIMARY KEY AUTOINCREMENT";
    public static final String COLUMN_CAPTION = "Caption";
    private static final String COLUMN_CAPTION_DATATYPE = "TEXT";
    public static final String COLUMN_THUMBNAIL_PATH = "Thumbnail_Path";
    private static final String COLUMN_THUMBNAIL_PATH_DATATYPE = "TEXT";
    public static final String COLUMN_IMAGE_PATH = "Image_Path";
    private static final String COLUMN_IMAGE_PATH_DATATYPE = "TEXT";
    private static final String CREATE_SQL = "CREATE TABLE IF NOT EXISTS "
            + TABLE_NOTES + " ("
            + COLUMN_ID + " " + COLUMN_ID_DATATYPE +" "+ COLUMN_ID_CONSTRAINT +", "
            + COLUMN_CAPTION + " " + COLUMN_CAPTION_DATATYPE + ", "
            + COLUMN_THUMBNAIL_PATH + " " + COLUMN_THUMBNAIL_PATH_DATATYPE + ", "
            + COLUMN_IMAGE_PATH + " " + COLUMN_IMAGE_PATH_DATATYPE + ")";
    private static final String DROP_SQL = "DROP TABLE IF EXISTS " + TABLE_NOTES;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NOTES, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_SQL);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DROP_SQL);
        onCreate(db);
    }

    public boolean insertData(String Caption, String Thumbnail_Path, String Image_Path) {
        SQLiteDatabase db  = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_CAPTION, Caption);
        contentValues.put(COLUMN_THUMBNAIL_PATH, Thumbnail_Path);
        contentValues.put(COLUMN_IMAGE_PATH, Image_Path);
        long result = db.insert(TABLE_NOTES,null,contentValues);
        if(result==-1){
            return false;
        }
        return true;
    }

    public Cursor getAllData(){
        SQLiteDatabase db  = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from "+ TABLE_NOTES, null);
        return result;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = null;
        String[] whereArgs = {};
        db.delete(TABLE_NOTES, whereClause, whereArgs);
    }

    public Cursor getImagePath(String note_ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("Select * from "+ TABLE_NOTES + " Where " + COLUMN_ID +" = " +note_ID, null);
        return result;
    }


}
