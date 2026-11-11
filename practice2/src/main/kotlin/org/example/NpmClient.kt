package org.example

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

/** Получает данные о пакетах из npm-репозитория. */
object NpmClient {
    private val client = HttpClient.newHttpClient()

    /** Запрашивает сведения о конкретной версии пакета. */
    fun fetch(
        packageName: String,
        repository: String,
        version: String
    ): String {
        val encodedName = URLEncoder.encode(packageName, StandardCharsets.UTF_8)
        val encodedVersion = URLEncoder.encode(version, StandardCharsets.UTF_8)

        val url = "${repository.trimEnd('/')}/$encodedName/$encodedVersion"

        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .build()

        val response = client.send(
            request,
            HttpResponse.BodyHandlers.ofString()
        )

        if (response.statusCode() != HttpURLConnection.HTTP_OK) {
            throw IOException("Registry returned HTTP ${response.statusCode()}")
        }

        return response.body()
    }
}