package at.xirado.ktbin.internal

import at.xirado.ktbin.api.FileData
import at.xirado.ktbin.api.Language
import io.ktor.client.request.forms.*
import io.ktor.http.*

internal fun Collection<FileData>.buildMultipartBody() = MultiPartFormDataContent(formData {
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