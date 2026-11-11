package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


/**
 * Хранит параметры анализа зависимостей npm-пакета.
 *
 * @property packageName имя анализируемого пакета
 * @property repository URL репозитория или путь к тестовому файлу
 * @property testMode режим использования тестового репозитория
 * @property version версия анализируемого пакета
 * @property asciiTree необходимость вывода ASCII-дерева
 * @property maxDepth максимальная глубина анализа зависимостей
 */
@Serializable
data class AppConfig(
    @SerialName("package_name")
    val packageName: String,

    @SerialName("repository")
    val repository: String,

    @SerialName("test_mode")
    val testMode: Boolean,

    @SerialName("version")
    val version: String,

    @SerialName("ascii_tree")
    val asciiTree: Boolean,

    @SerialName("max_depth")
    val maxDepth: Int
)