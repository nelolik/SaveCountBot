package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
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
    private static final String CREATE_BTN_TEXT = "New record";

    private static final String TEXT_NO_RECORD = "You have no records.";

    @Autowired
    private RecordsRepository recordsRepository;

    @Autowired
    private CountsRepository countsRepository;

    @Override
    public SendMessage handleHelloCommand(Message message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton btn = InlineKeyboardButton.builder()
                .text(CREATE_BTN_TEXT)
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
        Long userId = message.getChatId();
        List<Records> records = recordsRepository.findByUserid(userId);
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(message.getChatId().toString());
        if (records == null || records.size() == 0) {
            return messageBuilder
                    .text(TEXT_NO_RECORD)
                    .build();
        }
        String messageText = "";
        for (Records r :
                records) {
            Long recId = r.getId();
            List<Counts> counts = countsRepository.findByRecordid(recId);
            Long sum = counts.stream().map(c -> c.getCount()).reduce((x, y) -> x + y).orElse(0l);
            messageText += "Record: " + r.getRecordName() + ", count: " + sum + "\n";
        }
        return messageBuilder
                .text(messageText)
                .build();
    }

    @Override
    public SendMessage handleDeleteRecord(Message message) {
        return null;
    }
}
