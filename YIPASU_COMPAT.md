# YipaSU compatibility

This fork recognizes the YipaSU package `com.jinfuwei.luoyu` before other supported KernelSU managers.

YipaSU 3.2.5ksu manager builds intentionally ship `libksud.so` without embedded KMI modules. Therefore package-name detection alone is insufficient for GhostLock late-load. This fork selects a certificate-bound module set by hashing the installed manager signing certificate, then selects the module matching the running kernel KMI.

Bundled delivery sets:

| YipaSU delivery | Manager certificate SHA-256 | KMI modules |
| --- | --- | --- |
| Previous delivery, Actions run `35224021313` | `c0637c5479d2d2c57853c51e8e7c4f0014080a1b442c299df30d91d898f17f74` | Android 12/5.10 through Android 16/6.12 (7 modules) |
| Current delivery, Actions run `35344920757` | `8384ceae15190e053777bf73c220e6892e599a421f12f7fe7784ed9bde02cafd` | Android 12/5.10 through Android 16/6.12 (7 modules) |

The selected module is loaded with the installed YipaSU `ksud` using `ksud insmod`, after which `ksud late-load --package-name com.jinfuwei.luoyu` completes userspace initialization.

Future YipaSU builds signed with a different temporary certificate require their matching KMI directory to be added under `app/src/main/assets/yipasu-kmi/<certificate-sha256>/`. A mismatched module must never be substituted because manager authorization is certificate-bound.
