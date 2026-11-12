package org.example

import java.io.File
import java.io.FileNotFoundException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Проверяет загрузку тестового графа зависимостей. */
class TestGraphLoaderTest {
    private val validGraph = "tests/fixtures/graph.txt"

    @Test
    fun loadsValidGraph() {
        val graph = TestGraphLoader.load(validGraph)

        assertEquals(listOf("B", "C"), graph["A"])
        assertEquals(listOf("D"), graph["B"])
        assertEquals(listOf("E"), graph["C"])
        assertEquals(listOf("A", "F"), graph["D"])
        assertEquals(listOf("F"), graph["E"])
        assertEquals(listOf("G"), graph["F"])
        assertEquals(emptyList(), graph["G"])
    }

    @Test
    fun rejectsLineWithoutColon() {
        assertFailsWith<IllegalArgumentException> {
            loadText("A B C")
        }
    }

    @Test
    fun rejectsInvalidPackageName() {
        assertFailsWith<IllegalArgumentException> {
            loadText("a: B C")
        }
    }

    @Test
    fun rejectsInvalidDependencyName() {
        assertFailsWith<IllegalArgumentException> {
            loadText("A: B c")
        }
    }

    @Test
    fun rejectsDuplicatePackage() {
        assertFailsWith<IllegalArgumentException> {
            loadText("A: B\nA: C")
        }
    }

    @Test
    fun ignoresBlankLines() {
        val graph = loadText("\nA: B\n\nB:\n")

        assertEquals(listOf("B"), graph["A"])
        assertEquals(emptyList(), graph["B"])
    }

    @Test
    fun rejectsMissingGraphFile() {
        assertFailsWith<FileNotFoundException> {
            TestGraphLoader.load("tests/fixtures/missing-graph.txt")
        }
    }

    /**
     * Создаёт временный файл с тестовым графом,
     * загружает его и удаляет после проверки.
     */
    private fun loadText(content: String): Map<String, List<String>> {
        val file = File.createTempFile("graph-test-", ".txt")

        return try {
            file.writeText(content, Charsets.UTF_8)
            TestGraphLoader.load(file.absolutePath)
        } finally {
            file.delete()
        }
    }
}