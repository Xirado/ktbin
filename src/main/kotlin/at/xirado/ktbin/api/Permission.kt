package at.xirado.ktbin.api

import kotlinx.serialization.Serializable

enum class Permission(val id: String) {
    WRITE("write"),
    DELETE("delete"),
    SHARE("share")
}

@Serializable
internal data class Permissions(val permissions: Set<String>)