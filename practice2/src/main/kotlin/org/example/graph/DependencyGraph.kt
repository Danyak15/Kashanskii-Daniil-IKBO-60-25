package org.example.graph

/** Строит граф зависимостей алгоритмом BFS без рекурсии. */
object DependencyGraph {
    private const val MIN_DEPTH = 0

    /**
     * Строит граф из заранее загруженных зависимостей.
     *
     * @param source зависимости пакетов
     * @param start начальный пакет
     * @param maxDepth максимальная глубина обхода
     * @return граф зависимостей в порядке BFS
     */
    fun build(
        source: Map<String, List<String>>,
        start: String,
        maxDepth: Int
    ): Map<String, List<String>> {
        require(start in source) {
            "Package not found: $start"
        }

        return build(start, maxDepth) { name ->
            source[name].orEmpty()
        }
    }

    /**
     * Выполняет BFS с получением зависимостей по мере обхода.
     *
     * @param start начальный пакет
     * @param maxDepth максимальная глубина обхода
     * @param dependenciesOf функция получения зависимостей пакета
     * @return граф, ограниченный заданной глубиной
     */
    fun build(
        start: String,
        maxDepth: Int,
        dependenciesOf: (String) -> List<String>
    ): Map<String, List<String>> {
        require(maxDepth >= MIN_DEPTH) {
            "Depth cannot be negative"
        }

        val queue = ArrayDeque<Pair<String, Int>>()
        val visited = mutableSetOf(start)
        val result = linkedMapOf<String, List<String>>()

        queue.addLast(start to MIN_DEPTH)

        while (queue.isNotEmpty()) {
            val (name, depth) = queue.removeFirst()

            val dependencies = if (depth < maxDepth) {
                dependenciesOf(name)
            } else {
                emptyList()
            }

            result[name] = dependencies

            for (dependency in dependencies) {
                if (visited.add(dependency)) {
                    queue.addLast(dependency to depth + 1)
                }
            }
        }

        return result
    }
}