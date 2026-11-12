package org.example.visualization

import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals

/** Проверяет создание графа в формате Graphviz DOT. */
class DotExporterTest {
    @Test
    fun generatesGraph() {
        val graph = linkedMapOf(
            "A" to listOf("B", "C"),
            "B" to emptyList(),
            "C" to emptyList()
        )

        val expected = """
            digraph Dependencies {
                "A";
                "A" -> "B";
                "A" -> "C";
                "B";
                "C";
            }
        """.trimIndent() + "\n"

        assertEquals(expected, DotExporter.generate(graph))
    }

    @Test
    fun handlesCycles() {
        val graph = mapOf(
            "A" to listOf("B"),
            "B" to listOf("A")
        )

        val result = DotExporter.generate(graph)

        assertContains(result, "\"A\" -> \"B\";")
        assertContains(result, "\"B\" -> \"A\";")
    }

    @Test
    fun supportsNpmPackageNames() {
        val graph = mapOf(
            "@babel/core" to listOf("ipaddr.js"),
            "ipaddr.js" to emptyList()
        )

        val result = DotExporter.generate(graph)

        assertContains(result, "\"@babel/core\" -> \"ipaddr.js\";")
        assertContains(result, "\"ipaddr.js\";")
    }
}