package com.nelolik.savecountbot;

import com.nelolik.savecountbot.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bot extends TelegramWebhookBot {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;

    @Value("${bot.webhook.url}")
    private String webhookUrl;

    @Value("${bot.webhook.path}")
    private String webhookPath;

    private final MessageHandler messageHandler;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotPath() {
        return webhookPath;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        try {
            SendMessage message = messageHandler.handle(update);
            if (message != null && StringUtils.hasText(message.getText())) {
                return message;
            }
        } catch (Exception e) {
            log.error("Exception occurred while message handling", e);
        }
        return null;
    }
}
