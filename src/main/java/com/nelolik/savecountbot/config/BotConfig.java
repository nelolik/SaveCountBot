package com.nelolik.savecountbot.config;

import com.nelolik.savecountbot.Bot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
public class BotConfig {

    private final Bot bot;

    @Value("${bot.webhook.url}")
    private String webhookUrl;

    public BotConfig(Bot bot) {
        this.bot = bot;
    }

    @PostConstruct
    public void init() {
        try {
            SetWebhook setWebhook = SetWebhook.builder()
                    .url(webhookUrl)
                    .build();

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(bot, setWebhook);

            log.info("Webhook registered successfully: {}", webhookUrl);
        } catch (TelegramApiException e) {
            log.error("Failed to register webhook", e);
        }
    }
}
