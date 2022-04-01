package com.nelolik.savecountbot.handler.message;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.nelolik.savecountbot.handler.message.StringConstants.TEXT_ENTER_NEW_RECORD_NAME;
import static com.nelolik.savecountbot.handler.context.ContextPhase.NEW_RECORD_REQUESTED;
import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_NEW_RECORD;

@Component(COMMAND_NEW_RECORD)
@RequiredArgsConstructor
public class NewRecordCommandHandler implements TextHandler{

    public static final String FORMAT_NEW_RECORD_CREATED = "Record with name %s created.";

    private final ContextHandler contextHandler;

    private final RecordsRepository recordsRepository;

    @Override
    public SendMessage handle(Message message) {
        String text = message.getText().trim();
        if (COMMAND_NEW_RECORD.equals(text)) {
            contextHandler.saveContext(message.getChatId(), NEW_RECORD_REQUESTED);
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
}
