package com.nelolik.savecountbot;

import com.nelolik.savecountbot.handler.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@RequiredArgsConstructor
@Component
public class Bot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botUsername;

    @Value("${bot.token}")
    private String botToken;


    public Bot(MessageHandler messageHandler) {
        this.messageHandler = messageHandler;
    }

    @Autowired
    private MessageHandler messageHandler;


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

            SendMessage message = messageHandler.handle(update);
            if (message != null && StringUtils.hasText(message.getText())) {
                execute(message);
            }
        } catch (TelegramApiException e) {
            log.error("Exception occured while message handling");
            e.printStackTrace();
        }
    }
}
