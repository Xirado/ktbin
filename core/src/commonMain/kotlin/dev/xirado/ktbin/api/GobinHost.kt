package dev.xirado.ktbin.api

import io.ktor.http.*

/**
 * The host of a remote Gobin server.
 *
 * @see defaultGobinHost
 */
data class GobinHost(
    val host: String,
    val protocol: URLProtocol = URLProtocol.HTTPS,
    val port: Int = 443
)

/**
 * The default Gobin instance used by Ktbin. (https://xgob.in)
 */
val defaultGobinHost = GobinHost("xgob.in")