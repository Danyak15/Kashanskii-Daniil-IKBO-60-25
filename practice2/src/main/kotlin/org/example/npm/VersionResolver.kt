package org.example.npm

/** Извлекает конкретную версию из требования npm. */
object VersionResolver {
    private val versionPattern = Regex("""\d+\.\d+\.\d+(?:-[\w.-]+)?""")
    private val unsupportedPrefixes = listOf(
        "npm:", "file:", "git+", "github:"
    )

    /**
     * Извлекает версию из требования npm.
     *
     * @param requirement требование к версии
     * @return версия пакета или null для неподдерживаемого требования
     */
    fun resolve(requirement: String): String? {
        val value = requirement.trim()

        if (value == "*" || value == "latest") return "latest"

        if (unsupportedPrefixes.any { value.startsWith(it) }) return null

        if (value.startsWith(">") && !value.startsWith(">=")) return null

        if (value.startsWith("<") && !value.startsWith("<=")) return null

        return versionPattern.find(value)?.value
    }
}