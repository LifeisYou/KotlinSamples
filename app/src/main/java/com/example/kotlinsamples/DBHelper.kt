package com.example.kotlinsamples

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DBHelper(context: Context, DB_VERSION: Int = CURRENT_VERSION)
    : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        private val TAG = "DBHelper"
        val DB_NAME = "beida_soft.db" //数据库名称
        var CURRENT_VERSION = 1 //当前的最新版本，如有表结构变更，该版本号要加一
        private var instance: DBHelper? = null
        //  创建语句
        val sqlCreate = "create table Test (" +
                "id integer primary key autoincrement, " +
                "author text, " +
                "price real, " +
                "pages integer, " +
                "name text, " +
                "ver2 text, " +
                "ver3 text, " +
                "ver4 text, " +
                "ver5 text)"

        @Synchronized
        fun getInstance(ctx: Context, version: Int= 0): DBHelper {
            if (instance == null) {
                //如果调用时没传版本号，就使用默认的最新版本号
                instance = if (version>0) DBHelper(ctx.applicationContext, version)
                else DBHelper(ctx.applicationContext)
            }
            return instance!!
        }
    }


    override fun onCreate(db: SQLiteDatabase?) {
        //db?.execSQL(sqlCreate)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
    }

    override fun onDowngrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        super.onDowngrade(db, oldVersion, newVersion)
    }
}