package dev.xirado.ktbin.internal

import dev.xirado.ktbin.api.json
import io.ktor.util.*
import kotlinx.serialization.Serializable

internal val jwtDelimiterRegex = "\\.".toRegex()

@Serializable
internal data class DecodedJWT(
    val iat: Int,
    val pms: Int,
    val sub: String,
)

internal fun readUpdateToken(token: String): DecodedJWT {
    val encoded = token.split(jwtDelimiterRegex)[1]
    val decoded = encoded.decodeBase64String()

    return json.decodeFromString(decoded)
}