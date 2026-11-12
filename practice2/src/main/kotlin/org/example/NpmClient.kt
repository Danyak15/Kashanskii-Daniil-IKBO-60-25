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

    /** Получает сведения о конкретной версии пакета. */
    fun fetch(
        packageName: String,
        repository: String,
        version: String
    ): String {
        val name = URLEncoder.encode(packageName, StandardCharsets.UTF_8)
        val encodedVersion = URLEncoder.encode(version, StandardCharsets.UTF_8)
        val url = "${repository.trimEnd('/')}/$name/$encodedVersion"

        return sendRequest(url)
    }

    /** Выполняет HTTP-запрос и возвращает тело ответа. */
    private fun sendRequest(url: String): String {
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