package com.diskwala.recreated.data.remote

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class MediaResponse(
    val status: String? = null,
    val data: MediaData? = null
)

@Serializable
data class MediaData(
    val id: String? = null,
    val streamUrl: String? = null,
    val title: String? = null,
    val thumbnail: String? = null,
    val sizeBytes: Long? = null
)

class ApiClient(
    private val baseUrl: String = "https://api.diskwala.com/api/v2/android"
) {
    private val client = HttpClient(OkHttp) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
            })
        }
    }

    suspend fun getStreamInfo(mediaId: String): Result<MediaData> {
        return runCatching {
            val response = client.get("$baseUrl/media/$mediaId") {
                contentType(ContentType.Application.Json)
                header("User-Agent", "DiskWala-Android-Client")
            }
            val body: MediaResponse = response.body()
            body.data ?: throw IllegalStateException("Empty media response")
        }
    }
}
