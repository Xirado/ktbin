package at.xirado.ktbin

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import java.net.URLDecoder
import java.net.URLEncoder

internal inline fun <reified T : Any> createCoroutineScope(
    job: Job? = null,
    dispatcher: CoroutineDispatcher
): CoroutineScope {
    val parent = job ?: SupervisorJob()
    val log = KotlinLogging.logger(T::class.qualifiedName!!)
    val handler = CoroutineExceptionHandler { _, throwable ->
        log.error(throwable) { "Uncaught exception from coroutine" }
        if (throwable is Error) {
            parent.cancel()
            throw throwable
        }
    }
    return CoroutineScope(dispatcher + parent + handler)
}

internal fun String.urlEncode(): String = URLEncoder.encode(this, Charsets.UTF_8)
internal fun String.urlDecode(): String = URLDecoder.decode(this, Charsets.UTF_8)