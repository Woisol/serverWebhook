package com.webhook.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class WebhookConfig {
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("webhook-config.json")
            .toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public String baseUrl = "";
    public String secretKey = "your_secret_key";

    private static WebhookConfig instance;

    public static WebhookConfig getInstance() {
        if (instance == null) {
            load();
        }
        return instance;
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                instance = GSON.fromJson(reader, WebhookConfig.class);
            } catch (IOException e) {
                e.printStackTrace();
                // Fallback to default if loading fails
                instance = new WebhookConfig();
            }
        } else {
            instance = new WebhookConfig();
            save();
        }
    }

    public static void save() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            // ! ？？意思是只会写 public 的两个属性！
            // GSON.toJson(instance) 会将 instance 对象转换为 JSON 字符串
            GSON.toJson(instance, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
