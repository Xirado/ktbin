package at.xirado.ktbin.http

import at.xirado.ktbin.Ktbin
import io.ktor.http.*
import io.ktor.util.reflect.*

class Request<T, A>(
    val app: Ktbin,
    val route: Route.CompiledRoute,
    val body: T,
    val bodyType: TypeInfo,
    val resolveType: TypeInfo,
    val requestHeaders: Headers,
    val queryParameters: Parameters,
)