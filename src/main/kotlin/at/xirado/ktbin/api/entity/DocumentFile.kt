package at.xirado.ktbin.api.entity

import at.xirado.ktbin.api.KtbinEntity
import at.xirado.ktbin.api.Language
import kotlinx.datetime.Instant

interface DocumentFile : KtbinEntity {
    val name: String
    val content: String
    val formatted: String?
    val language: Language
    val expiresAt: Instant?
}