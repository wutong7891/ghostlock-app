package com.ghostlock.app.data

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import java.io.File

/** Covers the platform-facing scan(): package enumeration, self/system
 * filtering, and libksud.so detection through a real PackageManager. */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ManagerScannerScanTest {
    private lateinit var pm: PackageManager
    private lateinit var app: Application

    @Before
    fun setUp() {
        app = RuntimeEnvironment.getApplication() as Application
        pm = app.packageManager
    }

    private fun install(pkg: String, system: Boolean, ksud: Boolean) {
        // Each package gets its own lib dir so ksud presence is independent.
        val dir = File(app.cacheDir, "libs-${pkg}-${System.nanoTime()}").apply { mkdirs() }
        val info = ApplicationInfo().apply {
            packageName = pkg
            flags = if (system) ApplicationInfo.FLAG_SYSTEM else 0
            nativeLibraryDir = dir.absolutePath
        }
        if (ksud) File(dir, "libksud.so").writeText("stub")
        shadowOf(pm).installPackage(
            PackageInfo().apply {
                packageName = pkg
                applicationInfo = info
            },
        )
    }

    @Test
    fun `scan keeps only Night and drops other managers and system apps`() {
        install(ManagerScanner.NIGHT_PACKAGE, system = false, ksud = true)
        install("com.example.no-ksud", system = false, ksud = false)
        install("me.weishu.kernelsu", system = false, ksud = true)
        install("com.android.settings", system = true, ksud = false)

        val result = ManagerScanner.scan(pm, "com.ghostlock.app")
        assertEquals(listOf(ManagerScanner.NIGHT_PACKAGE), result.map { it.packageName })
        assertTrue(result[0].hasKsud)
        assertTrue(result[0].builtin)
    }

    @Test
    fun `scan drops the self package even if it ships libksud`() {
        install("com.ghostlock.app", system = false, ksud = true)
        assertTrue(ManagerScanner.scan(pm, "com.ghostlock.app").isEmpty())
    }

    @Test
    fun `scan reports hasKsud false when Night lib dir is empty`() {
        install(ManagerScanner.NIGHT_PACKAGE, system = false, ksud = false)
        val result = ManagerScanner.scan(pm, "com.ghostlock.app")
        assertEquals(1, result.size)
        assertFalse(result[0].hasKsud)
        assertTrue(result[0].builtin)
    }
}
