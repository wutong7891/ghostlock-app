package com.ghostlock.app.data

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.system.Os
import java.io.File
import java.security.MessageDigest
import java.util.Locale

/** Prepares the certificate-bound Night ksud and matching external KMI. */
object NightRuntimePreparer {
    const val CERTIFICATE_SHA256 =
        "adcf2529d856694b572bdae35a3ae211e79a69213cd9bc2180f4d5a8e9296afa"
    const val MODULE_NAME = "night_kernelsu.ko"
    private const val KSUD_NAME = "ksud"

    fun prepare(context: Context, workDir: File, log: (String) -> Unit): File? {
        File(workDir, MODULE_NAME).delete()
        File(workDir, KSUD_NAME).delete()
        val pm = context.packageManager
        val appInfo = runCatching {
            pm.getApplicationInfo(ManagerScanner.NIGHT_PACKAGE, 0)
        }.getOrNull()
        if (appInfo == null) {
            log("Night manager is not installed: ${ManagerScanner.NIGHT_PACKAGE}")
            return null
        }
        val certHash = certificateHash(pm)
        if (certHash == null) {
            log("Night manager signing certificate could not be read")
            return null
        }
        if (!certHash.equals(CERTIFICATE_SHA256, ignoreCase = true)) {
            log("Night manager certificate mismatch: ${certHash.take(12)}")
            return null
        }
        val source = File(appInfo.nativeLibraryDir, "libksud.so")
        if (!source.isFile) {
            log("Night manager does not contain libksud.so")
            return null
        }
        if (!prepareModule(context, workDir, certHash, log)) return null
        val output = File(workDir, KSUD_NAME)
        return runCatching {
            source.inputStream().use { input -> output.outputStream().use { input.copyTo(it) } }
            runCatching { Os.chmod(output.absolutePath, 448) }
            output
        }.onFailure { log("copy Night ksud failed: ${it.message}") }.getOrNull()
    }

    private fun prepareModule(
        context: Context,
        workDir: File,
        certHash: String,
        log: (String) -> Unit,
    ): Boolean {
        val release = System.getProperty("os.version", "").orEmpty()
        val android = Regex("android\\d+").find(release)?.value
        val kernel = Regex("^(\\d+\\.\\d+)").find(release)?.groupValues?.get(1)
        if (android == null || kernel == null) {
            log("Night detected, but KMI could not be parsed from $release")
            return false
        }
        val kmi = "$android-$kernel"
        val asset = "night-kmi/$certHash/${kmi}_kernelsu.ko"
        val output = File(workDir, MODULE_NAME)
        return runCatching {
            context.assets.open(asset).use { input ->
                output.outputStream().use { target -> input.copyTo(target) }
            }
            runCatching { Os.chmod(output.absolutePath, 420) }
            log("Night exclusive KMI ready: $kmi (${certHash.take(12)})")
            true
        }.onFailure {
            output.delete()
            log("Night certificate/KMI pair is not bundled: ${certHash.take(12)}/$kmi")
        }.getOrDefault(false)
    }

    @Suppress("DEPRECATION")
    private fun certificateHash(pm: PackageManager): String? = runCatching {
        val signatures = when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                val info = pm.getPackageInfo(
                    ManagerScanner.NIGHT_PACKAGE,
                    PackageManager.PackageInfoFlags.of(
                        PackageManager.GET_SIGNING_CERTIFICATES.toLong(),
                    ),
                )
                val signingInfo = checkNotNull(info.signingInfo)
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.P -> {
                val info = pm.getPackageInfo(
                    ManagerScanner.NIGHT_PACKAGE,
                    PackageManager.GET_SIGNING_CERTIFICATES,
                )
                val signingInfo = checkNotNull(info.signingInfo)
                if (signingInfo.hasMultipleSigners()) {
                    signingInfo.apkContentsSigners
                } else {
                    signingInfo.signingCertificateHistory
                }
            }

            else -> pm.getPackageInfo(
                ManagerScanner.NIGHT_PACKAGE,
                PackageManager.GET_SIGNATURES,
            ).signatures
        }
        val certificate = checkNotNull(signatures?.firstOrNull()).toByteArray()
        MessageDigest.getInstance("SHA-256").digest(certificate)
            .joinToString("") { byte ->
                "%02x".format(Locale.ROOT, byte.toInt() and 0xff)
            }
    }.getOrNull()
}
