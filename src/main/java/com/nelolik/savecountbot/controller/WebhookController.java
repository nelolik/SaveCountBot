package com.nelolik.savecountbot.controller;

import com.nelolik.savecountbot.Bot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final Bot bot;

    @PostMapping("${bot.webhook.path}")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        log.info("Received update: {}", update.getUpdateId());
        return bot.onWebhookUpdateReceived(update);
    }
}
