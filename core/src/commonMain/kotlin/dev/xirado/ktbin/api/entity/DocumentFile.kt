package dev.xirado.ktbin.api.entity

import dev.xirado.ktbin.api.KtbinEntity
import dev.xirado.ktbin.api.Language
import kotlinx.datetime.Instant

/**
 * A file contained in a Gobin [Document].
 *
 * @see Document.files
 * @see Document.getFile
 */
interface DocumentFile : KtbinEntity {
    /**
     * The name of this file
     */
    val name: String

    /**
     * The content of this file.
     *
     * @throws IllegalStateException If the [Document] this file is contained in was retrieved using [Document.getVersions(withContent = false)][Document.getVersions].
     */
    val content: String

    /**
     * The formatted content of this file, or null.
     *
     * @see [dev.xirado.ktbin.api.Ktbin.getDocument]
     * @see [dev.xirado.ktbin.api.Ktbin.getDocumentFile]
     */
    val formatted: String?

    /**
     * The [Language] of this file.
     */
    val language: Language

    /**
     * Timestamp of when this file should expire, or null if it never expires.
     */
    val expiresAt: Instant?
}