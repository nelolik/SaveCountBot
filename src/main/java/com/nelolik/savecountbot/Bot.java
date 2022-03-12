package com.nelolik.savecountbot;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername = "alepars_bot";

    @Value("${bot.token}")
    private String botToken="1242854240:AAGM0dtPbry7_umSGh3FPStcrMdPUZzT8PE";

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(update.getMessage().getChatId().toString());
            message.setText(update.getMessage().getText());
            execute(message);
            log.info("Sent message to Id={} with text: {}",update.getMessage().getChatId().toString(),
                    update.getMessage().getText());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
