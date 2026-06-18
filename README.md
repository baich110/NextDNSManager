# NextDNSManager · 中文 Android 客户端

> 一个为中文用户打造的 NextDNS 全功能 Android 管理客户端，覆盖 NextDNS 官方 API 的全部能力。

[![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)](LICENSE)
[![Platform](https://img.shields.io/badge/Platform-Android%205.0%2B-green.svg?style=for-the-badge)](#)
[![Language](https://img.shields.io/badge/Kotlin-Jetpack%20Compose-purple.svg?style=for-the-badge)](#)
[![Release](https://img.shields.io/github/v/release/baich110/NextDNSManager?style=for-the-badge)](https://github.com/baich110/NextDNSManager/releases)

---

## ✨ 特性

- 🇨🇳 **完整中文界面** —— 不用再对着英文文档对照设置项
- 🛡️ **安全设置** —— 威胁情报、加密恶意软件、僵尸网络、加密 DNS、AI 钓鱼检测
- 🕶️ **隐私管理** —— 跟踪器拦截、广告屏蔽、原生应用追踪、伪装域名识别
- 📋 **黑白名单** —— 域名/TLD 级别的允许与拒绝列表，支持批量导入
- 👨‍👩‍👧 **家长控制** —— 网站/分类/服务屏蔽、安全搜索、YouTube 限制模式、休息时间
- 📊 **日志与统计** —— 查询日志、来源分布、域名 TopN、按时间段筛选
- ⚙️ **多配置管理** —— 同时管理多个 NextDNS 配置，一键切换
- 🔑 **API Key 安全存储** —— Android Keystore 加密保存，本地不落明文

## 📦 与其他 NextDNS 客户端的差异

| | NextDNSManager (本项目) | 官方 NextDNS App | 其他第三方 |
|---|---|---|---|
| 中文界面 | ✅ 全量本地化 | ❌ 仅英文 | ❌ 仅英文 |
| API 完整覆盖 | ✅ | ⚠️ 部分 | ⚠️ 部分 |
| 家长控制管理 | ✅ | ❌ | ❌ |
| 黑白名单批量管理 | ✅ | ⚠️ 单条添加 | ⚠️ 单条添加 |
| 开源 | ✅ | ❌ | 取决于具体项目 |

## 🚀 安装

从 [Releases](https://github.com/baich110/NextDNSManager/releases) 页面下载最新 APK 安装即可。

## 🛠️ 编译

```bash
git clone https://github.com/baich110/NextDNSManager.git
cd NextDNSManager
./gradlew assembleDebug
```

输出位置：`app/build/outputs/apk/debug/app-debug.apk`

## 📲 使用

1. 在 [NextDNS 控制台](https://my.nextdns.io/account) 创建 API Key
2. 在本应用首次启动时填入 API Key 与 Profile ID
3. 即可在客户端内管理你 NextDNS 账户下的所有配置

## 🧰 技术栈

- **语言**：Kotlin
- **UI**：Jetpack Compose + Material 3
- **架构**：MVI / Repository
- **网络**：Retrofit + OkHttp + Kotlinx Serialization
- **存储**：DataStore + Android Keystore（API Key 加密）
- **最低 SDK**：Android 5.0 (API 21)
- **目标 SDK**：Android 14 (API 34)

## 🤝 贡献

Issue / PR 欢迎。如果你发现 NextDNS 新增了 API 字段而本客户端尚未支持，请开 Issue 附上 API 文档片段。

## 📜 License

[MIT License](LICENSE)

本项目与 NextDNS 官方无任何隶属关系。`NextDNS` 是 NextDNS Inc. 的商标。

---

> 如果这个项目对你有帮助，欢迎点亮 ⭐ Star！
