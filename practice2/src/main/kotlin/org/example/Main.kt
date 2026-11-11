package org.example

/** Точка входа для проверки и вывода параметров конфигурации. */
fun main() {
    val config = try {
        ConfigLoader.load("config.yaml")
    } catch (e: Exception) {
        System.err.println("Configuration error: ${e.message}")
        return
    }

    val json = try {
        NpmClient.fetch(
            config.packageName,
            config.repository,
            config.version
        )
    } catch (e: Exception) {
        System.err.println("Registry error: ${e.message}")
        return
    }

    val dependencies = try {
        NpmParser.parseDependencies(json)
    } catch (e: Exception) {
        System.err.println("JSON parsing error: ${e.message}")
        return
    }

    println("Direct dependencies for ${config.packageName}@${config.version}:")

    if (dependencies.isEmpty()) {
        println("No dependencies")
    } else {
        dependencies.toSortedMap().forEach { (name, version) ->
            println("$name: $version")
        }
    }
}