package com.nelolik.savecountbot.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.nelolik.savecountbot.handler.ContextHandler.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED;

@Component
@RequiredArgsConstructor
@Slf4j
public class CallbackMessageHandlerImpl implements CallbackMessageHandler {

    public static final String TEXT_ENTER_NEW_RECORD_NAME = "Enter new record name";
    public static final String TEXT_ERROR_COMMAND_PROCESSING = "An error occurred during command processing. Try again.";
    public static final String TEXT_ENTER_COUNT = "Enter count you want to save";

    private final ContextHandler contextHandler;

    @Override
    public SendMessage handleCreateRecordCallback(Long userId) {
        contextHandler.saveContext(userId, ContextHandler.ContextPhase.NEW_RECORD_REQUESTED);
        return SendMessage.builder()
                .text(TEXT_ENTER_NEW_RECORD_NAME)
                .chatId(userId.toString())
                .build();
    }

    @Override
    public SendMessage handleSaveCountCallback(String data, Long userId) {
        if (!contextHandler.hasContext(userId)) {
            log.error("Context not found while processing ADD_COUNT callback query");
            return SendMessage.builder()
                    .text(TEXT_ERROR_COMMAND_PROCESSING)
                    .chatId(userId.toString())
                    .build();
        }
        if (data.length() <= ADD_COUNT_BTN_DATA.length()) {
            log.error("Name of record is absent in data of ADD_COUNT callback query");
            return SendMessage.builder()
                    .text(TEXT_ERROR_COMMAND_PROCESSING)
                    .chatId(userId.toString())
                    .build();
        }
        String recordName = data.substring(ADD_COUNT_BTN_DATA.length());
        contextHandler.saveContext(userId, RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        contextHandler.saveRecordName(userId, recordName);
        return SendMessage.builder()
                .text(TEXT_ENTER_COUNT)
                .chatId(userId.toString())
                .build();
    }
}
