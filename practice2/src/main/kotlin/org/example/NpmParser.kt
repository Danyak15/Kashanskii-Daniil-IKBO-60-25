package org.example

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Извлекает прямые зависимости npm-пакета из JSON. */
object NpmParser {
    /** Возвращает названия зависимостей и требования к их версиям. */
    fun parseDependencies(json: String): Map<String, String> {
        val root = Json.parseToJsonElement(json).jsonObject

        val dependencies = root["dependencies"]?.jsonObject
            ?: return emptyMap()

        return dependencies.mapValues { (_, value) ->
            value.jsonPrimitive.content
        }
    }
}