package dev.xirado.ktbin.internal.entity

import dev.xirado.ktbin.api.Ktbin
import dev.xirado.ktbin.api.entity.Document
import dev.xirado.ktbin.api.entity.DocumentFile
import kotlinx.serialization.Serializable

@Serializable
internal data class ApiDocument(
    val key: String,
    val version: Long,
    val files: List<ApiFile>,
    val token: String? = null,
)

internal class DocumentImpl(
    document: ApiDocument,
    override val ktbin: Ktbin,
) : Document {
    override val key: String = document.key
    override val version: Long = document.version
    override val files: List<DocumentFile> = document.files.map { FileImpl(it, ktbin) }
    override val updateToken: String? = document.token
}