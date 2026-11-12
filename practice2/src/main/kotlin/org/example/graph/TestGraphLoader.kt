package org.example.graph

import java.io.File

/** Загружает тестовый граф зависимостей из текстового файла. */
object TestGraphLoader {
    private const val GRAPH_PARTS = 2
    private val namePattern = Regex("[A-Z]+")
    private val spaces = Regex("\\s+")

    /**
     * Загружает тестовый граф зависимостей из файла.
     *
     * @param path путь к текстовому файлу
     * @return граф зависимостей
     */
    fun load(path: String): Map<String, List<String>> {
        val graph = linkedMapOf<String, List<String>>()

        File(path).forEachLine(Charsets.UTF_8) { line ->
            if (line.isBlank()) return@forEachLine

            val parts = line.split(":", limit = 2)
            require(parts.size == GRAPH_PARTS) {
                "Invalid graph line: $line"
            }

            val name = parts[0].trim()
            val dependencies = parts[1].trim()
                .split(spaces)
                .filter { it.isNotBlank() }

            require(namePattern.matches(name)) {
                "Invalid package name: $name"
            }
            require(dependencies.all { namePattern.matches(it) }) {
                "Invalid dependencies for package: $name"
            }
            require(name !in graph) {
                "Duplicate package: $name"
            }

            graph[name] = dependencies
        }

        return graph
    }
}