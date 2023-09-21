package com.yantranet.smartagent.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.yantranet.smartagent.database.dao.ConfigDataDao
import com.yantranet.smartagent.database.entities.ConfigDataEntity

@Database(entities = [ConfigDataEntity::class], version = 1, exportSchema = false)
abstract class SmartAgentDatabase : RoomDatabase() {
    abstract fun configDataDao(): ConfigDataDao

    companion object {
        private var instance: SmartAgentDatabase? = null

        @Synchronized
        fun getInstance(context: Context): SmartAgentDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    SmartAgentDatabase::class.java,
                    "smart_agent_database"
                ).build()
            }
            return instance!!
        }

    }

}