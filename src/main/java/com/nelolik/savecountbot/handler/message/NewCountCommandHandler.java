package com.nelolik.savecountbot.handler.message;

import com.nelolik.savecountbot.handler.context.ContextHandler;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

import static com.nelolik.savecountbot.handler.callback.CallbackData.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.context.ContextPhase.SAVE_COUNT_REQUESTED;
import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_NEW_COUNT;
import static com.nelolik.savecountbot.handler.message.MessageStringConstants.BEEN_POSTFIX;
import static com.nelolik.savecountbot.handler.message.MessageStringConstants.TEXT_CHOOSE_RECORD;

@Component(COMMAND_NEW_COUNT + BEEN_POSTFIX)
@RequiredArgsConstructor
public class NewCountCommandHandler implements TextHandler {

    private final ContextHandler contextHandler;

    private final RecordsRepository recordsRepository;

    @Override
    public SendMessage handle(Message message) {
        String text = message.getText().trim();
        Long userId = message.getChatId();
        contextHandler.deleteContext(userId);
        if (COMMAND_NEW_COUNT.equals(text)) {
            contextHandler.saveContext(userId, SAVE_COUNT_REQUESTED);
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
}
