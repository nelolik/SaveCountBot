package com.nelolik.savecountbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextMessageHandler {
    String COMMAND_HELLO = "/hello";
    String COMMAND_NEW_RECORD = "/new_record";
    String COMMAND_NEW_COUNT = "/new_count";
    String COMMAND_LIST_OF_RECORDS = "/list_of_records";
    String COMMAND_DELETE_RECORD = "/delete_record";

    SendMessage handleHelloCommand(Message message);

    SendMessage handleNewRecordCommand(Message message);

    SendMessage handleNewCountCommand(Message message);

    SendMessage handleLisOfRecordsCommand(Message message);

    SendMessage handleDeleteRecord(Message message);

    SendMessage handleTextMessage(Message message);
}
