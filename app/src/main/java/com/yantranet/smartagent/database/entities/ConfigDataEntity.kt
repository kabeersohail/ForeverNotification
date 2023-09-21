package com.yantranet.smartagent.database.entities

import androidx.room.PrimaryKey
import androidx.room.Entity

@Entity(tableName = "config_data")
data class ConfigDataEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val sizeInBytes: Long,
    val cdn_path: String
)