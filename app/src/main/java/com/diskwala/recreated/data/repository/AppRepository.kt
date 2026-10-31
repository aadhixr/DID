package com.diskwala.recreated.data.repository

import com.diskwala.recreated.data.local.DiskWalaDao
import com.diskwala.recreated.data.local.DownloadEntity
import com.diskwala.recreated.data.local.RecentFileEntity
import com.diskwala.recreated.data.remote.ApiClient
import com.diskwala.recreated.data.remote.MediaData
import com.diskwala.recreated.player.StreamResolver
import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val dao: DiskWalaDao,
    private val api: ApiClient = ApiClient()
) : StreamResolver {

    // Resolves streaming links for RefreshingPlayerEngine
    override suspend fun resolveStreamUrl(mediaId: String): Result<String> {
        // If mediaId is already a full direct URL, return it immediately
        if (mediaId.startsWith("http://") || mediaId.startsWith("https://") || mediaId.startsWith("file://")) {
            return Result.success(mediaId)
        }
        return api.getStreamInfo(mediaId).mapCatching { it.streamUrl ?: throw IllegalStateException("No URL") }
    }

    // Database operations
    fun getAllDownloads(): Flow<List<DownloadEntity>> = dao.getAllDownloads()

    suspend fun saveDownload(download: DownloadEntity) = dao.insertDownload(download)

    suspend fun removeDownload(id: String) = dao.deleteDownload(id)

    fun getRecentHistory(): Flow<List<RecentFileEntity>> = dao.getRecentFiles()

    suspend fun recordFileView(file: RecentFileEntity) {
        dao.insertRecentFile(file)
        dao.pruneRecentFiles(100)
    }
}
