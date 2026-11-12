package org.example

import org.example.config.AppConfig
import org.example.config.ConfigLoader
import org.example.graph.DependencyGraph
import org.example.graph.TestGraphLoader
import org.example.npm.NpmClient
import org.example.npm.NpmParser
import org.example.npm.VersionResolver
import org.example.visualization.AsciiTree
import org.example.visualization.DotExporter
import java.awt.Desktop
import java.io.File

private const val SUCCESS_EXIT_CODE = 0

/** Точка входа для запуска анализа зависимостей. */
fun main() {
    val config = try {
        ConfigLoader.load("config.yaml")
    } catch (e: Exception) {
        System.err.println("Configuration error: ${e.message}")
        return
    }

    if (config.testMode) {
        runTestMode(config)
    } else {
        runRegistryMode(config)
    }
}

/**
 * Строит и выводит граф зависимостей из тестового файла.
 *
 * @param config настройки приложения
 */
private fun runTestMode(config: AppConfig) {
    val graph = try {
        val source = TestGraphLoader.load(config.repository)
        DependencyGraph.build(source, config.packageName, config.maxDepth)
    } catch (e: Exception) {
        System.err.println("Graph error: ${e.message}")
        return
    }

    println("Dependency graph for ${config.packageName}:")

    for ((name, dependencies) in graph) {
        println("$name: ${dependencies.joinToString(" ")}")
    }

    saveDot(graph)

    if (config.showLoadOrder) {
        println("BFS order: ${graph.keys.joinToString(" -> ")}")
    }

    if (config.asciiTree) {
        println("ASCII dependency tree:")
        print(AsciiTree.generate(graph, config.packageName))
    }
}

/**
 * Строит и выводит граф зависимостей из npm Registry.
 *
 * @param config настройки приложения
 */
private fun runRegistryMode(config: AppConfig) {
    val versions = mutableMapOf<String, String?>(
        config.packageName to config.version
    )

    val requestOrder = mutableListOf<String>()

    val graph = try {
        DependencyGraph.build(config.packageName, config.maxDepth) { name ->
            loadRegistryDependencies(name, config, versions, requestOrder)
        }
    } catch (e: Exception) {
        System.err.println("Registry error: ${e.message}")
        return
    }

    println("Dependency graph for ${config.packageName}@${config.version}:")

    for ((name, dependencies) in graph) {
        println("$name: ${dependencies.joinToString(" ")}")
    }

    saveDot(graph)

    if (config.showLoadOrder) {
        println("Registry request order: ${requestOrder.joinToString(" -> ")}")
    }

    if (config.asciiTree) {
        println("ASCII dependency tree:")
        print(AsciiTree.generate(graph, config.packageName))
    }
}

/**
 * Получает прямые зависимости npm-пакета.
 *
 * @param name имя пакета
 * @param config настройки приложения
 * @param versions выбранные версии пакетов
 * @param requestOrder порядок запросов к реестру
 * @return список имён прямых зависимостей
 */
private fun loadRegistryDependencies(
    name: String,
    config: AppConfig,
    versions: MutableMap<String, String?>,
    requestOrder: MutableList<String>
): List<String> {
    val version = versions[name] ?: run {
        System.err.println("Skipped $name: unsupported version")
        return emptyList()
    }

    val dependencies = try {
        requestOrder.add("$name@$version")

        val json = NpmClient.fetch(name, config.repository, version)
        NpmParser.parseDependencies(json)
    } catch (e: Exception) {
        if (name == config.packageName) throw e

        System.err.println("Skipped $name@$version: ${e.message}")
        return emptyList()
    }

    for ((dependency, requirement) in dependencies) {
        if (dependency !in versions) {
            versions[dependency] = VersionResolver.resolve(requirement)
        }
    }

    return dependencies.keys.toList()
}

/**
 * Сохраняет граф в DOT, создаёт PNG и открывает изображение.
 *
 * @param graph граф зависимостей
 */
private fun saveDot(graph: Map<String, List<String>>) {
    val dotFile = File("build/dependencies.dot")
    val pngFile = File("build/dependencies.png")

    dotFile.parentFile.mkdirs()
    dotFile.writeText(DotExporter.generate(graph), Charsets.UTF_8)

    println("DOT saved to: ${dotFile.path}")

    try {
        val process = ProcessBuilder(
            "dot", "-Tpng", dotFile.path, "-o", pngFile.path
        ).inheritIO().start()

        if (process.waitFor() != SUCCESS_EXIT_CODE) {
            System.err.println("Graphviz rendering failed")
            return
        }

        println("PNG saved to: ${pngFile.path}")

        if (Desktop.isDesktopSupported() &&
            Desktop.getDesktop().isSupported(Desktop.Action.OPEN)
        ) {
            Desktop.getDesktop().open(pngFile)
        } else {
            println("Open image manually: ${pngFile.path}")
        }
    } catch (e: Exception) {
        System.err.println("Graphviz error: ${e.message}")
    }
}