package com.nelolik.savecountbot.handler.message;

import lombok.experimental.UtilityClass;

import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_DELETE_RECORD;

@UtilityClass
public class MessageStringConstants {

    public static final String HELLO_MESSAGE = "Hello! Here you can save your repetitions of anything you want!";
    public static final String CREATE_BTN_TEXT = "New record";

    public static final String FORMAT_NEW_RECORD_CREATED = "Record with name %s created.";
    public static final String TEXT_NAME_IS_NOT_UNIQ = "Record with this name is already presents.";
    public static final String TEXT_ENTER_NEW_RECORD_NAME = "Enter new record name";

    public static final String TEXT_CHOOSE_RECORD = "Choose the record from list to save new value.";

    public static final String TEXT_ERROR_PARSE_LONG = "You entered not a number. Enter integer number to save.";
    public static final String FORMAT_DELETE_RECORD_SUCCEED = "Record named %s removed successfully.";
    public static final String FORMAT_RECORD_NOT_FOUND = "Record with name %s not found.";
    public static final String FORMAT_COUNT_SAVED = "%s: total %s";

    public static final String TEXT_ENTER_DELETE_RECORD_NAME = "Enter the name of the record to delete.";
    public static final String TEXT_WRONG_DELETE_ARG = "You entered wrong argument to " +
            COMMAND_DELETE_RECORD + " command.";

    public static final String FORMAT_LIST_OF_RECORDS = "Record: %s, count: %s";
    public static final String TEXT_NO_RECORD = "You have no records.";

    public static final String BEEN_POSTFIX = "_been";
}
