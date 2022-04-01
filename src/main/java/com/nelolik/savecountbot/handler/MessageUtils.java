package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.handler.callback.CallbackData;
import com.nelolik.savecountbot.handler.message.ApiCommands;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.List;

import static com.nelolik.savecountbot.handler.message.ApiCommands.NO_COMMAND;

@UtilityClass
public class MessageUtils {

    public boolean messageHasText(Update update) {
        return !update.hasCallbackQuery() && update.hasMessage() && update.getMessage().hasText();
    }

    public String extractCommand(String text) {
        String[] words = text.split(" ");
        List<String> commands = Arrays.stream(ApiCommands.class.getDeclaredFields()).map(f -> {
            try {
                return (String) f.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).toList();
        return commands.stream().filter(c -> c.equals(words[0])).findFirst().orElse(NO_COMMAND);
    }

    public String extractCallbackData(String text) {
        List<String> datas = Arrays.stream(CallbackData.class.getDeclaredFields()).map(f -> {
            try {
                return (String) f.get(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return "";
            }
        }).toList();
        return datas.stream().filter(text::startsWith).findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Callback command contains unsupported data."));
    }
}
