package dev.xirado.ktbin.internal.entity

import kotlinx.serialization.Serializable

@Serializable
internal data class RemainingVersions(
    val versions: Int
)