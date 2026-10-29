package com.ghostlock.app.data

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import java.io.File

/** Resolves the certificate-bound Night manager used by this build. */
object ManagerScanner {
    const val PREF_KEY = "manager_package"
    const val NIGHT_PACKAGE = "com.Night.night"

    /** This Night-specific build must never hand off to another manager. */
    val BUILTIN = listOf(NIGHT_PACKAGE)

    data class Candidate(
        val packageName: String,
        val label: String,
        val hasKsud: Boolean,
        val builtin: Boolean,
    )

    /**
     * Only Night is exposed. Other installed managers are deliberately ignored
     * so their incompatible ksud or signing identity cannot be selected.
     */
    @Suppress("DEPRECATION")
    fun scan(pm: PackageManager, selfPackage: String): List<Candidate> =
        pm.getInstalledPackages(0)
            .asSequence()
            .mapNotNull { pi -> pi.applicationInfo?.let { pi to it } }
            .filter { (pi, _) -> pi.packageName == NIGHT_PACKAGE && pi.packageName != selfPackage }
            .filter { (_, ai) -> (ai.flags and ApplicationInfo.FLAG_SYSTEM) == 0 }
            .map { (pi, ai) ->
                val dir = ai.nativeLibraryDir
                Candidate(
                    packageName = pi.packageName,
                    label = ai.loadLabel(pm).toString(),
                    hasKsud = dir != null && File(dir, "libksud.so").isFile,
                    builtin = pi.packageName in BUILTIN,
                )
            }
            .sortedWith(sortComparator())
            .toList()

    /** Pure sort, extracted so it is testable without a PackageManager. */
    fun sortComparator() = compareByDescending<Candidate> { it.builtin && it.hasKsud }
        .thenByDescending { it.hasKsud }
        .thenBy { it.label.lowercase() }

    /** Ignore stale preferences from older generic builds and always use Night. */
    fun resolvePackages(@Suppress("UNUSED_PARAMETER") chosen: String?): List<String> = BUILTIN
}
