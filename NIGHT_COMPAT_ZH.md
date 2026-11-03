[English](NIGHT_COMPAT.md) | 简体中文

# Night 面具适配

此 GhostLock 构建仅适配 WTLYF 服务器当前部署的 Night 面具。

- 面具包名：`com.Night.night`
- 面具版本：`g442be83b`
- 服务器仓库快照：`wutong7891/KernelSU@4d3dc3fc35386f7c9d84fbb4f3ffc378fc8a3638`
- 服务器面具 APK SHA-256：`9b3e0df21ab34542850bd37becd80e197b3790bbbbbece874b2973e7de0bc243`
- 面具签名证书 SHA-256：`adcf2529d856694b572bdae35a3ae211e79a69213cd9bc2180f4d5a8e9296afa`
- 内置 KMI 范围：Android 12/5.10 至 Android 17/6.18

启动 Native 流程前，Android 层会同时校验已安装面具的包名和 APK 签名证书，然后从 `app/src/main/assets/night-kmi/<证书哈希>/` 选择准确的 KMI，并且只从已验证的 Night 安装中复制 `libksud.so`。

内置模块与服务器快照发布的 KMI 文件逐字节一致：

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

Root 阶段只加载选中的 Night 模块，并使用 `--package-name com.Night.night` 初始化 KernelSU。文件缺失或不匹配会直接失败，不会回退到官方或第三方面具。
