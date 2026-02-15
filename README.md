# Server Webhook Mod

这是一个基于 Fabric 的 Minecraft 模组，用于将服务器的关键事件（启动、关闭、玩家进出）通过 Webhook 推送到外部服务。

## 功能特性

该模组会在以下事件发生时向配置的 URL 发送 POST 请求：

1.  **Server Started**: 服务器启动完成。
2.  **Server Stopped**: 服务器正在关闭。
3.  **Player Joined**: 玩家加入服务器。
4.  **Player Left**: 玩家离开服务器。

## 配置文件

模组第一次运行后，会在 `config/server_webhook.json` 生成配置文件。

```json
{
  "baseUrl": "",
  "secretKey": "your_secret_key"
}
```

*   `baseUrl`: Webhook 接收地址。
*   `secretKey`: 用于验证请求的密钥（在请求头 `authority-api-key` 中发送）。

## Webhook 格式

所有请求均为 `POST` 方法，请求体为 JSON 格式：

**Body:**
```ts
type McServerWebhookPayload = {
  event: 'server_started' | 'server_stopped' | 'player_joined' | 'player_left'; // 目前支持的事件类型
  playerName?: string; // 仅在 player_joined 或 player_left 时存在
  currentPlayers?: string[]; // 当前在线玩家名称列表
};
```

**示例 Payload:**

```json
{
  "event": "player_joined",
  "playerName": "Steve",
  "currentPlayers": [
    "Steve",
    "Alex"
  ]
}
```

## 安装与构建

### 依赖
*   Minecraft 1.20.1
*   Fabric Loader
*   Fabric API

### 构建
在项目根目录运行：

Windows:
```powershell
./gradlew build
```

Linux/Mac:
```bash
./gradlew build
```

构建产物位于 `build/libs/` 目录下。
