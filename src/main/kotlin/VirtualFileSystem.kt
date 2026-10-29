package org.example

import java.io.File
import java.io.IOException
import java.util.Base64

data class VfsEntry(
    val path: String,
    val isDirectory: Boolean,
    val content: ByteArray
)

class VirtualFileSystem(
    private val csvPath: String
) {
    private val entries = linkedMapOf<String, VfsEntry>()

    val fileCount: Int
        get() = entries.values.count { !it.isDirectory }

    val directoryCount: Int
        get() = entries.values.count { it.isDirectory }

    fun allEntries(): List<VfsEntry> = entries.values.toList()

    fun load() {
        val lines = File(csvPath).readLines(Charsets.UTF_8)

        if (lines.firstOrNull()?.trim() != CSV_HEADER) {
            throw IOException("Неверный заголовок CSV: $csvPath")
        }

        val loaded = linkedMapOf<String, VfsEntry>()

        for ((index, line) in lines.drop(1).withIndex()) {
            if (line.isBlank()) continue

            val entry = parseEntry(line, index + 2)

            if (loaded.put(entry.path, entry) != null) {
                throw IOException("Повторяющийся путь: ${entry.path}")
            }
        }

        for ((path) in loaded.values) {
            val parent = path.substringBeforeLast('/').ifEmpty { "/" }

            if (parent != "/" && loaded[parent]?.isDirectory != true) {
                throw IOException("Нет родительского каталога $parent для $path")
            }
        }

        entries.clear()
        entries.putAll(loaded)
    }

    fun reset() {
        File(csvPath).writeText("$CSV_HEADER\n", Charsets.UTF_8)
        entries.clear()
    }

    private fun parseEntry(line: String, lineNumber: Int): VfsEntry {
        val columns = line.split(',', limit = 3)
        if (columns.size != 3) throw IOException("Неверная строка CSV: $lineNumber")

        val (path, type, encoded) = columns

        if (path == "/" || !path.startsWith('/') || path.endsWith('/') || "//" in path ||
            path.split('/').any { it == "." || it == ".." }
        ) {
            throw IOException("Неверный путь в строке $lineNumber: $path")
        }

        if (type != "directory" && type != "file") {
            throw IOException("Неизвестный тип в строке $lineNumber: $type")
        }

        if (type == "directory" && encoded.isNotEmpty()) {
            throw IOException("Каталог не может содержать данные: $path")
        }

        val bytes = try {
            if (type == "file") Base64.getDecoder().decode(encoded)
            else byteArrayOf()
        } catch (e: IllegalArgumentException) {
            throw IOException("Неверный Base64 в строке $lineNumber", e)
        }

        return VfsEntry(path, type == "directory", bytes)
    }

    private companion object {
        const val CSV_HEADER = "path,type,content"
    }
}
