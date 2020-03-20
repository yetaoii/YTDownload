package com.yetao.download.model.store

import android.content.ContentValues
import com.yetao.download.manager.YTDownloadManager
import com.yetao.download.util.log
import java.util.concurrent.locks.ReentrantLock
import java.util.concurrent.locks.ReentrantReadWriteLock


/**
 *  Created by yetao on 2020/3/19
 *  description
 **/
class SqlManager private constructor() {

    companion object {
        val instance by lazy {
            SqlManager()
        }
    }

    private val lock = ReentrantReadWriteLock()

    private var dbHelper: SqlLiteHelper? = null
        get() {
            if (field == null) {
                field = SqlLiteHelper(YTDownloadManager.instance.context!!)
            }
            return field
        }

    private fun getWritableDatabase() = dbHelper?.writableDatabase

    fun insert(body: TaskBody) {
        lock.writeLock().lock()
        try {
            val values = ContentValues()
            values.put(SqlConstant.SQL_FIELD_URL, body.url)
            values.put(SqlConstant.SQL_FIELD_SAVE_PATH, body.savePath)
            values.put(SqlConstant.SQL_FIELD_PROGRESS, body.progress)
            values.put(SqlConstant.SQL_FIELD_TOTAL, body.total)
            values.put(SqlConstant.SQL_FIELD_FILE_MODIFY_TIME, body.fileModifyTime)
            //数据库执行插入命令
            getWritableDatabase()?.apply {
                use {
                    insert(SqlConstant.SQL_TABLE_NAME, null, values)
                }
            }
        } finally {
            lock.writeLock().unlock()
        }
        "sql -> insert into table ${SqlConstant.SQL_TABLE_NAME} values=$body".log()

    }

    fun delete(url: String) {
        lock.writeLock().lock()
        try {
            val status = getWritableDatabase()?.apply {
                use {
                    delete(
                        SqlConstant.SQL_TABLE_NAME,
                        "${SqlConstant.SQL_FIELD_URL}=?",
                        arrayOf(url)
                    )
                }
            }
            "sql -> delete table ${SqlConstant.SQL_TABLE_NAME} status:${status} url=$url".log()

        } finally {
            lock.writeLock().unlock()
        }

    }

    fun find(url: String): TaskBody? {
        lock.writeLock().lock()
        var result: TaskBody? = null
        try {
            result = null
            getWritableDatabase()?.apply {
                use {
                    val cursor =query(
                        SqlConstant.SQL_TABLE_NAME,
                        arrayOf(
                            SqlConstant.SQL_FIELD_URL,
                            SqlConstant.SQL_FIELD_SAVE_PATH,
                            SqlConstant.SQL_FIELD_PROGRESS,
                            SqlConstant.SQL_FIELD_TOTAL,
                            SqlConstant.SQL_FIELD_FILE_MODIFY_TIME,
                            SqlConstant.SQL_FIELD_UPDATE_TIME,
                            SqlConstant.SQL_FIELD_CREATE_TIME
                        ),
                        "${SqlConstant.SQL_FIELD_URL}=?",
                        arrayOf(url),
                        null,
                        null,
                        null
                    );
                    while (cursor!!.moveToNext()) {
                        val url = cursor.getString(cursor.getColumnIndex(SqlConstant.SQL_FIELD_URL))
                        val savePath = cursor.getString(cursor.getColumnIndex(SqlConstant.SQL_FIELD_SAVE_PATH))
                        val progress = cursor.getLong(cursor.getColumnIndex(SqlConstant.SQL_FIELD_PROGRESS))
                        val total = cursor.getLong(cursor.getColumnIndex(SqlConstant.SQL_FIELD_TOTAL))
                        val fileModifyTime =
                            cursor.getString(cursor.getColumnIndex(SqlConstant.SQL_FIELD_FILE_MODIFY_TIME))
                          val updateTime =
                            cursor.getLong(cursor.getColumnIndex(SqlConstant.SQL_FIELD_UPDATE_TIME))
                        val createTime =
                            cursor.getLong(cursor.getColumnIndex(SqlConstant.SQL_FIELD_CREATE_TIME))
                        if (result == null) {
                            result = TaskBody(url, savePath, progress, total, fileModifyTime,updateTime, createTime)
                        }
                    }
                    // 关闭游标，释放资源
                    cursor.close()
                }
            }
        } finally {
            lock.writeLock().unlock()
        }

        "sql -> find table ${SqlConstant.SQL_TABLE_NAME} url=$url values=$result".log()
        return result
    }

    fun update(body: TaskBody) {
        lock.writeLock().lock()
        try {
            val values = ContentValues()
            values.put(SqlConstant.SQL_FIELD_URL, body.url)
            values.put(SqlConstant.SQL_FIELD_SAVE_PATH, body.savePath)
            values.put(SqlConstant.SQL_FIELD_PROGRESS, body.progress)
            values.put(SqlConstant.SQL_FIELD_TOTAL, body.total)
            values.put(SqlConstant.SQL_FIELD_FILE_MODIFY_TIME, body.fileModifyTime)
            var status = -1;
            getWritableDatabase()?.apply {
                use {
                    beginTransaction()
                    try {
                        status =update(
                            SqlConstant.SQL_TABLE_NAME,
                            values,
                            "${SqlConstant.SQL_FIELD_URL} = ?",
                            arrayOf(body.url)
                        )
                        setTransactionSuccessful()
                    } finally{
                        endTransaction()
                    }
                }

            }
            "sql -> update table ${SqlConstant.SQL_TABLE_NAME} status:${status} values=$body".log()

        } finally {
            lock.writeLock().unlock()
        }


    }
}