package com.diskwala.recreated.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DownloadEntity::class,
        RecentFileEntity::class,
        RecentCreatorEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diskWalaDao(): DiskWalaDao
}
