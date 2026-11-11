# EchoMusic

基于 Jetpack Compose 的某抑云音乐 Android 客户端，采用 MVVM 架构。

接口有两种模式：默认直连某抑云，也可以切换为连接自建的 [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi) 服务。

---

## 功能

- 扫码登录、Cookie 登录、游客模式
- 发现页、播客、我的、K歌、云村
- 搜索：热搜、输入联想、搜索历史
- 歌单详情、歌单广场、歌曲播放、逐字歌词高亮
- 全屏播放页、底部迷你播放器、通知栏控制
- 播放模式：列表循环、单曲循环、随机
- 本地音乐扫描与播放
- 最近播放
- 歌曲点赞、我喜欢的音乐
- 评论、楼层评论、评论点赞
- 视频瀑布流、视频播放、视频点赞与收藏
- 播客：推荐电台、电台节目列表、节目播放
- Native 音效：5 段 EQ 与预设
- 音频缓存、视频缓存，视频仅在 Wi-Fi 写入
- 多账号数据隔离
- 主题切换与持久化

---

## 技术栈

| 类别 | 技术 |
|------|------|
| UI | Jetpack Compose + Material 3 |
| 架构 | MVVM，ViewModel + Compose State |
| 导航 | Navigation Compose 2.9.7 |
| 依赖注入 | Dagger Hilt 2.59.2 |
| 网络 | Retrofit 3.0.0 + OkHttp 5.3.2 |
| 播放器 | Media3 ExoPlayer 1.6.1 |
| 图片 | Coil 2.7.0 |
| 事件总线 | EventBus 3.3.1 |
| 本地存储 | MMKV 2.4.0 |
| 分页 | Paging 3.4.2 |
| 音效 | C++ / CMake / NDK，JNI |
| 语言 | Kotlin 2.2.x |
| SDK | minSdk 24，targetSdk 36 |

---

## 项目结构

```
app/src/main/
├── cpp/                        # native 音效
└── java/xyz/larkzhh/echomusic/
    ├── broadcast/              # 通知栏控制
    ├── compose/                # 通用 UI 组件
    ├── core/                   # 配置、播放控制、缓存、音效、本地存储
    ├── hilt/                   # 依赖注入
    ├── http/                   # API 与拦截器
    │   └── local/              # 直连某抑云的实现
    ├── model/                  # 数据模型
    ├── nav/                    # 路由
    ├── service/                # 播放前台服务
    ├── ui/page/                # 页面
    ├── ui/theme/               # 主题
    ├── utils/
    └── viewmodel/
```

---

## 快速开始

环境：JDK 17、Android SDK 36、NDK 28.2、CMake 3.22.1。

### 直连模式

默认构建即直连，不需要启动任何后端：

```bash
./gradlew :app:assembleDebug
```

### 后端模式

后端使用开源项目 [NeteaseCloudMusicApi](https://github.com/Binaryify/NeteaseCloudMusicApi)，需要另外准备：

```bash
git clone https://github.com/Binaryify/NeteaseCloudMusicApi.git
cd NeteaseCloudMusicApi
npm install
npm start
```

服务默认监听 3000 端口。

然后修改 `core/AppConfig.kt` 中的 `BASE_URL`，模拟器用 `10.0.2.2`，真机用局域网 IP，构建时关掉直连：

```bash
./gradlew :app:assembleDebug -PlocalApi=false
```

也可以在 `gradle.properties` 里改 `localApi`。

---

## 权限

| 权限 | 用途 |
|------|------|
| `INTERNET` | 网络请求 |
| `ACCESS_NETWORK_STATE` | 判断 Wi-Fi |
| `CAMERA` | 扫码登录 |
| `READ_MEDIA_AUDIO` | 扫描本地音乐，Android 13 及以上 |
| `READ_EXTERNAL_STORAGE` | 扫描本地音乐，Android 12 及以下 |
| `FOREGROUND_SERVICE` | 后台播放 |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | 后台播放 |
| `VIBRATE` | 交互反馈 |

---

## 已知限制

- 某抑云风控会导致扫码失败，点赞等写操作返回 -460
- 第三方接口无法写入官方客户端的云端最近播放
- 非会员账号实际码率为 128k
- 系统深色模式下颜色主题不生效
- 直连模式的部分读接口只有内存缓存，重启后失效

---

## 免责声明

本项目仅供学习交流，不得用于商业用途。音乐版权归网易公司所有。
