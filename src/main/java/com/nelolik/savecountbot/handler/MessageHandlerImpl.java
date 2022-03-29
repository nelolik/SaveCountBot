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

import static com.nelolik.savecountbot.handler.CallbackMessageHandler.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.CallbackMessageHandler.CREATE_RECORD_BTN_DATA;
import static com.nelolik.savecountbot.handler.TextMessageHandler.*;

@Slf4j
@Component
@AllArgsConstructor
public class MessageHandlerImpl implements MessageHandler {

    private final TextMessageHandler textMessageHandler;

    private final CallbackMessageHandler callbackMessageHandler;

    public SendMessage handle(Update update) {
        SendMessage message = new SendMessage();
        if (messageHasText(update)) {
            message = handleTextMessage(update.getMessage());
            if (message != null) {
                log.info("Handled message to Id={} with text: {}", message.getChatId(),
                        message.getText());
            }
        } else if (update.hasCallbackQuery()) {
            String queryData = update.getCallbackQuery().getData();
            message = handleCallbackMessage(update.getCallbackQuery());
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

        String messageText = message.getText();
        if (messageText == null || messageText.isBlank()) {
            return null;
        }
        if (COMMAND_HELLO.equals(messageText)) {
            return textMessageHandler.handleHelloCommand(message);
        } else if (messageText.startsWith(COMMAND_LIST_OF_RECORDS)) {
            return textMessageHandler.handleLisOfRecordsCommand(message);
        } else if (messageText.startsWith(COMMAND_NEW_RECORD)) {
            return textMessageHandler.handleNewRecordCommand(message);
        } else if (messageText.startsWith(COMMAND_NEW_COUNT)) {
          return textMessageHandler.handleNewCountCommand(message);
        } else if (messageText.startsWith(COMMAND_DELETE_RECORD)) {
            return textMessageHandler.handleDeleteRecord(message);
        } else {
            return textMessageHandler.handleTextMessage(message);
        }
    }

    private SendMessage handleCallbackMessage(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData().trim();
        Message message = callbackQuery.getMessage();
        if (data.startsWith(CREATE_RECORD_BTN_DATA)) {
            return callbackMessageHandler.handleCreateRecordCallback(message.getChatId());
        } else if (data.startsWith(ADD_COUNT_BTN_DATA)) {
            return callbackMessageHandler.handleSaveCountCallback(data, message.getChatId());
        } else {
            log.error("Callback query with unspecified callback data: {}", data);
            return null;
        }
    }
}
