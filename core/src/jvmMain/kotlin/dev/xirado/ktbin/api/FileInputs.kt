package dev.xirado.ktbin.api

import io.ktor.client.request.forms.*
import io.ktor.utils.io.streams.*
import kotlinx.datetime.Instant
import java.io.File
import java.io.InputStream

/**
 * Creates a [FileData] object used for uploading files to Gobin.
 *
 * @param fileName  The name of the file.
 * @param content   The content of the file.
 * @param language  The language of this file. Default: [AUTO][Language.AUTO]
 * @param expiresAt When this file should expire, or null for no expiry
 *
 * @return [FileData] used for uploading.
 */
fun fileInput(
    fileName: String,
    content: File,
    language: Language,
    expiresAt: Instant? = null,
): FileData {
    if (!content.exists())
        throw IllegalStateException("File does not exist")

    if (content.isDirectory)
        throw IllegalArgumentException("Cannot upload directories")

    return FileData(fileName, InputProvider(content.length()) { content.inputStream().asInput() }, language, expiresAt)
}

/**
 * Creates a [FileData] object used for uploading files to Gobin.
 *
 * @param fileName  The name of the file.
 * @param content   The content of the file.
 * @param language  The language of this file. Default: [AUTO][Language.AUTO]
 * @param expiresAt When this file should expire, or null for no expiry
 *
 * @return [FileData] used for uploading.
 */
fun fileInput(
    fileName: String,
    content: InputStream,
    language: Language,
    expiresAt: Instant? = null,
) = FileData(fileName, InputProvider(null) { content.asInput() }, language, expiresAt)