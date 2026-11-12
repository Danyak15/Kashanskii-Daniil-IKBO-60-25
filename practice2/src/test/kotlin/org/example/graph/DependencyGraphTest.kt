package org.example.graph

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

/** Проверяет обход графа зависимостей алгоритмом BFS. */
class DependencyGraphTest {
    private val source = TestGraphLoader.load("tests/fixtures/graph.txt")

    @Test
    fun handlesZeroDepth() {
        val result = DependencyGraph.build(source, "A", 0)

        assertEquals(mapOf("A" to emptyList()), result)
    }

    @Test
    fun handlesFirstLevel() {
        val result = DependencyGraph.build(source, "A", 1)

        assertEquals(listOf("B", "C"), result["A"])
        assertEquals(emptyList(), result["B"])
        assertEquals(emptyList(), result["C"])
        assertEquals(3, result.size)
    }

    @Test
    fun respectsMaximumDepth() {
        val result = DependencyGraph.build(source, "A", 2)

        assertEquals(listOf("A", "B", "C", "D", "E"), result.keys.toList())
        assertEquals(emptyList(), result["D"])
        assertEquals(emptyList(), result["E"])
    }

    @Test
    fun traversesInBreadthFirstOrder() {
        val result = DependencyGraph.build(source, "A", 4)

        assertEquals(
            listOf("A", "B", "C", "D", "E", "F", "G"),
            result.keys.toList()
        )
    }

    @Test
    fun handlesCycles() {
        val result = DependencyGraph.build(source, "A", 10)

        assertEquals(listOf("A", "F"), result["D"])
        assertEquals(7, result.size)
    }

    @Test
    fun handlesSharedDependencies() {
        val result = DependencyGraph.build(source, "A", 3)

        assertEquals(listOf("A", "F"), result["D"])
        assertEquals(listOf("F"), result["E"])
        assertEquals(1, result.keys.count { it == "F" })
    }

    @Test
    fun rejectsUnknownPackage() {
        assertFailsWith<IllegalArgumentException> {
            DependencyGraph.build(source, "Z", 3)
        }
    }

    @Test
    fun rejectsNegativeDepth() {
        assertFailsWith<IllegalArgumentException> {
            DependencyGraph.build(source, "A", -1)
        }
    }

    @Test
    fun buildsGraphWithDependencyProvider() {
        val result = DependencyGraph.build("A", 2) { name ->
            source[name].orEmpty()
        }

        assertEquals(
            listOf("A", "B", "C", "D", "E"),
            result.keys.toList()
        )
    }
}