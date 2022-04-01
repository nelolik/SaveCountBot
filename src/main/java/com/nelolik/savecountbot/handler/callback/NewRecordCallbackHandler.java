package com.nelolik.savecountbot.handler.callback;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.nelolik.savecountbot.handler.callback.CallbackData.CREATE_RECORD_BTN_DATA;
import static com.nelolik.savecountbot.handler.callback.CallbackStringConstants.TEXT_ENTER_NEW_RECORD_NAME;
import static com.nelolik.savecountbot.handler.context.ContextPhase.NEW_RECORD_REQUESTED;

@Component(CREATE_RECORD_BTN_DATA)
@AllArgsConstructor
public class NewRecordCallbackHandler implements CallbackHandler{

    private ContextHandler contextHandler;

    @Override
    public SendMessage handle(CallbackQuery callbackQuery) {
        Long userId = callbackQuery.getMessage().getChatId();
        contextHandler.saveContext(userId,
                NEW_RECORD_REQUESTED);
        return SendMessage.builder()
                .text(TEXT_ENTER_NEW_RECORD_NAME)
                .chatId(userId.toString())
                .build();
    }
}
