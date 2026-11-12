package org.example

import java.io.File
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** Проверяет загрузку и валидацию YAML-конфигурации. */
class ConfigLoaderTest {
    @Test
    fun loadsValidConfiguration() {
        val config = ConfigLoader.load(VALID_CONFIG)

        assertEquals("express", config.packageName)
        assertEquals("https://registry.npmjs.org", config.repository)
        assertFalse(config.testMode)
        assertEquals("4.18.2", config.version)
        assertTrue(config.asciiTree)
        assertEquals(3, config.maxDepth)
    }

    @Test
    fun rejectsNegativeDepth() {
        val error = assertFailsWith<IllegalArgumentException> {
            loadWithValue("max_depth", "-1")
        }

        assertEquals(
            "max_depth: depth cannot be negative",
            error.message
        )
    }

    @Test
    fun rejectsEmptyPackageName() {
        val error = assertFailsWith<IllegalArgumentException> {
            loadWithValue("package_name", "\"\"")
        }

        assertEquals(
            "package_name: package name cannot be empty",
            error.message
        )
    }

    @Test
    fun rejectsEmptyRepository() {
        val error = assertFailsWith<IllegalArgumentException> {
            loadWithValue("repository", "\"\"")
        }

        assertEquals(
            "repository: URL or path cannot be empty",
            error.message
        )
    }

    @Test
    fun rejectsInvalidRepositoryUrl() {
        val error = assertFailsWith<IllegalArgumentException> {
            loadWithValue("repository", "\"not-a-url\"")
        }

        assertEquals(
            "repository: a valid HTTP(S) URL is required",
            error.message
        )
    }

    @Test
    fun rejectsEmptyVersion() {
        val error = assertFailsWith<IllegalArgumentException> {
            loadWithValue("version", "\"\"")
        }

        assertEquals(
            "version: package version cannot be empty",
            error.message
        )
    }

    @Test
    fun rejectsInvalidTestModeType() {
        assertFailsWith<Exception> {
            loadWithValue("test_mode", "invalid")
        }
    }

    @Test
    fun rejectsInvalidAsciiTreeType() {
        assertFailsWith<Exception> {
            loadWithValue("ascii_tree", "invalid")
        }
    }

    @Test
    fun rejectsInvalidDepthType() {
        assertFailsWith<Exception> {
            loadWithValue("max_depth", "invalid")
        }
    }

    @Test
    fun rejectsMissingRequiredField() {
        val text = File(VALID_CONFIG)
            .readLines(Charsets.UTF_8)
            .filterNot { it.startsWith("version:") }
            .joinToString("\n")

        assertFailsWith<Exception> {
            loadText(text)
        }
    }

    @Test
    fun rejectsMissingConfigurationFile() {
        assertFailsWith<FileNotFoundException> {
            ConfigLoader.load("tests/fixtures/missing.yaml")
        }
    }

    @Test
    fun rejectsMalformedYaml() {
        assertFailsWith<Exception> {
            loadText("package_name: [broken")
        }
    }

    @Test
    fun acceptsFilePathInTestMode() {
        val text = File(VALID_CONFIG)
            .readText(Charsets.UTF_8)
            .replace("test_mode: false", "test_mode: true")
            .replace(
                "repository: \"https://registry.npmjs.org\"",
                "repository: \"tests/graph.txt\""
            )

        val config = loadText(text)

        assertTrue(config.testMode)
        assertEquals("tests/graph.txt", config.repository)
    }

    @Test
    fun disablesLoadOrderByDefault() {
        val config = ConfigLoader.load(VALID_CONFIG)

        assertFalse(config.showLoadOrder)
    }

    @Test
    fun enablesLoadOrder() {
        val yaml = File(VALID_CONFIG).readText(Charsets.UTF_8) +
                "\nshow_load_order: true\n"

        val config = loadText(yaml)

        assertTrue(config.showLoadOrder)
    }

    /**
     * Заменяет значение параметра в правильной конфигурации
     * и загружает полученный YAML.
     */
    private fun loadWithValue(key: String, value: String): AppConfig {
        val lines = File(VALID_CONFIG).readLines(Charsets.UTF_8)

        val text = lines.joinToString("\n") { line ->
            if (line.startsWith("$key:")) {
                "$key: $value"
            } else {
                line
            }
        }

        return loadText(text)
    }

    /**
     * Создаёт временный YAML-файл, загружает его и удаляет
     * после завершения проверки.
     */
    private fun loadText(content: String): AppConfig {
        val file = File.createTempFile("config-test-", ".yaml")

        return try {
            file.writeText(content, Charsets.UTF_8)
            ConfigLoader.load(file.absolutePath)
        } finally {
            file.delete()
        }
    }

    private companion object {
        const val VALID_CONFIG = "tests/fixtures/valid.yaml"
    }
}