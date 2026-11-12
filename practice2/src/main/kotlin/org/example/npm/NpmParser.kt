package org.example.npm

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/** Извлекает прямые зависимости npm-пакета из JSON. */
object NpmParser {
    /**
     * Извлекает прямые зависимости пакета из JSON.
     *
     * @param json JSON с информацией о пакете
     * @return зависимости в формате имя пакета — требование версии
     */
    fun parseDependencies(json: String): Map<String, String> {
        val root = Json.parseToJsonElement(json).jsonObject

        val dependencies = root["dependencies"]?.jsonObject
            ?: return emptyMap()

        return dependencies.mapValues { (_, value) ->
            value.jsonPrimitive.content
        }
    }
}