package com.nelolik.savecountbot.handler.message;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.nelolik.savecountbot.handler.callback.CallbackData.CREATE_RECORD_BTN_DATA;
import static com.nelolik.savecountbot.handler.message.ApiCommands.COMMAND_HELLO;
import static com.nelolik.savecountbot.handler.message.StringConstants.CREATE_BTN_TEXT;
import static com.nelolik.savecountbot.handler.message.StringConstants.HELLO_MESSAGE;


@Component(COMMAND_HELLO)
public class HelloCommandHandler implements TextHandler {

    @Override
    public SendMessage handle(Message message) {
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
}
