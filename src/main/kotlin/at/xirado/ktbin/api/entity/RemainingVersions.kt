package at.xirado.ktbin.api.entity

import kotlinx.serialization.Serializable

@Serializable
data class RemainingVersions(
    val versions: Int
)