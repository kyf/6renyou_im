package com.liurenyou.im.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.liurenyou.im.MyApplication;

/**
 * Created by keyf on 2015/11/23.
 */
public class TravelDB extends SQLiteOpenHelper {

    public TravelDB(Context context, String dbName){
        super(context, dbName, null, 1);
    }

    private SQLiteDatabase db = null;

    private static TravelDB helper;

    public void onCreate(SQLiteDatabase db){
        String sql = "create table `travel_card`(id integer primary key, mac_addr varchar(250),  `is_disconnect` int(1), `is_connect` int(1), `longitude` varchar(250), `latitude` varchar(250))";
        db.execSQL(sql);

        if(this.db == null){
            this.db = db;
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }

    public void onOpen(SQLiteDatabase db){
        if(this.db == null){
            this.db = db;
        }
    }

    public Cursor select(String sql){
        return db.rawQuery(sql, null);
    }

    public void exec(String sql){
        db.execSQL(sql);
    }

    private static TravelDB getHelper(){
        if(TravelDB.helper == null){
            Context context = MyApplication.getContext();
            TravelDB.helper = new TravelDB(context, "liurenyou_travelcard");
            TravelDB.helper.getWritableDatabase();
        }
        return TravelDB.helper;
    }

    public static Cursor query(String sql){
        return TravelDB.getHelper().select(sql);
    }

    public static void execute(String sql){
        TravelDB.getHelper().exec(sql);
    }
}
