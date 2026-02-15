# 计划的 webhook

以下 webhook 地址均需要预先在 config.json 中配置 baseUrl 和 secretKey

secretKey 需要在 GET 请求中附在 header 的 authority-api-key 中

所有请求均为 POST {baseUrl}，携带的信息格式的 ts 类型如下

```ts
type McServerWebhookPayload = {
  event: 'server_started' | 'server_stopped' | 'player_joined' | 'player_left';
  playerName?: string;
  currentPlayers?: string[];
};
```

需要实现四个事件推送

1. 服务器启动
2. 服务器关闭
3. 玩家加入
   需要携带 playerName 和 currentPlayers（当前在线玩家列表）
4. 玩家离开
   需要携带 playerName 和 currentPlayers（当前在线玩家列表）

