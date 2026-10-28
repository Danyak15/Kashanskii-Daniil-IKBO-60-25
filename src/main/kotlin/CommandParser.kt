package org.example

data class ParsedCommand(
    val name: String,
    val args: List<String>
)

object CommandParser {
    fun parse(input: String): ParsedCommand? {
        val parts = input.trim().split(" ").filter { it.isNotBlank() }

        if (parts.isEmpty()) return null

        return ParsedCommand(
            name = parts[0],
            args = parts.drop(1)
        )
    }
}