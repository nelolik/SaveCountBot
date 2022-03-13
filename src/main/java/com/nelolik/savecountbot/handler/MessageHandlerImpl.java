package com.nelolik.savecountbot.handler;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.List;

import static com.nelolik.savecountbot.handler.TextMessageHandler.COMMAND_HELLO;

@Slf4j
@Component
@RequiredArgsConstructor
//@AllArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    @Autowired
    TextMessageHandler textMessageHandler;

    public SendMessage handle(Update update) {
        SendMessage message = new SendMessage();

        if (messageHasText(update)) {
            message = handleTextMessage(update.getMessage());
            log.info("Handled message to Id={} with text: {}",message.getChatId(),
                    message.getText());
        } else if (update.hasCallbackQuery()) {
            String queryData = update.getCallbackQuery().getData();
            log.info("Handled query with data: {}", queryData);
        }
        return message;
    }


    private boolean messageHasText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    private SendMessage handleTextMessage(Message message) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId().toString());
        answer.setText("");

        if (COMMAND_HELLO.equals(message.getText())) {
            return textMessageHandler.handleHelloCommand(message);
        }
        return answer;

    }
}
