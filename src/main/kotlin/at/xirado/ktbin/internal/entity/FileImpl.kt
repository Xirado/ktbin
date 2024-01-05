package at.xirado.ktbin.internal.entity

import at.xirado.ktbin.api.Ktbin
import at.xirado.ktbin.api.entity.DocumentFile
import at.xirado.ktbin.api.Language
import at.xirado.ktbin.api.language
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiFile(
    val name: String,
    val content: String,
    val formatted: String? = null,
    val language: String,
    @SerialName("expires_at")
    val expiresAt: Instant?,
)

internal class FileImpl(
    file: ApiFile,
    override val ktbin: Ktbin,
) : DocumentFile {
    override val name: String = file.name
    override val content: String = file.content
    override val formatted: String? = file.formatted
    override val language: Language = language(file.language)
    override val expiresAt: Instant? = file.expiresAt
}