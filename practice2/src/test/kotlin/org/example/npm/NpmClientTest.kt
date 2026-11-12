package org.example.npm

import com.sun.net.httpserver.HttpServer
import java.io.IOException
import java.net.HttpURLConnection
import java.net.InetSocketAddress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Проверяет HTTP-запросы к реестру npm-пакетов. */
class NpmClientTest {
    private val responseJson = """{"name":"express","version":"4.18.2"}"""

    @Test
    fun fetchesPackageData() {
        val server = startServer("/express/4.18.2")

        try {
            val json = NpmClient.fetch(
                "express", serverUrl(server), "4.18.2"
            )

            assertEquals(responseJson, json)
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun rejectsMissingPackage() {
        val server = startServer(
            "/unknown/1.0.0",
            HttpURLConnection.HTTP_NOT_FOUND
        )

        try {
            val error = assertFailsWith<IOException> {
                NpmClient.fetch("unknown", serverUrl(server), "1.0.0")
            }

            assertEquals("Registry returned HTTP 404", error.message)
        } finally {
            server.stop(0)
        }
    }

    @Test
    fun fetchesScopedPackage() {
        val server = startServer("/%40babel%2Fcore/7.24.0")

        try {
            val json = NpmClient.fetch(
                "@babel/core", serverUrl(server), "7.24.0"
            )

            assertEquals(responseJson, json)
        } finally {
            server.stop(0)
        }
    }

    /**
     * Формирует адрес локального тестового сервера.
     *
     * @param server HTTP-сервер
     * @return URL сервера
     */
    private fun serverUrl(server: HttpServer): String {
        return "http://127.0.0.1:${server.address.port}"
    }

    /**
     * Запускает локальный HTTP-сервер для тестирования.
     *
     * @param expectedPath ожидаемый путь запроса
     * @param statusCode HTTP-код ответа
     * @return запущенный тестовый сервер
     */
    private fun startServer(
        expectedPath: String,
        statusCode: Int = HttpURLConnection.HTTP_OK
    ): HttpServer {
        val address = InetSocketAddress("127.0.0.1", 0)
        val server = HttpServer.create(address, 0)

        server.createContext("/") { exchange ->
            val status = if (exchange.requestURI.rawPath == expectedPath) {
                statusCode
            } else {
                HttpURLConnection.HTTP_NOT_FOUND
            }

            val bytes = responseJson.toByteArray(Charsets.UTF_8)
            exchange.sendResponseHeaders(status, bytes.size.toLong())
            exchange.responseBody.use { it.write(bytes) }
        }

        server.start()
        return server
    }
}