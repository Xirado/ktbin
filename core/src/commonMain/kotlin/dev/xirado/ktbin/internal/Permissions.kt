package dev.xirado.ktbin.internal

import dev.xirado.ktbin.api.Permission
import kotlinx.serialization.Serializable

@Serializable
internal data class Permissions(val permissions: Set<String>)

internal fun Collection<Permission>.getRaw(): Int {
    var mask = 0

    forEach {
        mask = mask or it.offset
    }

    return mask
}

internal fun Int.toPermissions(): Set<Permission> {
    val set = mutableSetOf<Permission>()

    Permission.entries.forEach {
        if (this and it.offset != 0)
            set += it
    }

    return set
}