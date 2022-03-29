package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.nelolik.savecountbot.handler.CallbackMessageHandler.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.CallbackMessageHandler.CREATE_RECORD_BTN_DATA;

@Component
@AllArgsConstructor
public class TextMessageHandlerImpl implements TextMessageHandler {

    public static final String HELLO_MESSAGE = "Hello! Here you can save your repititions of anything you want!";
    public static final String CREATE_BTN_TEXT = "New record";

    public static final String TEXT_NO_RECORD = "You have no records.";
    public static final String TEXT_ENTER_NEW_RECORD_NAME = "Enter new record name";
    public static final String FORMAT_NEW_RECORD_CREATED = "Record with name %s created.";
    public static final String TEXT_NAME_IS_NOT_UNIQ = "Record with this name is already presents.";

    public static final String TEXT_CHOOSE_RECORD = "Choose the record from list to save new value.";
    public static final String TEXT_ERROR_PARSE_LONG = "You entered not a number. Enter integer number to save.";

    public static final String TEXT_ENTER_DELETE_RECORD_NAME = "Enter the name of the record to delete.";
    public static final String FORMAT_DELETE_RECORD_SUCCEED = "Record named %s removed successfully.";
    public static final String FORMAT_RECORD_NOT_FOUND = "Record with name %s not found.";
    public static final String TEXT_WRONG_DELETE_ARG = "You entered wrong argument to " +
            COMMAND_DELETE_RECORD + " command.";

    private final RecordsRepository recordsRepository;

    private final CountsRepository countsRepository;

    private final ContextHandler contextHandler;

