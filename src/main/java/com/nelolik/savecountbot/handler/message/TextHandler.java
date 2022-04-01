package com.nelolik.savecountbot.handler.message;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextHandler {
    SendMessage handle(Message message);
}
