[English](NIGHT_COMPAT.md) | 简体中文

# Night 面具适配

此 GhostLock 构建仅适配 WTLYF 服务器当前部署的 Night 面具。

- 面具包名：`com.Night.night`
- 面具版本：`night-local-patcher-89-g1f7e97c0`
- 服务器仓库快照：`wutong7891/KernelSU@1f7e97c0070c960f712326217013d0e49242d28f`
- Ghost 上游同步：`YuKongA/ghostlock-app@b88b70c77918f7226d899f33976533fa639b264f`
- 服务器面具 APK SHA-256：`4f1a56d9d3e77fd4b08c439efd5d17115e4386e5242cb31223fb4deacaf82485`
- 面具签名证书 SHA-256：`adcf2529d856694b572bdae35a3ae211e79a69213cd9bc2180f4d5a8e9296afa`
- 内置 KMI 范围：Android 12/5.10 至 Android 17/6.18

启动 Native 流程前，Android 层会同时校验已安装面具的包名和 APK 签名证书，然后从 `app/src/main/assets/night-kmi/<证书哈希>/` 选择准确的 KMI，并且只从已验证的 Night 安装中复制 `libksud.so`。

内置模块与服务器快照发布的 KMI 文件逐字节一致：

| KMI | SHA-256 |
|---|---|
| `android12-5.10` | `d74922b3244ad8ad247d4e5c83b270057ed1c56692965eb51415b00c9641609f` |
| `android13-5.10` | `9b61b1f4922106e6d7abfae5fadef70743a8613db323e3e2b7f393ad5a8fcd8b` |
| `android13-5.15` | `991abd26609b7a3825fbd14b677bf8ce737b2933340b3afa64b9e648d6d2435f` |
| `android14-5.15` | `afcbd49626c01afad5c0391197b649e6461f611e1d1492f506bedc2dbcf5b39c` |
| `android14-6.1` | `b7e97bc8881ab716ed6a1541af5b206d3b3e9dcb35e20d8b1a82794f362455b0` |
| `android15-6.6` | `9c694ecab0c1cf0c726b5e90c881e99b59faa920447ab3fe45190e494b98daf6` |
| `android16-6.12` | `ee09c575f1e4bc1b6d7f7d2e60d5d638d169451ae6540f7ca656d7b3012ad62b` |
| `android17-6.18` | `4a207f6ba64d0cbcccc4d769324fb2e6413ba1e0c2ff8731516d825b7a5d98ea` |

Root 阶段只加载选中的 Night 模块，并使用 `--package-name com.Night.night` 初始化 KernelSU。文件缺失或不匹配会直接失败，不会回退到官方或第三方面具。
