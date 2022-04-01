package com.nelolik.savecountbot.handler.message;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import com.nelolik.savecountbot.handler.context.ContextPhase;
import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Date;
import java.util.List;

import static com.nelolik.savecountbot.handler.context.ContextPhase.*;
import static com.nelolik.savecountbot.handler.message.ApiCommands.NO_COMMAND;
import static com.nelolik.savecountbot.handler.message.StringConstants.*;

@Component(NO_COMMAND)
@AllArgsConstructor
public class NoCommandTextHandler implements TextHandler {

    private ContextHandler contextHandler;

    private RecordsRepository recordsRepository;

    private CountsRepository countsRepository;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getChatId();
        String text = message.getText().trim();
        if (!contextHandler.hasContext(userId) || text.isBlank()) {
            return null;
        }

        String answerText = "";
        ContextPhase context = contextHandler.getContext(userId);
        if (context == NEW_RECORD_REQUESTED) {
            answerText = trySaveNewRecordAndGetAnswerText(text, userId);
        } else if (context == DELETE_RECORD_REQUESTED) {
            answerText = tryDeleteRecordAndGetAnswerText(text, userId);
        } else if (context == RECORD_NAME_FOR_SAVE_COUNT_ENTERED) {
            answerText = trySaveCountAndGetAnswerText(text, userId);
        }
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text(answerText)
                .build();
    }

    private boolean isRecordPresent(String recordName, Long userId) {
        return  !recordsRepository.findByRecordNameAndUserid(recordName, userId).isEmpty();
    }

    private String trySaveNewRecordAndGetAnswerText(String recordName, Long userId) {
        if (!isRecordPresent(recordName, userId)) {
            recordsRepository.save(new Records(0L, userId, recordName));
            contextHandler.deleteContext(userId);
            return String.format(FORMAT_NEW_RECORD_CREATED, recordName);
        } else {
            return TEXT_NAME_IS_NOT_UNIQ;
        }
    }

    private String tryDeleteRecordAndGetAnswerText(String recordName, Long userId) {
        if (isRecordPresent(recordName, userId)) {
            Records recordToDelete = recordsRepository.findByRecordNameAndUserid(recordName, userId).get(0);
            recordsRepository.deleteById(recordToDelete.getId());
            countsRepository.deleteAllByRecordid(recordToDelete.getId());
            contextHandler.deleteContext(userId);
            return String.format(FORMAT_DELETE_RECORD_SUCCEED, recordName);
        } else {
            return String.format(FORMAT_RECORD_NOT_FOUND, recordName);
        }
    }

    private String trySaveCountAndGetAnswerText(String input, Long userId) {
        try {
            Long count = Long.parseLong(input);
            String recordName = contextHandler.getRecordName(userId);
            saveCountToRepository(count, recordName, userId);
            Long sum = getSumOfCountsForRecord(recordName, userId);
            return String.format(FORMAT_COUNT_SAVED, recordName, sum);
        } catch (NumberFormatException e) {
            return TEXT_ERROR_PARSE_LONG;
        }
    }
    private void saveCountToRepository(Long count, String recordName, Long userId) {
        Records record = recordsRepository.findByRecordNameAndUserid(recordName, userId).get(0);
        countsRepository.save(new Counts(0L, record.getId(), count,
                new Date(System.currentTimeMillis())));
        contextHandler.deleteContext(userId);
    }

    private Long getSumOfCountsForRecord(String recordName, Long userId) {
        Records record = recordsRepository.findByRecordNameAndUserid(recordName, userId).get(0);
        List<Counts> counts = countsRepository.findByRecordid(record.getId());
        return counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
    }
}
