package com.diskwala.recreated.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey val id: String,
    val name: String,
    val maskName: String,
    val onDiskName: String,
    val savedUri: String,
    val thumb: String?,
    val sizeBytes: Long,
    val mimeType: String,
    val ext: String,
    val uploaderName: String,
    val uploaderId: String,
    val downloadedAtMs: Long
)

@Entity(tableName = "recent_files")
data class RecentFileEntity(
    @PrimaryKey val id: String,
    val name: String,
    val maskName: String,
    val thumb: String?,
    val sizeBytes: Long,
    val mimeType: String,
    val ext: String,
    val views: Long,
    val createdAt: String,
    val timeStampMs: Long,
    val uploaderName: String,
    val uploaderId: String,
    val addedAtMs: Long
)

@Entity(tableName = "recent_creators")
data class RecentCreatorEntity(
    @PrimaryKey val id: String,
    val name: String,
    val profilePic: String?,
    val lastSeenMs: Long
)
