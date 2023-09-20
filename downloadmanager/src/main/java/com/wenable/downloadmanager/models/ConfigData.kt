package com.wenable.downloadmanager.models

data class ConfigData(
    val id: String,
    val name: String,
    val type: String,
    val sizeInBytes: Long,
    val cdn_path: String
)