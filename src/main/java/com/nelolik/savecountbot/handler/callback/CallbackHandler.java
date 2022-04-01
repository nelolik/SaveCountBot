package com.nelolik.savecountbot.handler.callback;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackHandler {
    SendMessage handle(CallbackQuery callbackQuery);
}
