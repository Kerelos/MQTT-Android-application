package kerelosdiaa.mqttapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Kerelos on 6/30/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "MQTT.db";
    public static final String TABLE_NAME = "history_table";

    public static final String ID = "ID";
    public static final String TOPIC = "TOPIC";
    public static final String MESSAGE = "MESSAGE";



    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, TOPIC TEXT, MESSAGE TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
    public boolean insertData(String topic, String message){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TOPIC,topic);
        contentValues.put(MESSAGE,message);

        long result = db.insert(TABLE_NAME,null,contentValues);
        if(result == -1)
            return false;
        return true;
    }
    public Cursor retrieveData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME , null);
        return  res;
    }
}
