package at.xirado.ktbin.api.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GobinAPIException(
    @SerialName("message")
    val apiMessage: String,
    val status: Int,
    val path: String,
    @SerialName("request_id")
    val requestId: String,
) : Exception("$status - $apiMessage")