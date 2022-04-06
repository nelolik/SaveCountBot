package com.nelolik.savecountbot.handler.callback;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.nelolik.savecountbot.handler.callback.CallbackData.CREATE_RECORD_BTN_DATA;
import static com.nelolik.savecountbot.handler.callback.CallbackStringConstants.BEEN_POSTFIX;
import static com.nelolik.savecountbot.handler.callback.CallbackStringConstants.TEXT_ENTER_NEW_RECORD_NAME;
import static com.nelolik.savecountbot.handler.context.ContextPhase.NEW_RECORD_REQUESTED;

@Component(CREATE_RECORD_BTN_DATA + BEEN_POSTFIX)
@RequiredArgsConstructor
public class NewRecordCallbackHandler implements CallbackHandler {

    private final ContextHandler contextHandler;

    @Override
    public SendMessage handle(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getMessage().getChatId();
        contextHandler.saveContext(userId, NEW_RECORD_REQUESTED);
        return SendMessage.builder()
                .text(TEXT_ENTER_NEW_RECORD_NAME)
                .chatId(userId.toString())
                .build();
    }
}
