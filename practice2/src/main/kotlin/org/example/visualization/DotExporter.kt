package org.example.visualization

import kotlin.collections.iterator

/** Преобразует граф зависимостей в формат Graphviz DOT. */
object DotExporter {
    /**
     * Формирует описание графа в формате DOT.
     *
     * @param graph граф зависимостей
     * @return текст графа на языке DOT
     */
    fun generate(graph: Map<String, List<String>>): String {
        return buildString {
            appendLine("digraph Dependencies {")

            for ((name, dependencies) in graph) {
                appendLine("    \"$name\";")

                for (dependency in dependencies) {
                    appendLine("    \"$name\" -> \"$dependency\";")
                }
            }

            appendLine("}")
        }
    }
}