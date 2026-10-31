package com.diskwala.recreated.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DiskWalaDao {
    // Downloads
    @Query("SELECT * FROM downloads ORDER BY downloadedAtMs DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: String): DownloadEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownload(id: String)

    // Recent Files
    @Query("SELECT * FROM recent_files ORDER BY addedAtMs DESC")
    fun getRecentFiles(): Flow<List<RecentFileEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentFile(file: RecentFileEntity)

    @Query("DELETE FROM recent_files WHERE id = :id")
    suspend fun deleteRecentFile(id: String)

    @Query("DELETE FROM recent_files WHERE id NOT IN (SELECT id FROM recent_files ORDER BY addedAtMs DESC LIMIT :limit)")
    suspend fun pruneRecentFiles(limit: Int = 100)

    // Recent Creators
    @Query("SELECT * FROM recent_creators ORDER BY lastSeenMs DESC")
    fun getRecentCreators(): Flow<List<RecentCreatorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentCreator(creator: RecentCreatorEntity)

    @Query("DELETE FROM recent_creators WHERE id = :id")
    suspend fun deleteRecentCreator(id: String)

    @Query("DELETE FROM recent_creators WHERE id NOT IN (SELECT id FROM recent_creators ORDER BY lastSeenMs DESC LIMIT :limit)")
    suspend fun pruneRecentCreators(limit: Int = 50)
}
