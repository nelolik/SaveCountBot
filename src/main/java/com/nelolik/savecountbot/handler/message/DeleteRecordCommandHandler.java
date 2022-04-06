package com.nelolik.savecountbot.handler.message;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.nelolik.savecountbot.handler.context.ContextPhase.DELETE_RECORD_REQUESTED;
import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_DELETE_RECORD;
import static com.nelolik.savecountbot.handler.message.MessageStringConstants.*;

@Component(COMMAND_DELETE_RECORD + BEEN_POSTFIX)
@RequiredArgsConstructor
public class DeleteRecordCommandHandler implements TextHandler {

    private final ContextHandler contextHandler;

    private final RecordsRepository recordsRepository;

    private final CountsRepository countsRepository;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getChatId();
        String text = message.getText().trim();
        if(COMMAND_DELETE_RECORD.equals(text)) {
            contextHandler.saveContext(userId, DELETE_RECORD_REQUESTED);
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
}
