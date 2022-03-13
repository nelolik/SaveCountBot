package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class TextMessageHandlerImpl implements TextMessageHandler {

    private static final String HELLO_MESSAGE = "Hello! Here you can save your repititions of anything you want!";
    private static final String CREATE_BTN_DATA = "NEW_RECORD";

    @Autowired
    private RecordsRepository recordsRepository;

    @Override
    public SendMessage handleHelloCommand(Message message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton btn = InlineKeyboardButton.builder()
                .text("Create record name")
                .callbackData(CREATE_BTN_DATA)
                .build();
        List<InlineKeyboardButton> raw = List.of(btn);
        markup.setKeyboard(List.of(raw));

        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(HELLO_MESSAGE)
                .replyMarkup(markup)
                .build();
    }

    @Override
    public SendMessage handleNewRecordCommand(Message message) {
        return null;
    }

    @Override
    public SendMessage handleLisOfRecordsCommand(Message message) {
        List<Records> records = recordsRepository.findByUserid(message.getContact().getUserId());

        return null;
    }

    @Override
    public SendMessage handleDeleteRecord(Message message) {
        return null;
    }
}
