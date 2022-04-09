package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.Bot;
import com.nelolik.savecountbot.handler.context.ContextHandler;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.nelolik.savecountbot.handler.callback.CallbackData.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.callback.CallbackData.CREATE_RECORD_BTN_DATA;
import static com.nelolik.savecountbot.handler.callback.CallbackStringConstants.TEXT_ENTER_COUNT;
import static com.nelolik.savecountbot.handler.TestConstants.*;
import static com.nelolik.savecountbot.handler.context.ContextPhase.*;
import static com.nelolik.savecountbot.handler.message.ApiCommands.*;
import static com.nelolik.savecountbot.handler.message.MessageStringConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class MessageHandlerTest {

    private final Chat CHAT = new Chat(CHAT_ID, "private");
    private final InlineKeyboardMarkup MARKUP = new InlineKeyboardMarkup();
    private final InlineKeyboardButton BTN_HELLO = InlineKeyboardButton.builder()
            .text(CREATE_BTN_TEXT)
            .callbackData(CREATE_RECORD_BTN_DATA)
            .build();
    private static final String LIST_OF_RECORDS_TEXT = """
            Record: prostrations, count: 400
            Record: dordje sempa, count: 400""";
    private final static String RECORD1 = "prostrations";
    private final static String RECORD2 = "dordje sempa";
    private static final InlineKeyboardButton BTN_RECORD1 = InlineKeyboardButton.builder()
            .text(RECORD1)
            .callbackData(ADD_COUNT_BTN_DATA + RECORD1)
            .build();
    private static final InlineKeyboardButton BTN_RECORD2 = InlineKeyboardButton.builder()
            .text(RECORD2)
            .callbackData(ADD_COUNT_BTN_DATA + RECORD2)
            .build();

    @Autowired
    private MessageHandler messageHandler;

    @Autowired
    private RecordsRepository recordsRepository;

    @MockBean
    private ContextHandler contextHandler;

    //Added to prevent asking bot`s name and token and connecting to server in test
    @MockBean
    private TelegramBotsApi telegramBotsApi;

    @Test
    void helloCommandTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_HELLO);
        MARKUP.setKeyboard(List.of(List.of(BTN_HELLO)));
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText(), m.getReplyMarkup()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), HELLO_MESSAGE, MARKUP));
    }

    @Test
    void handleNewRecordCommandWithoutArgsTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_NEW_RECORD);
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_NEW_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, NEW_RECORD_REQUESTED);
    }

    @Test
    void handleNewRecordCommandWithArg() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(String.format("%s %s", COMMAND_NEW_RECORD,RECORD_NAME));
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), String.format(FORMAT_NEW_RECORD_CREATED, RECORD_NAME)));
        verify(contextHandler, Mockito.only()).deleteContext(CHAT_ID);
    }

    @Test
    @Sql(scripts = "classpath:/clearTables.sql")
    void handleEmptyLisOfRecordsTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_LIST_OF_RECORDS);
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_NO_RECORD));
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void handleNotEmptyListOfRecords() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_LIST_OF_RECORDS);
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText).isEqualTo(LIST_OF_RECORDS_TEXT);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void newCountCommandTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_NEW_COUNT);
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        MARKUP.setKeyboard(List.of(List.of(BTN_RECORD1), List.of(BTN_RECORD2)));
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getReplyMarkup).isEqualTo(MARKUP);
        verify(contextHandler, Mockito.atLeastOnce()).deleteContext(CHAT_ID);
        verify(contextHandler, Mockito.atLeastOnce())
                .saveContext(CHAT_ID, SAVE_COUNT_REQUESTED);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void commandDeleteRecordWithoutArgsTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(COMMAND_DELETE_RECORD);
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_DELETE_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, DELETE_RECORD_REQUESTED);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    @Transactional
    void commandDeleteRecordWithArgTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(String.format("%s %s", COMMAND_DELETE_RECORD, RECORD1));
        Update update = new Update();
        update.setMessage(message);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_DELETE_RECORD_SUCCEED, RECORD1));
        assertThat(recordsRepository.findByUserid(CHAT_ID)).isNotNull().map(Records::getRecordName)
                .doesNotContain(RECORD1)
                .contains(RECORD2);
    }

    @Test
    void handleTextMessageWithoutContext() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(WRONG_COMMAND);
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(Mockito.any())).thenReturn(false);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNull();
    }

    @Test
    void handleTextMessageWithNewRecordContextTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(RECORD_NAME);
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(NEW_RECORD_REQUESTED);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), String.format(FORMAT_NEW_RECORD_CREATED, RECORD_NAME)));
        assertThat(recordsRepository.findByUserid(CHAT_ID)).isNotNull().map(Records::getRecordName)
                .contains(RECORD_NAME);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void handleTextMessageWithNewRecordContextAndExistingRecordNameTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(RECORD1);
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(NEW_RECORD_REQUESTED);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(TEXT_NAME_IS_NOT_UNIQ);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    @Transactional
    void handleTextMessageDeleteRecordContextTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(RECORD1);
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(DELETE_RECORD_REQUESTED);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_DELETE_RECORD_SUCCEED, RECORD1));
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void handleTextMessageDeleteRecordContextWithNotExistingNameTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(RECORD_NAME);
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(DELETE_RECORD_REQUESTED);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_RECORD_NOT_FOUND, RECORD_NAME));
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void handleTextMessageWithSaveCountContextTest() {
        //Given:
        long count = 100L;
        Message message = new Message();
        message.setChat(CHAT);
        message.setText(Long.toString(count));
        Update update = new Update();
        update.setMessage(message);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        when(contextHandler.getRecordName(CHAT_ID)).thenReturn(RECORD1);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_COUNT_SAVED, RECORD1, 500));
    }

    @Test
    void handleCallbackWithCreateRecordDataTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText("");
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(CREATE_RECORD_BTN_DATA);
        callbackQuery.setMessage(message);
        Update update = new Update();
        update.setCallbackQuery(callbackQuery);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_NEW_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, NEW_RECORD_REQUESTED);
    }

    @Test
    @Sql(scripts = "classpath:/fillRecordsTable.sql")
    @Sql(scripts = "classpath:/fillCountsTable.sql")
    void handleCallbackWithAddCountDataTest() {
        //Given:
        Message message = new Message();
        message.setChat(CHAT);
        message.setText("");
        CallbackQuery callbackQuery = new CallbackQuery();
        callbackQuery.setData(ADD_COUNT_BTN_DATA + RECORD1);
        callbackQuery.setMessage(message);
        Update update = new Update();
        update.setCallbackQuery(callbackQuery);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        //When:
        SendMessage sendMessage = messageHandler.handle(update);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_COUNT));
        verify(contextHandler, Mockito.atLeastOnce()).saveContext(CHAT_ID, RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        verify(contextHandler, Mockito.atLeastOnce()).saveRecordName(CHAT_ID, RECORD1);
    }
}
