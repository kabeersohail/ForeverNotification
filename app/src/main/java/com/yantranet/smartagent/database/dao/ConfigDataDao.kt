package com.yantranet.smartagent.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yantranet.smartagent.database.entities.ConfigDataEntity

@Dao
interface ConfigDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(configData: ConfigDataEntity)

    @Query("SELECT * FROM config_data WHERE id = :id")
    suspend fun getConfigDataById(id: String): ConfigDataEntity?
}