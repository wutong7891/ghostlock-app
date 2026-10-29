package com.ghostlock.app.data

import com.ghostlock.app.data.ManagerScanner.Candidate
import org.junit.Assert.assertEquals
import org.junit.Test

class ManagerScannerTest {
    private fun candidate(
        pkg: String,
        hasKsud: Boolean = true,
        builtin: Boolean = pkg in ManagerScanner.BUILTIN,
        label: String = pkg,
    ) = Candidate(pkg, label, hasKsud, builtin)

    @Test
    fun `resolvePackages keeps builtin order when nothing chosen`() {
        assertEquals(ManagerScanner.BUILTIN, ManagerScanner.resolvePackages(null))
        assertEquals(ManagerScanner.BUILTIN, ManagerScanner.resolvePackages(""))
        assertEquals(ManagerScanner.BUILTIN, ManagerScanner.resolvePackages("  "))
    }

    @Test
    fun `resolvePackages ignores a stale non-Night builtin`() {
        assertEquals(ManagerScanner.BUILTIN, ManagerScanner.resolvePackages("me.weishu.kernelsu"))
    }

    @Test
    fun `resolvePackages ignores a custom manager`() {
        assertEquals(ManagerScanner.BUILTIN, ManagerScanner.resolvePackages("custom.pkg"))
    }

    @Test
    fun `sort puts compatible builtins first then compatible then label order`() {
        val sorted = listOf(
            candidate("z.app", hasKsud = false, label = "Zeta"),
            candidate(ManagerScanner.NIGHT_PACKAGE),
            candidate("a.app", hasKsud = false, label = "Alpha"),
            candidate("m.app", hasKsud = true, label = "Mid"),
        ).sortedWith(ManagerScanner.sortComparator())
        assertEquals(
            listOf(ManagerScanner.NIGHT_PACKAGE, "m.app", "a.app", "z.app"),
            sorted.map { it.packageName },
        )
    }
}
