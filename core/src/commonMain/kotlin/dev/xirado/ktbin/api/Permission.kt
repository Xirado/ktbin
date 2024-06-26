package dev.xirado.ktbin.api

/**
 * Permissions used when sharing Gobin [documents][dev.xirado.ktbin.api.entity.Document].
 *
 * @see dev.xirado.ktbin.api.Ktbin.shareDocument
 */
enum class Permission(val id: String, val offset: Int) {
    WRITE("write", 1 shl 0),
    DELETE("delete", 1 shl 1),
    SHARE("share", 1 shl 2),
    WEBHOOKS("webhook", 1 shl 3)
}