package dev.xirado.ktbin.api.entity

import dev.xirado.ktbin.api.FileData
import dev.xirado.ktbin.api.Formatter
import dev.xirado.ktbin.api.Ktbin
import dev.xirado.ktbin.api.KtbinEntity
import dev.xirado.ktbin.internal.http.Route
import dev.xirado.ktbin.internal.http.createUrl

/**
 * A Gobin document.
 *
 * @see Ktbin.getDocument
 * @see Ktbin.createDocument
 */
interface Document : KtbinEntity {
    /**
     * The unique identifier of this document.
     */
    val key: String

    /**
     * Unix timestamp representing the time this document (or version!) was created.
     */
    val version: Long

    /**
     * List of [files][DocumentFile] contained in this document.
     */
    val files: List<DocumentFile>

    /**
     * The token used for updating a document.
     *
     * This is never null if this document was [created][Ktbin.createDocument] or is the result of [updating][Ktbin.updateDocument] an
     * existing one.
     */
    val updateToken: String?

    /**
     * The url of this document
     */
    val url: String
        get() = ktbin.host.createUrl(key).toString()

    fun previewUrl(alwaysLatest: Boolean = true): String = if (alwaysLatest)
        ktbin.host.createUrl(Route.GET_DOCUMENT_PREVIEW.compile(key)).toString()
    else
        ktbin.host.createUrl(Route.GET_DOCUMENT_VERSION_PREVIEW.compile(key, version.toString())).toString()

    /**
     * Gets a [file][DocumentFile] contained in this document.
     *
     * @param fileName The name of the file to get
     *
     * @return [DocumentFile] of this document, or `null` if a file with this name does not exist.
     */
    fun getFile(fileName: String): DocumentFile? = files.find { it.name == fileName }

    /**
     * Retrieves all versions of this document.
     *
     * Each [version][version] snapshot is represented by its own [Document] object.
     *
     * @param formatter   The default [Formatter] used to [format][DocumentFile.formatted] the files.
     * @param styleName   The name of the style used in combination with [formatter].
     * @param withContent Whether the [content][DocumentFile.content] of each file should be included. (Default: `true`).
     *
     * @return List of [Documents][Document] containing each version of this document as a snapshot, or `null`, if this
     * document no longer exists.
     */
    suspend fun getVersions(
        formatter: Formatter? = null,
        styleName: String? = null,
        withContent: Boolean = true
    ): List<Document>? = ktbin.getDocumentVersions(key, formatter, styleName, withContent)

    /**
     * Updates this document with a new collection of [files][FileData].
     *
     * This will return a new [Document] representing the current snapshot.
     *
     * @param newFiles  Collection of [FileData] to use as the new set of files.
     * @param formatter The default [Formatter] used to [format][DocumentFile.formatted] the files.
     * @param styleName The name of the style used in combination with [formatter].
     */
    suspend fun update(
        newFiles: Collection<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null
    ): Document? {
        if (updateToken == null)
            throw IllegalStateException("This Document has no associated update token!")

        return ktbin.updateDocument(key, updateToken!!, newFiles, formatter, styleName)
    }
}