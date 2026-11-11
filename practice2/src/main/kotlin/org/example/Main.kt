package org.example

/** Точка входа для проверки и вывода параметров конфигурации. */
fun main() {
    val config = try {
        ConfigLoader.load("config.yaml")
    } catch (e: Exception) {
        System.err.println("Configuration error: ${e.message}")
        return
    }

    println("package_name = ${config.packageName}")
    println("repository = ${config.repository}")
    println("test_mode = ${config.testMode}")
    println("version = ${config.version}")
    println("ascii_tree = ${config.asciiTree}")
    println("max_depth = ${config.maxDepth}")
}