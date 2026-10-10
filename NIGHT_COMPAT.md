# Night manager integration

This fork is locked to the private Night manager and refuses every other KernelSU manager.

- Package: `me.weishu.wutong`
- Signing certificate SHA-256: `606f3dc77238f37243cf57d2ecb6a6383cadfa48bb2f69dcd501435167c631be`
- Bundled KMI range: Android 12/5.10 through Android 17/6.18

Before running the native chain, the Android layer verifies both the installed package name and its APK signing certificate. It then selects the KMI for the current `uname -r` from `app/src/main/assets/night-kmi/<certificate-sha256>/` and copies `libksud.so` only from that verified Night installation.

The root stage loads only `night_kernelsu.ko` and always initializes KernelSU with `--package-name me.weishu.wutong`. Missing or mismatched Night files are fatal; there is deliberately no fallback to an official or third-party manager.
