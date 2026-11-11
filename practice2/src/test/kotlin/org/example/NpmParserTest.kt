package org.example

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/** Проверяет извлечение прямых зависимостей из JSON npm-пакета. */
class NpmParserTest {
    @Test
    fun parsesDependencies() {
        val json = """
            {
                "dependencies": {
                    "debug": "^4.1.0",
                    "@babel/parser": "^7.24.0"
                }
            }
        """.trimIndent()

        val dependencies = NpmParser.parseDependencies(json)

        assertEquals(
            mapOf(
                "debug" to "^4.1.0",
                "@babel/parser" to "^7.24.0"
            ),
            dependencies
        )
    }

    @Test
    fun handlesMissingDependencies() {
        val json = """
            {
                "name": "example",
                "version": "1.0.0"
            }
        """.trimIndent()

        val dependencies = NpmParser.parseDependencies(json)
        assertTrue(dependencies.isEmpty())
    }

    @Test
    fun handlesEmptyDependencies() {
        val json = """{"dependencies": {}}"""

        val dependencies = NpmParser.parseDependencies(json)

        assertTrue(dependencies.isEmpty())
    }

    @Test
    fun rejectsMalformedJson() {
        val json = """{"dependencies": """

        assertFailsWith<Exception> {
            NpmParser.parseDependencies(json)
        }
    }

    @Test
    fun rejectsInvalidDependenciesStructure() {
        val json = """{"dependencies": []}"""

        assertFailsWith<IllegalArgumentException> {
            NpmParser.parseDependencies(json)
        }
    }
}