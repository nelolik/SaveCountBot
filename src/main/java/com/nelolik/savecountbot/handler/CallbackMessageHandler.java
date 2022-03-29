package com.nelolik.savecountbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface CallbackMessageHandler {

    String CREATE_RECORD_BTN_DATA = "NEW_RECORD";
    String ADD_COUNT_BTN_DATA = "ADD_COUNT";

    SendMessage handleCreateRecordCallback(Long userId);

    SendMessage handleSaveCountCallback(String data, Long userId);
}
