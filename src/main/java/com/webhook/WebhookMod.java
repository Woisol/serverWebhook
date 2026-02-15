package com.webhook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.webhook.config.WebhookConfig;
import com.webhook.webhook.WebhookSender;
import com.webhook.webhook.WebhookSender.Payload;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public class WebhookMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("server-webhook");

    @Override
    public void onInitialize() {

        // Load configuration
        WebhookConfig.load();

        // 1. Server Started
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            WebhookSender.send(new Payload("server_started"));
        });

        // 2. Server Stopped (Using STOPPING to ensure network stack is still active)
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            WebhookSender.send(new Payload("server_stopped"));
        });

        // 3. Player Joined
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            String playerName = handler.getPlayer().getName().getString();
            // Get current list and ensure player is included
            List<String> players = new ArrayList<>(Arrays.asList(server.getPlayerManager().getPlayerNames()));
            if (!players.contains(playerName)) {
                players.add(playerName);
            }
            WebhookSender.send(new Payload("player_joined", playerName, players));
        });

        // 4. Player Left
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            String playerName = handler.getPlayer().getName().getString();
            // Get current list and ensure player is removed (since they might still be in
            // the list during disconnect)
            List<String> players = new ArrayList<>(Arrays.asList(server.getPlayerManager().getPlayerNames()));
            players.remove(playerName);
            WebhookSender.send(new Payload("player_left", playerName, players));
        });
        LOGGER.info("Server Webhook initialized successfully.");
    }
}
