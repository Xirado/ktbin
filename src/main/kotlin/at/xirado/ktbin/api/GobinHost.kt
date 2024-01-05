package at.xirado.ktbin.api

import io.ktor.http.*

/**
 * The host used to connect to a remote Gobin server.
 *
 * @see defaultGobinHost
 */
data class GobinHost(
    val host: String,
    val protocol: URLProtocol = URLProtocol.HTTPS,
    val port: Int = 443
)

val defaultGobinHost = GobinHost("xgob.in")