    @Override
    public SendMessage handleHelloCommand(Message message) {
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        InlineKeyboardButton btn = InlineKeyboardButton.builder()
                .text(CREATE_BTN_TEXT)
                .callbackData(CREATE_RECORD_BTN_DATA)
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
                    .text(TEXT_ENTER_NEW_RECORD_NAME)
                    .chatId(message.getChatId().toString())
                    .build();
        } else if (text.startsWith(COMMAND_NEW_RECORD) && text.length() > COMMAND_NEW_RECORD.length()) {
            String arg = text.substring(COMMAND_NEW_RECORD.length()).trim();
            Long userId = message.getChatId();
            if (!arg.isBlank()) {
                recordsRepository.save(new Records(0L, userId, arg));
                contextHandler.deleteContext(userId);
                return SendMessage.builder()
                        .chatId(message.getChatId().toString())
                        .text(String.format(FORMAT_NEW_RECORD_CREATED, arg))
                        .build();
            }
        }
        return null;
    }

    @Override
    public SendMessage handleNewCountCommand(Message message) {
        String text = message.getText().trim();
        Long userId = message.getChatId();
        contextHandler.deleteContext(userId);
        if (COMMAND_NEW_COUNT.equals(text)) {
            contextHandler.saveContext(userId, ContextHandler.ContextPhase.SAVE_COUNT_REQUESTED);
            List<Records> records = recordsRepository.findByUserid(userId);
            InlineKeyboardMarkup markup= new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
            for (Records r :
                    records) {
                InlineKeyboardButton btn = new InlineKeyboardButton();
                btn.setText(r.getRecordName());
                btn.setCallbackData(ADD_COUNT_BTN_DATA + r.getRecordName());
                keyboard.add(List.of(btn));
            }
            markup.setKeyboard(keyboard);
            return SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text(TEXT_CHOOSE_RECORD)
                    .replyMarkup(markup)
                    .build();
        }

        return null;
    }

    @Override
    public SendMessage handleLisOfRecordsCommand(Message message) {
        Long userId = message.getChatId();
        List<Records> records = recordsRepository.findByUserid(userId);
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(message.getChatId().toString());
        if (CollectionUtils.isEmpty(records)) {
            return messageBuilder
                    .text(TEXT_NO_RECORD)
                    .build();
        }
        String messageText = "";
        for (Records r :
                records) {
            Long recId = r.getId();
            List<Counts> counts = countsRepository.findByRecordid(recId);
            Long sum = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
            messageText += "Record: " + r.getRecordName() + ", count: " + sum + "\n";
        }
        return messageBuilder
                .text(messageText)
                .build();
    }

    @Override
    public SendMessage handleDeleteRecord(Message message) {
        Long userId = message.getChatId();
        String text = message.getText().trim();
        if(COMMAND_DELETE_RECORD.equals(text)) {
            contextHandler.saveContext(userId, ContextHandler.ContextPhase.DELETE_RECORD_REQUESTED);
            return SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text(TEXT_ENTER_DELETE_RECORD_NAME)
                    .build();
        } else if (text.startsWith(COMMAND_DELETE_RECORD) && text.length() > COMMAND_DELETE_RECORD.length()) {
            String arg = text.substring(COMMAND_DELETE_RECORD.length()).trim();
            String messageText;
            contextHandler.deleteContext(userId);
            if (!arg.isBlank()) {
                List<Records> recordFromDb = recordsRepository.findByRecordNameAndUserid(arg, userId);
                if (!CollectionUtils.isEmpty(recordFromDb)) {
                    Records records = recordFromDb.get(0);
                    recordsRepository.deleteById(records.getId());
                    countsRepository.deleteAllByRecordid(records.getId());
                    messageText = String.format(FORMAT_DELETE_RECORD_SUCCEED, records.getRecordName());
                } else {
                    messageText = String.format(FORMAT_RECORD_NOT_FOUND, arg);
                }
            } else {
                messageText = TEXT_WRONG_DELETE_ARG;
            }
            return SendMessage.builder()
                    .chatId(message.getChatId().toString())
                    .text(messageText)
                    .build();
        }
        return null;
    }

    @Override
    public SendMessage handleTextMessage(Message message) {
        Long userId = message.getChatId();
        String text = message.getText().trim();
        if (!contextHandler.hasContext(userId) || text.isBlank()) {
            return null;
        }

        String answerText = "";
        ContextHandler.ContextPhase context = contextHandler.getContext(userId);
        if (context == ContextHandler.ContextPhase.NEW_RECORD_REQUESTED) {
            if (!isRecordPresent(text, userId)) {
                recordsRepository.save(new Records(0L, userId, text));
                contextHandler.deleteContext(userId);
                answerText = String.format(FORMAT_NEW_RECORD_CREATED, text);
            } else {
                answerText = TEXT_NAME_IS_NOT_UNIQ;
            }
        } else if (context == ContextHandler.ContextPhase.DELETE_RECORD_REQUESTED) {
            if (isRecordPresent(text, userId)) {
                Records recordToDelete = recordsRepository.findByRecordNameAndUserid(text, userId).get(0);
                recordsRepository.deleteById(recordToDelete.getId());
                countsRepository.deleteAllByRecordid(recordToDelete.getId());
                contextHandler.deleteContext(userId);
                answerText = String.format(FORMAT_DELETE_RECORD_SUCCEED, text);
            } else {
                answerText = String.format(FORMAT_RECORD_NOT_FOUND, text);
            }
        } else if (context == ContextHandler.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED) {
            try {
                Long longValue = Long.parseLong(text);
                String nameToSave = contextHandler.getRecordName(userId);
                Records record = recordsRepository.findByRecordNameAndUserid(nameToSave, userId).get(0);
                countsRepository.save(new Counts(0L, record.getId(), longValue,
                        new Date(System.currentTimeMillis())));
                contextHandler.deleteContext(userId);
                List<Counts> counts = countsRepository.findByRecordid(record.getId());
                Long sum = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
                answerText = nameToSave + ": total " + sum.toString();
            } catch (NumberFormatException e) {
                answerText = TEXT_ERROR_PARSE_LONG;
            }
        }
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(answerText)
                .build();
    }

    private boolean isRecordPresent(String recordName, Long userId) {
        return  !recordsRepository.findByRecordNameAndUserid(recordName, userId).isEmpty();
    }
}
