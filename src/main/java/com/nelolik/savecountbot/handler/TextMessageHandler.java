package com.nelolik.savecountbot.handler;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface TextMessageHandler {
    public static final String COMMAND_HELLO = "/hello";
    public static final String COMMAND_NEW_RECORD = "/new_record";
    public static final String COMMAND_LIST_OF_RECORDS = "/list_of_records";
    public static final String COMMAND_DELETE_RECORD = "/delete_record";

    SendMessage handleHelloCommand(Message message);

    SendMessage handleNewRecordCommand(Message message);

    SendMessage handleLisOfRecordsCommand(Message message);

    SendMessage handleDeleteRecord(Message message);
}
