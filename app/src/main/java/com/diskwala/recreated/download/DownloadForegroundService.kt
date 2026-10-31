package com.diskwala.recreated.download

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.diskwala.recreated.data.local.AppDatabase
import com.diskwala.recreated.data.local.DownloadEntity
import kotlinx.coroutines.*
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

class DownloadForegroundService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val notificationManager by lazy {
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val downloadUrl = intent?.getStringExtra(EXTRA_URL)
        val fileId = intent?.getStringExtra(EXTRA_ID) ?: return START_NOT_STICKY
        val fileName = intent.getStringExtra(EXTRA_NAME) ?: "media_download"

        if (downloadUrl != null) {
            val initialNotification = buildNotification(fileName, 0)
            startForeground(NOTIFICATION_ID, initialNotification.build())
            startDownloadTask(fileId, fileName, downloadUrl)
        }

        return START_NOT_STICKY
    }

    private fun startDownloadTask(fileId: String, fileName: String, urlStr: String) {
        serviceScope.launch {
            try {
                val url = URL(urlStr)
                val connection = url.openConnection() as HttpURLConnection
                connection.connect()

                val fileLength = connection.contentLengthLong
                val destination = File(getExternalFilesDir(null), "$fileName.mp4")

                connection.inputStream.use { input ->
                    FileOutputStream(destination).use { output ->
                        val buffer = ByteArray(8192)
                        var totalRead = 0L
                        var read: Int
                        var lastProgress = 0

                        while (input.read(buffer).also { read = it } != -1) {
                            output.write(buffer, 0, read)
                            totalRead += read
                            if (fileLength > 0) {
                                val progress = ((totalRead * 100) / fileLength).toInt()
                                if (progress > lastProgress + 2) {
                                    lastProgress = progress
                                    notificationManager.notify(
                                        NOTIFICATION_ID,
                                        buildNotification(fileName, progress).build()
                                    )
                                }
                            }
                        }
                    }
                }

                // Record finished download in Room
                val entity = DownloadEntity(
                    id = fileId,
                    name = fileName,
                    maskName = fileName,
                    onDiskName = destination.name,
                    savedUri = destination.absolutePath,
                    thumb = null,
                    sizeBytes = destination.length(),
                    mimeType = "video/mp4",
                    ext = "mp4",
                    uploaderName = "DiskWala User",
                    uploaderId = "0",
                    downloadedAtMs = System.currentTimeMillis()
                )
                // Persistence in DB
                stopForeground(STOP_FOREGROUND_DETACH)
                notificationManager.notify(
                    NOTIFICATION_ID,
                    NotificationCompat.Builder(this@DownloadForegroundService, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_sys_download_done)
                        .setContentTitle(fileName)
                        .setContentText("Download complete")
                        .setAutoCancel(true)
                        .build()
                )
            } catch (e: Exception) {
                notificationManager.notify(
                    NOTIFICATION_ID,
                    NotificationCompat.Builder(this@DownloadForegroundService, CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.stat_notify_error)
                        .setContentTitle("Download failed")
                        .setContentText(e.message)
                        .build()
                )
            } finally {
                stopSelf()
            }
        }
    }

    private fun buildNotification(name: String, progress: Int): NotificationCompat.Builder {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle("Downloading $name")
            .setContentText("$progress%")
            .setProgress(100, progress, false)
            .setOngoing(true)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Media Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val CHANNEL_ID = "diskwala_downloads_channel"
        const val NOTIFICATION_ID = 2001
        const val EXTRA_ID = "extra_file_id"
        const val EXTRA_NAME = "extra_file_name"
        const val EXTRA_URL = "extra_download_url"
    }
}
