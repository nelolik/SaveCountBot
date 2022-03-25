package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class TextMessageHandlerImpl implements TextMessageHandler {

    public static final String HELLO_MESSAGE = "Hello! Here you can save your repititions of anything you want!";
    public static final String CREATE_BTN_DATA = "NEW_RECORD";
    public static final String CREATE_BTN_TEXT = "New record";

    public static final String TEXT_NO_RECORD = "You have no records.";
    public static final String TEXT_ENTER_RECORD_NAME = "Enter new record name";

    private final RecordsRepository recordsRepository;

    private final CountsRepository countsRepository;

    private final ContextHandler contextHandler;

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
        String text = message.getText().trim();
        if (COMMAND_NEW_RECORD.equals(text)) {
            contextHandler.saveContext(message.getChatId(), ContextHandler.ContextPhase.NEW_RECORD_REQUESTED);
            return SendMessage.builder()
                    .text(TEXT_ENTER_RECORD_NAME)
                    .chatId(message.getChatId().toString())
                    .build();
        } else if (text.startsWith(COMMAND_NEW_RECORD) && text.length() > COMMAND_NEW_RECORD.length()) {
            String[] args = text.substring(COMMAND_NEW_RECORD.length()).trim().split(" ");
            Long userId = message.getChatId();
            if (args.length == 1 && !args[0].isBlank()) {
                recordsRepository.save(new Records(0l, userId, args[0]));
                contextHandler.deleteContext(userId);
                return SendMessage.builder()
                        .text("Record with name " + args[0] + " created.")
                        .build();
            } else if (args.length > 1 && !args[0].isBlank() && !args[0].isBlank()) {
                String recordName = args[0];
                String initCountString = args[1];
                Long initCount = Long.getLong(initCountString);
                Records saved = recordsRepository.save(new Records(0l, userId, recordName));
                if (initCount != null) {
                    countsRepository.save(new Counts(0l, saved.getId(), initCount,
                            new Date(System.currentTimeMillis())));
                    contextHandler.deleteContext(userId);
                    return SendMessage.builder()
                            .text("Record with name " + recordName
                                    + " and init count " + initCount + "created.")
                            .build();
                } else {
                    return SendMessage.builder()
                            .text("Record with name " + recordName + " created.")
                            .build();
                }
            }

        }

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

    @Override
    public SendMessage handleTextMessage(Message message) {
        if (!contextHandler.hasContext(message.getChatId())) {
            return null;
        }
        return null;
    }
}
