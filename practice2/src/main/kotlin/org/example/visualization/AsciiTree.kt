package org.example.visualization

/** Формирует ASCII-дерево зависимостей. */
object AsciiTree {
    /**
     * Формирует ASCII-дерево зависимостей.
     *
     * @param graph граф зависимостей
     * @param start начальный пакет
     * @return текстовое представление дерева
     */
    fun generate(graph: Map<String, List<String>>, start: String): String {
        val result = StringBuilder()
        val visited = mutableSetOf(start)

        result.appendLine(start)
        appendChildren(start, "", graph, visited, result)

        return result.toString()
    }

    /**
     * Добавляет зависимости пакета в ASCII-дерево.
     *
     * @param name имя текущего пакета
     * @param prefix отступ для текущего уровня дерева
     * @param graph граф зависимостей
     * @param visited множество уже посещённых пакетов
     * @param result буфер формируемого дерева
     */
    private fun appendChildren(
        name: String,
        prefix: String,
        graph: Map<String, List<String>>,
        visited: MutableSet<String>,
        result: StringBuilder
    ) {
        val dependencies = graph[name].orEmpty()

        for ((index, dependency) in dependencies.withIndex()) {
            val last = index == dependencies.lastIndex
            val branch = if (last) "`-- " else "|-- "
            val repeated = !visited.add(dependency)

            result.append(prefix).append(branch).append(dependency)

            if (repeated) result.append(" (already visited)")
            result.appendLine()

            if (!repeated) {
                val nextPrefix = prefix + if (last) "    " else "|   "
                appendChildren(dependency, nextPrefix, graph, visited, result)
            }
        }
    }
}