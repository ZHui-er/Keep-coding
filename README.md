# BLE Health Demo

一个 Android 原生 BLE 穿戴设备健康数据 MVP。由于当前没有真实设备和厂商 GATT 协议，本版本使用可重复触发的 Mock 场景演示设备发现、连接、健康数据同步和 Room 持久化。

> App 中的全部设备与健康数据均为 Mock 数据，不代表真实测量结果，也不提供医疗建议。

## 已实现

- Kotlin + Jetpack Compose + Material 3。
- 首页、设备、历史三页底部导航。
- Mock 设备扫描、连接、断开和设备信息。
- 心率、血氧、步数、睡眠同步。
- Room 本地持久化，重启后保留历史。
- 七种确定性演示场景：正常、无设备、连接失败、同步失败、部分数据、存储失败、重复数据。
- 健康指标范围校验和基础单元测试。
- Repository 边界，后续可替换为真实 BLE/GATT 实现。

## 开发环境

- Android Studio（支持 Android Gradle Plugin 8.9）。
- JDK 17。
- Android SDK 35。
- Gradle 8.11.1（仓库包含 Wrapper 配置）。

## 运行

1. 使用 Android Studio 打开仓库根目录。
2. 确认 Gradle JDK 设置为 JDK 17，并安装 Android SDK 35。
3. 同步 Gradle。
4. 在 API 26 或更高版本的模拟器或真机运行 `app`。
5. 在顶部“场景”菜单选择演示场景，再进入设备页执行扫描和连接。

命令行构建：

```shell
./gradlew testDebugUnitTest assembleDebug
```

Windows：

```powershell
.\gradlew.bat testDebugUnitTest assembleDebug
```

## 项目说明

- [执行计划](plan.md)
- `data/mock`：Mock 设备、健康数据和演示场景。
- `data/repository`：未来真实 BLE 实现需要遵循的接口。
- `data/local`：Room Entity、DAO 和数据库。
- `ui`：Compose 页面、主题和状态协调。

真实 BLE 接入、GATT 服务发现、Characteristic 解析、权限、后台同步和自动重连见执行计划的“后续安排”。
