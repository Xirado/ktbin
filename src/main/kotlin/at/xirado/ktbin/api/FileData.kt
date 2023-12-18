package at.xirado.ktbin.api

import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.streams.*
import kotlinx.datetime.Instant
import java.io.File

/**
 * Create using [fileInput]
 */
data class FileData internal constructor(
    val fileName: String,
    val input: Any,
    val language: Language,
    val expiresAt: Instant?,
)

fun fileInput(
    fileName: String,
    content: CharSequence,
    language: Language = Language.AUTO,
    expiresAt: Instant? = null,
) = FileData(fileName, content.toString(), language, expiresAt)

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

internal fun List<FileData>.buildMultipartBody() = MultiPartFormDataContent(formData {
    forEachIndexed { i, file ->
        val headers = headers {
            this[HttpHeaders.ContentDisposition] = "filename=${file.fileName.quote()}"
            val language = file.language

            if (language != Language.AUTO)
                this["Language"] = language.id
        }

        when (val input = file.input) {
            is String -> append("file-$i".quote(), input, headers)
            is InputProvider -> append("file-$i".quote(), input, headers)
            else -> throw IllegalArgumentException("Unsupported input type")
        }
    }
})