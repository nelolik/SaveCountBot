package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.handler.callback.CallbackHandler;
import com.nelolik.savecountbot.handler.callback.CallbackStringConstants;
import com.nelolik.savecountbot.handler.message.MessageStringConstants;
import com.nelolik.savecountbot.handler.message.TextHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final Map<String, TextHandler> textMessageHandlers;

    private final Map<String, CallbackHandler> callbackHandlers;

    public SendMessage handle(Update update) {
        SendMessage message = new SendMessage();
        if (MessageUtils.messageHasText(update)) {
            message = handleTextMessage(update.getMessage());

        } else if (update.hasCallbackQuery()) {
            message = handleCallback(update.getCallbackQuery());
        }
        if (message != null) {
            log.info("Handled message to Id={} with text: {}", message.getChatId(),
                    message.getText());
        }
        return message;
    }

    private SendMessage handleTextMessage(Message message) {
        SendMessage answer = new SendMessage();
        answer.setChatId(message.getChatId().toString());
        answer.setText("");

        String messageText = message.getText();
        if (!StringUtils.hasText(messageText)) {
            log.error("Request with message without text");
            return null;
        }
        String command = MessageUtils.extractCommand(messageText);
        TextHandler textHandler = textMessageHandlers.get(command + MessageStringConstants.BEEN_POSTFIX);
        return textHandler.handle(message);
    }

    private SendMessage handleCallback(CallbackQuery callbackQuery) {
        String queryData = callbackQuery.getData();
        if (!StringUtils.hasText(queryData)) {
            log.error("Callback query with empty data");
            return null;
        }
        String data = MessageUtils.extractCallbackData(queryData);
        return callbackHandlers.get(data + CallbackStringConstants.BEEN_POSTFIX).handle(callbackQuery);
    }
}
