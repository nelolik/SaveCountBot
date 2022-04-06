package com.nelolik.savecountbot.handler.callback;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.nelolik.savecountbot.handler.callback.CallbackData.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.callback.CallbackStringConstants.*;
import static com.nelolik.savecountbot.handler.context.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED;

@Component(ADD_COUNT_BTN_DATA + BEEN_POSTFIX)
@RequiredArgsConstructor
@Slf4j
public class AddCountCallbackHandler implements CallbackHandler {

    private final ContextHandler contextHandler;

    @Override
    public SendMessage handle(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getMessage().getChatId();
        String data = callbackQuery.getData().trim();
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
        String recordName = data.substring(ADD_COUNT_BTN_DATA.length()).trim();
        contextHandler.saveContext(userId, RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        contextHandler.saveRecordName(userId, recordName);
        return SendMessage.builder()
                .text(TEXT_ENTER_COUNT)
                .chatId(userId.toString())
                .build();
    }
}
