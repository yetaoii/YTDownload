package com.yetao.download.model.store

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.yetao.download.util.log

/**
 *  Created by yetao on 2020/3/18
 *  description
 **/
class SqlLiteHelper(
    context: Context
) : SQLiteOpenHelper(context, SqlConstant.sqlName, null, SqlConstant.sqlVersion) {
    override fun onCreate(db: SQLiteDatabase) {
//        val sql =
//                "create table ${SqlConstant.SQL_TABLE_NAME}(id INTEGER PRIMARY KEY autoincrement,${SqlConstant.SQL_FIELD_URL} varchar(512),${SqlConstant.SQL_FIELD_SAVE_PATH} varchar(512),${SqlConstant.SQL_FIELD_PROGRESS} INTEGER,${SqlConstant.SQL_FIELD_TOTAL} INTEGER,${SqlConstant.SQL_FIELD_UPDATE_TIME} datetime DEFAULT CURRENT_TIMESTAMP ,${SqlConstant.SQL_FIELD_CREATE_TIME} DateTime DEFAULT CURRENT_TIMESTAMP)"
        val sql =
            "create table ${SqlConstant.SQL_TABLE_NAME}(" +
                    "id INTEGER PRIMARY KEY autoincrement" +
                    ",${SqlConstant.SQL_FIELD_URL} varchar(512)" +
                    ",${SqlConstant.SQL_FIELD_SAVE_PATH} varchar(512)" +
                    ",${SqlConstant.SQL_FIELD_PROGRESS} INTEGER" +
                    ",${SqlConstant.SQL_FIELD_TOTAL} INTEGER" +
                    ",${SqlConstant.SQL_FIELD_UPDATE_TIME} TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP" +
                    ",${SqlConstant.SQL_FIELD_CREATE_TIME} DateTime DEFAULT CURRENT_TIMESTAMP" +
                    ")"
        db.execSQL(sql)
        "sql -> db create table ${SqlConstant.SQL_TABLE_NAME} success".log()
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

}