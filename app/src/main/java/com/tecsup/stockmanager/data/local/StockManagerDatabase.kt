package com.tecsup.stockmanager.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ProductEntity::class],
    version = 1,
    exportSchema = false
)
abstract class StockManagerDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: StockManagerDatabase? = null
// singlenton — solo una instancia de la base de datos
        fun getInstance(context: Context): StockManagerDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    StockManagerDatabase::class.java,
                    "stockmanager_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}