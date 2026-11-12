package org.example

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

/** Строит граф зависимостей из тестового файла. */
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
}

/** Строит граф зависимостей из npm Registry. */
private fun runRegistryMode(config: AppConfig) {
    val versions = mutableMapOf<String, String?>(
        config.packageName to config.version
    )

    val graph = try {
        DependencyGraph.build(config.packageName, config.maxDepth) { name ->
            loadRegistryDependencies(name, config, versions)
        }
    } catch (e: Exception) {
        System.err.println("Registry error: ${e.message}")
        return
    }

    println("Dependency graph for ${config.packageName}@${config.version}:")

    for ((name, dependencies) in graph) {
        println("$name: ${dependencies.joinToString(" ")}")
    }
}

/** Получает прямые зависимости очередного npm-пакета. */
private fun loadRegistryDependencies(
    name: String,
    config: AppConfig,
    versions: MutableMap<String, String?>
): List<String> {
    val version = versions[name] ?: run {
        System.err.println("Skipped $name: unsupported version")
        return emptyList()
    }

    val dependencies = try {
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