package com.webhook.webhook;

import java.math.BigInteger;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webhook.config.WebhookConfig;
import com.google.gson.Gson;

public class WebhookSender {
    public static final Logger LOGGER = LoggerFactory.getLogger("server-webhook");
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .connectTimeout(Duration.ofSeconds(10))
            .build();
    private static final Gson GSON = new Gson();

    public static void send(Payload payload) {
        WebhookConfig config = WebhookConfig.getInstance();
        if (config.baseUrl == null || config.baseUrl.isEmpty()) {
            // Might be default config, just log a warning once or debug
            LOGGER.error("Webhook URL not configured");
            return;
        }

        // 生成 secretkey
        String secretKey;
        try{
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(config.secretKey.getBytes());
            secretKey = new BigInteger(1, md.digest()).toString(16);// 转换为16进制数
        }catch(Exception e){
            LOGGER.error("Failed to create MD5 MessageDigest", e);
            return;
        }

        String json = GSON.toJson(payload);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(config.baseUrl))
                .header("Content-Type", "application/json")
                .header("authority-api-key", secretKey)
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

                // ？异步发送请求，避免阻塞服务器主线程
        CompletableFuture.runAsync(() -> {
            try {
                HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
                if (response.statusCode() >= 200 && response.statusCode() < 300) {
                    LOGGER.info("Webhook sent successfully for event: " + payload.event);
                } else {
                    LOGGER.warn("Webhook failed with status " + response.statusCode() + ": " + response.body());
                }
            } catch (Exception e) {
                LOGGER.error("Failed to send webhook", e);
            }
        });
    }

    public static class Payload {
        String event;
        String playerName;
        List<String> currentPlayers;

        public Payload(String event) {
            this.event = event;
        }

        public Payload(String event, String playerName, List<String> currentPlayers) {
            this.event = event;
            this.playerName = playerName; // Can be null
            this.currentPlayers = currentPlayers; // Can be null
        }
    }
}
