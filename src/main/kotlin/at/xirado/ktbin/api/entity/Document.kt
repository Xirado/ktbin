package at.xirado.ktbin.api.entity

import at.xirado.ktbin.api.FileData
import at.xirado.ktbin.api.Formatter
import at.xirado.ktbin.api.KtbinEntity
import at.xirado.ktbin.api.Language
import at.xirado.ktbin.http.createUrl
import at.xirado.ktbin.Ktbin
import at.xirado.ktbin.http.Route

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
     * The unix timestamp representing the time this document (version) was created
     */
    val version: Long

    /**
     * The list of [files][DocumentFile] contained in this document.
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
     * Retrieves a [file][DocumentFile] contained in this document.
     *
     * @param fileName The name of the file to get
     * @param version The [version][Document.version] of the file to get
     * @param formatter The formatter used to [format][DocumentFile.formatted] this file
     * @param styleName The name of the style used in combination with [formatter]
     * @param language The [Language] to show this file as
     *
     * @return [DocumentFile] of this document, or `null` if the document in question,
     * or a file with this name does not exist.
     */
    suspend fun getFile(
        fileName: String,
        version: Long? = null,
        formatter: Formatter? = null,
        styleName: String? = null,
        language: Language? = null
    ): DocumentFile? = ktbin.getDocumentFile(key, fileName, version, formatter, styleName, language)

    /**
     * Retrieves all versions of this document.
     *
     * Each version snapshot is represented by its own [Document] object.
     *
     * @param formatter   The formatter used to [format][DocumentFile.formatted] this file
     * @param styleName   The name of the style used in combination with [formatter]
     * @param withContent Whether the [content][DocumentFile.content] of each file should be included. (Default: `true`)
     *
     * @return List of [Documents][Document] containing each version of this document as a snapshot, or `null`, if this
     * document has been deleted.
     */
    suspend fun getVersions(
        formatter: Formatter? = null,
        styleName: String? = null,
        withContent: Boolean = true
    ): List<Document>? = ktbin.getDocumentVersions(key, formatter, styleName, withContent)

    suspend fun update(
        newFiles: List<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null
    ): Document? {
        if (updateToken == null)
            throw IllegalStateException("This Document has no associated update token!")

        return ktbin.updateDocument(key, updateToken!!, newFiles, formatter, styleName)
    }
}