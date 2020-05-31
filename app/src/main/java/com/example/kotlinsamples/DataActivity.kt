package com.example.kotlinsamples

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.kotlinsamples.data.Person3
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DataActivity : AppCompatActivity() {

    lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)
        context = this

        GlobalScope.launch(Dispatchers.Main) {
            query()
        }
    }

    private suspend fun query() {
        withContext(Dispatchers.IO) {
            val sql = "select * from IPAD_RESUME limit 10"

            val db = DBHelper
                .getInstance(context, 1)
                .writableDatabase


            //println(db.path + " " + db.isOpen)

//            db.execSQL("insert into Test (name, author, pages, price) values(?, ?, ?, ?)",
//                arrayOf("tiny's book", "tiny", "600", "20.9"))
//            db.execSQL("insert into Test (name, author, pages, price) values(?, ?, ?, ?)",
//                arrayOf("tongtong", "tong", "250", "9.99"))



            val cursor = db.rawQuery(sql, null)
            while (cursor.moveToNext()) {
                println(cursor.getString(cursor.getColumnIndex("A0101")))
            }
            cursor?.close()
        }
    }
}
