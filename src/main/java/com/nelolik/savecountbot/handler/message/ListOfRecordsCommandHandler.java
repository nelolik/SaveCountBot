package com.nelolik.savecountbot.handler.message;

import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_LIST_OF_RECORDS;
import static com.nelolik.savecountbot.handler.message.StringConstants.FORMAT_LIST_OF_RECORDS;
import static com.nelolik.savecountbot.handler.message.StringConstants.TEXT_NO_RECORD;

@Component(COMMAND_LIST_OF_RECORDS)
@AllArgsConstructor
public class ListOfRecordsCommandHandler implements TextHandler {

    private RecordsRepository recordsRepository;

    private CountsRepository countsRepository;

    @Override
    public SendMessage handle(Message message) {
        Long userId = message.getChatId();
        List<Records> records = recordsRepository.findByUserid(userId);
        SendMessage.SendMessageBuilder messageBuilder = SendMessage.builder()
                .chatId(message.getChatId().toString());
        if (CollectionUtils.isEmpty(records)) {
            return messageBuilder
                    .text(TEXT_NO_RECORD)
                    .build();
        }
        List<String> recordTexts = new ArrayList<>();
        for (Records r :
                records) {
            Long recId = r.getId();
            List<Counts> counts = countsRepository.findByRecordid(recId);
            Long sum = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
            recordTexts.add(String.format(FORMAT_LIST_OF_RECORDS, r.getRecordName(), sum));
        }
        String messageText = String.join("\n", recordTexts);
        return messageBuilder
                .text(messageText)
                .build();
    }
}
