package at.xirado.ktbin.internal

import at.xirado.ktbin.api.Permission
import kotlinx.serialization.Serializable
import java.util.EnumSet

@Serializable
internal data class Permissions(val permissions: Set<String>)

internal fun Collection<Permission>.getRaw(): Int {
    var mask = 0

    forEach {
        mask = mask or it.offset
    }

    return mask
}

internal fun Int.toPermissions(): EnumSet<Permission> {
    val set = EnumSet.noneOf(Permission::class.java)

    Permission.entries.forEach {
        if (this and it.offset != 0)
            set += it
    }

    return set
}