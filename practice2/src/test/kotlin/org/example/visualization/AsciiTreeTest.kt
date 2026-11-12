package org.example.visualization

import kotlin.test.Test
import kotlin.test.assertEquals

/** Проверяет формирование ASCII-дерева зависимостей. */
class AsciiTreeTest {
    @Test
    fun generatesSimpleTree() {
        val graph = mapOf(
            "A" to listOf("B", "C"),
            "B" to listOf("D"),
            "C" to emptyList(),
            "D" to emptyList()
        )

        val expected = """
            A
            |-- B
            |   `-- D
            `-- C
        """.trimIndent() + "\n"

        assertEquals(expected, AsciiTree.generate(graph, "A"))
    }

    @Test
    fun handlesCycles() {
        val graph = mapOf(
            "A" to listOf("B"),
            "B" to listOf("A")
        )

        val expected = """
            A
            `-- B
                `-- A (already visited)
        """.trimIndent() + "\n"

        assertEquals(expected, AsciiTree.generate(graph, "A"))
    }

    @Test
    fun handlesSharedDependencies() {
        val graph = mapOf(
            "A" to listOf("B", "C"),
            "B" to listOf("D"),
            "C" to listOf("D"),
            "D" to emptyList()
        )

        val expected = """
            A
            |-- B
            |   `-- D
            `-- C
                `-- D (already visited)
        """.trimIndent() + "\n"

        assertEquals(expected, AsciiTree.generate(graph, "A"))
    }

    @Test
    fun handlesPackageWithoutDependencies() {
        val graph = mapOf("A" to emptyList<String>())

        assertEquals("A\n", AsciiTree.generate(graph, "A"))
    }
}