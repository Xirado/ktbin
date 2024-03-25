package dev.xirado.ktbin.api

import io.ktor.client.request.forms.*
import kotlinx.datetime.Instant

/**
 * [dev.xirado.ktbin.api.entity.DocumentFile] input class used for document creation.
 *
 * Create using [fileInput]
 *
 * @see fileInput
 */
data class FileData internal constructor(
    val fileName: String,
    val input: Any,
    val language: Language,
    val expires: Instant?,
)

/**
 * Creates a [FileData] object used for uploading files to Gobin.
 *
 * @param fileName  The name of the file.
 * @param content   The content of the file.
 * @param language  The language of this file. Default: [AUTO][Language.AUTO]
 * @param expires   When this file should expire, or null for no expiry
 *
 * @return [FileData] used for uploading.
 */
fun fileInput(
    fileName: String,
    content: CharSequence,
    language: Language = Language.AUTO,
    expires: Instant? = null,
) = FileData(fileName, content.toString(), language, expires)

/**
 * Creates a [FileData] object used for uploading files to Gobin.
 *
 * @param fileName  The name of the file.
 * @param content   The content of the file.
 * @param language  The language of this file. Default: [AUTO][Language.AUTO]
 * @param expires   When this file should expire, or null for no expiry
 *
 * @return [FileData] used for uploading.
 */
fun fileInput(
    fileName: String,
    content: InputProvider,
    language: Language,
    expires: Instant? = null,
) = FileData(fileName, content, language, expires)