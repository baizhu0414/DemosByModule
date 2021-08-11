package com.example.modulestepcounter.utils;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.Nullable;


/**
 * Created by fySpring
 * Date: 2020/4/21
 * To do:
 */
public class DBOpenHelper extends SQLiteOpenHelper {
    private final String DB_NAME = "StepCounter.db"; //数据库名称
    private final int DB_VERSION = 1;//数据库版本,大于0

    //用于创建Banner表
    private String CREATE_BANNER = ("create table if not exists step  ("
            + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
            + "curDate TEXT, "
            + "totalSteps TEXT)");


    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_BANNER);//执行有更改的sql语句
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public DBOpenHelper(@Nullable Context context) {
        super(context, "StepCounter.db", null,1);
    }

}