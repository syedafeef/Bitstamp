package com.afeef.cryptoapi.db

import android.content.Context
import androidx.room.*
import com.afeef.cryptoapi.db.dao.OrderBookDao
import com.afeef.cryptoapi.model.OrderBookResponse

@Database(entities = [OrderBookResponse::class], version = 1, exportSchema = false)
abstract class AppDatabase:RoomDatabase() {

    abstract fun orderBookdao() : OrderBookDao

    companion object {
        var INSTANCE: AppDatabase? = null

        fun getAppDataBase(context: Context): AppDatabase? {
            if (INSTANCE == null){
                synchronized(AppDatabase::class){
                    INSTANCE = Room.databaseBuilder(context, AppDatabase::class.java, "myTerminalDB")
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }

}