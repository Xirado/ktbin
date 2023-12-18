package at.xirado.ktbin

import at.xirado.ktbin.api.*
import at.xirado.ktbin.api.entity.Document
import at.xirado.ktbin.api.entity.DocumentFile
import at.xirado.ktbin.api.entity.RemainingVersions
import at.xirado.ktbin.http.*
import at.xirado.ktbin.http.Requester
import at.xirado.ktbin.internal.entity.ApiDocument
import at.xirado.ktbin.internal.entity.ApiFile
import at.xirado.ktbin.internal.entity.DocumentImpl
import at.xirado.ktbin.internal.entity.FileImpl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.util.reflect.*
import kotlinx.coroutines.*
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json

class Ktbin internal constructor(
    val host: GobinHost,
    internal val coroutineDispatcher: CoroutineDispatcher,
    internal val httpClient: HttpClient,
) : AutoCloseable {
    internal val parentJob = SupervisorJob()
    internal val scope = createCoroutineScope<Ktbin>(parentJob, coroutineDispatcher)
    internal val requester = Requester(this, host)

    suspend fun getDocument(
        key: String,
        version: Long? = null,
        formatter: Formatter? = null,
        styleName: String? = null,
    ): Document? {
        if (styleName != null && formatter == null)
            throw IllegalArgumentException("Setting style name only works when specifying a formatter!")

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
        }

        val route = when {
            version != null -> Route.GET_DOCUMENT_VERSION.compile(key, version.toString())
            else -> Route.GET_DOCUMENT.compile(key)
        }

        val response: ApiDocument? = request(route, parameters = parameters)

        return response?.let { DocumentImpl(it, this) }
    }

    suspend fun getDocumentFile(
        key: String,
        fileName: String,
        version: Long? = null,
        formatter: Formatter? = null,
        styleName: String? = null,
        language: Language? = null
    ): DocumentFile? {
        if (styleName != null && formatter == null)
            throw IllegalArgumentException("Setting style name only works when specifying a formatter!")

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
            fileName.let { this["file"] = it }
            language?.let { this["language"] = it.id }
        }

        val route = when {
            version != null -> Route.GET_DOCUMENT_VERSION_FILE.compile(key, version.toString(), fileName)
            else -> Route.GET_DOCUMENT_FILE.compile(key, fileName)
        }

        val response: ApiFile? = request(route, parameters = parameters)

        return response?.let { FileImpl(it, this) }
    }

    suspend fun getDocumentVersions(
        key: String,
        formatter: Formatter? = null,
        styleName: String? = null,
        withContent: Boolean = true
    ): List<Document>? {
        if (styleName != null && formatter == null)
            throw IllegalArgumentException("Setting style name only works when specifying a formatter!")

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id }
            styleName?.let { this["style"] = it }
            this["withContent"] = withContent.toString()
        }

        val response: List<ApiDocument>? = request(Route.GET_DOCUMENT_VERSIONS.compile(key), parameters = parameters)

        return response?.let { versions ->
            versions.map { DocumentImpl(it, this) }
        }
    }

    suspend fun createDocument(
        content: CharSequence,
        language: Language = Language.AUTO,
        fileName: String = "untitled",
        formatter: Formatter? = null,
        expiresAt: Instant? = null,
    ): Document {
        val file = fileInput(fileName, content, language, expiresAt)

        return createDocument(listOf(file), formatter)
    }

    suspend fun createDocument(
        files: List<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null,
    ): Document {
        if (styleName != null && formatter == null)
            throw IllegalArgumentException("Setting style name only works when specifying a formatter!")

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id.urlEncode() }
            styleName?.let { this["style"] = it }
        }

        val body = files.buildMultipartBody()
        val response: ApiDocument = request(Route.CREATE_DOCUMENT.compile(), body, parameters = parameters)

        return DocumentImpl(response, this)
    }

    suspend fun updateDocument(
        key: String,
        updateToken: String,
        newFiles: List<FileData>,
        formatter: Formatter? = null,
        styleName: String? = null,
    ): Document? {
        if (styleName != null && formatter == null)
            throw IllegalArgumentException("Setting style name only works when specifying a formatter!")

        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val parameters = parameters {
            formatter?.let { this["formatter"] = it.id.urlEncode() }
            styleName?.let { this["style"] = it }
        }

        val body = newFiles.buildMultipartBody()
        val response: ApiDocument? = request(Route.UPDATE_DOCUMENT.compile(key), body, headers = headers, parameters = parameters)

        return response?.let { DocumentImpl(it, this) }
    }

    /**
     * Deletes a document.
     *
     * @return If [version] is not null, the amount of versions left in this document, or 0
     */
    suspend fun deleteDocument(key: String, updateToken: String, version: Long? = null): Int {
        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val route = when {
            version != null -> Route.DELETE_DOCUMENT_VERSION.compile(key, version.toString())
            else -> Route.DELETE_DOCUMENT.compile(key)
        }

        val response: RemainingVersions? = request(route, headers = headers)

        return response?.versions ?: 0
    }

    suspend fun shareDocument(key: String, updateToken: String, permissions: Collection<Permission>): String {
        val permissionBody = Permissions(permissions.map { it.id }.toSet())

        val headers = headers {
            this[HttpHeaders.Authorization] = "Bearer $updateToken"
        }

        val response: DocumentShareResponse = request(Route.SHARE_DOCUMENT.compile(key), permissionBody, headers = headers)

        return response.token
    }

    override fun close() {
        parentJob.cancel()
        httpClient.close()
    }
}

fun <T : HttpClientEngineConfig> ktbin(
    httpEngine: HttpClientEngineFactory<T>,
    httpConfiguration: HttpClientConfig<T>.() -> Unit = { },
    host: GobinHost = defaultGobinHost,
    coroutineDispatcher: CoroutineDispatcher = Dispatchers.IO,
): Ktbin {
    val httpClient = HttpClient(httpEngine, makeHttpConfig(httpConfiguration))
    return Ktbin(host, coroutineDispatcher, httpClient)
}

private typealias HttpConfig<T> = HttpClientConfig<T>.() -> Unit

internal val json = Json {
    ignoreUnknownKeys = true
}

private fun <T : HttpClientEngineConfig> makeHttpConfig(config: HttpConfig<T>): HttpConfig<T> = {
    install(ContentNegotiation) {
        json(json)
    }
    config()
}

internal suspend inline fun <reified T, reified A> Ktbin.request(
    route: Route.CompiledRoute,
    body: T,
    headers: Headers = Headers.Empty,
    parameters: Parameters = Parameters.Empty,
): A = withContext(parentJob + coroutineDispatcher) {
    requester.request(Request(this@request, route, body, typeInfo<T>(), typeInfo<A>(), headers, parameters))
}

internal suspend inline fun <reified A> Ktbin.request(
    route: Route.CompiledRoute,
    headers: Headers = Headers.Empty,
    parameters: Parameters = Parameters.Empty,
): A = withContext(parentJob + coroutineDispatcher) {
    requester.request(Request(this@request, route, Unit, typeInfo<Unit>(), typeInfo<A>(), headers, parameters))
}