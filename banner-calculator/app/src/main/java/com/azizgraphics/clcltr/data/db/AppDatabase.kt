package com.azizgraphics.clcltr.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.azizgraphics.clcltr.data.model.Order

@Database(entities = [Order::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun get(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext, AppDatabase::class.java, "banner_calc.db"
                ).build().also { INSTANCE = it }
            }
    }
}
