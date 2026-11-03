English | [简体中文](NIGHT_COMPAT_ZH.md)

# Night manager integration

This GhostLock build is locked to the Night manager currently deployed by the WTLYF server.

- Manager package: `com.Night.night`
- Manager build: `g442be83b`
- Server repository snapshot: `wutong7891/KernelSU@4d3dc3fc35386f7c9d84fbb4f3ffc378fc8a3638`
- GhostLock upstream sync: `YuKongA/ghostlock-app@b88b70c77918f7226d899f33976533fa639b264f`
- Server Manager APK SHA-256: `9b3e0df21ab34542850bd37becd80e197b3790bbbbbece874b2973e7de0bc243`
- Manager signing-certificate SHA-256: `adcf2529d856694b572bdae35a3ae211e79a69213cd9bc2180f4d5a8e9296afa`
- Bundled KMI range: Android 12/5.10 through Android 17/6.18

Before starting the native chain, the Android layer verifies the installed package name and APK signing certificate. It then selects the exact KMI from `app/src/main/assets/night-kmi/<certificate-sha256>/` and copies `libksud.so` only from that verified Night installation.

The bundled modules are byte-for-byte copies of the KMI files published with the server snapshot:

| KMI | SHA-256 |
|---|---|
| `android12-5.10` | `dc77a7581f265aec6dec109e5323a10a7f7b91a3e0611e3d96381bafe27cdbd7` |
| `android13-5.10` | `5ae8a727c1b38bff0a44b5913e3d1fa84e967a63094a8eb9fd7c56ee8eb3eb73` |
| `android13-5.15` | `2dc79fba1582ff92bd1c3bf8f5b8587e0a0cb9fb9fffb654beb867a715ee6e12` |
| `android14-5.15` | `c1aff5c9f90b5e554e06667c0781124047ef8b166fd2f5445c412daf296c7723` |
| `android14-6.1` | `acc26cac7d92024e4432f5147aeeec6a33bc26b10cdbe275c517aef66348705e` |
| `android15-6.6` | `78e6043c19416b048918f662dd363bbc13a0ea923b96e285f2ddc756b85c34c4` |
| `android16-6.12` | `c669f015b5e1519878e89921967bfee6d4149e84aeca69cdddf30335894cd146` |
| `android17-6.18` | `b6b0cad3748032abd7fa204977e91a25df8a54b2ae3b6032d3f1d424ae4d74c4` |

The root stage loads only the selected Night module and initializes KernelSU with `--package-name com.Night.night`. Missing or mismatched files remain fatal; no fallback to an official or third-party manager is enabled.
