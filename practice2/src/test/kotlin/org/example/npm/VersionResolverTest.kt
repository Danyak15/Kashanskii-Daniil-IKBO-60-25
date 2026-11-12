package org.example.npm

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

/** Проверяет упрощённое извлечение версий npm-зависимостей. */
class VersionResolverTest {
    @Test
    fun extractsExactVersion() {
        assertEquals("1.6.8", VersionResolver.resolve("1.6.8"))
    }

    @Test
    fun extractsVersionFromRange() {
        assertEquals("1.6.8", VersionResolver.resolve("^1.6.8"))
        assertEquals("2.3.4", VersionResolver.resolve("~2.3.4"))
        assertEquals("1.6.8", VersionResolver.resolve(">=1.6.8"))
    }

    @Test
    fun extractsFirstVersion() {
        assertEquals(
            "1.6.8",
            VersionResolver.resolve("^1.6.8 || ^2.0.0")
        )
    }

    @Test
    fun preservesPrereleaseSuffix() {
        assertEquals(
            "1.6.8-beta.2",
            VersionResolver.resolve("^1.6.8-beta.2")
        )
    }

    @Test
    fun handlesLatestVersion() {
        assertEquals("latest", VersionResolver.resolve("*"))
        assertEquals("latest", VersionResolver.resolve("latest"))
    }

    @Test
    fun rejectsPartialVersion() {
        assertNull(VersionResolver.resolve("1.x"))
        assertNull(VersionResolver.resolve("1.2.x"))
    }

    @Test
    fun rejectsExclusiveBounds() {
        assertNull(VersionResolver.resolve(">1.6.8"))
        assertNull(VersionResolver.resolve("<1.6.8"))
    }

    @Test
    fun rejectsUnsupportedSources() {
        assertNull(VersionResolver.resolve("file:../library"))
        assertNull(VersionResolver.resolve("git+https://example.com/repo"))
        assertNull(VersionResolver.resolve("npm:other-package@^1.0.0"))
    }
}