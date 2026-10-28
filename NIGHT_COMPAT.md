# Night manager integration

This fork is locked to the private Night manager and refuses every other KernelSU manager.

- Package: `com.Night.night`
- Signing certificate SHA-256: `adcf2529d856694b572bdae35a3ae211e79a69213cd9bc2180f4d5a8e9296afa`
- Bundled KMI range: Android 12/5.10 through Android 17/6.18
- Night source build: GitHub Actions run `40501327378` (`v3.3.1-350-g836fa803`)
- Night manager policy: the manager APK contains no KMI modules; GhostLock carries the eight certificate-bound modules.

Before running the native chain, the Android layer verifies both the installed package name and its APK signing certificate. It then selects the KMI for the current `uname -r` from `app/src/main/assets/night-kmi/<certificate-sha256>/` and copies `libksud.so` only from that verified Night installation.

The root stage loads only `night_kernelsu.ko` and always initializes KernelSU with `--package-name com.Night.night`. Missing or mismatched Night files are fatal; there is deliberately no fallback to an official or third-party manager.
