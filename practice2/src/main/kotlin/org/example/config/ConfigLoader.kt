package org.example.config

import com.charleskorn.kaml.Yaml
import java.io.File
import java.net.URI
import java.net.URISyntaxException

/** Загружает YAML-конфигурацию и проверяет её параметры. */
object ConfigLoader {
    private const val MIN_DEPTH = 0

    /**
     * Читает конфигурацию из YAML-файла.
     *
     * @param path путь к файлу конфигурации
     * @return проверенная конфигурация приложения
     */
    fun load(path: String): AppConfig {
        val content = File(path).readText(Charsets.UTF_8)

        val config = Yaml.default.decodeFromString(
            AppConfig.serializer(),
            content
        )

        validate(config)
        return config
    }

    /**
     * Проверяет допустимость параметров конфигурации.
     *
     * @param config конфигурация приложения
     */
    private fun validate(config: AppConfig) {
        require(config.packageName.isNotBlank()) {
            "package_name: package name cannot be empty"
        }
        require(config.repository.isNotBlank()) {
            "repository: URL or path cannot be empty"
        }
        require(config.version.isNotBlank()) {
            "version: package version cannot be empty"
        }
        require(config.maxDepth >= MIN_DEPTH) {
            "max_depth: depth cannot be negative"
        }

        if (!config.testMode) {
            require(isValidUrl(config.repository)) {
                "repository: a valid HTTP(S) URL is required"
            }
        }
    }

    /**
     * Проверяет корректность HTTP(S)-адреса репозитория.
     *
     * @param value проверяемый адрес
     * @return true, если адрес содержит допустимую схему и имя сервера
     */
    private fun isValidUrl(value: String): Boolean {
        val uri = try {
            URI(value)
        } catch (_: URISyntaxException) {
            return false
        }

        return uri.scheme in setOf("http", "https") &&
                !uri.host.isNullOrBlank()
    }
}