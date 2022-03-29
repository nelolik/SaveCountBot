package com.nelolik.savecountbot.handler;


import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static com.nelolik.savecountbot.handler.CallbackMessageHandler.ADD_COUNT_BTN_DATA;
import static com.nelolik.savecountbot.handler.CallbackMessageHandlerImpl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CallbackMessageHandlerTest {

    private final Long USER_ID = 1234567L;
    private final String RECORD_NAME = "Record name";
    private final String TEST_DATA = ADD_COUNT_BTN_DATA + RECORD_NAME;

    @MockBean
    private ContextHandler contextHandler;

    @Autowired
    private CallbackMessageHandler handler;

    @Test
    void handleCreateRecordCallbackTest() {
        SendMessage sendMessage = handler.handleCreateRecordCallback(USER_ID);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(USER_ID.toString(), TEXT_ENTER_NEW_RECORD_NAME));
    }

    @Test
    void handleSaveCountCallbackTest() {
        when(contextHandler.hasContext(USER_ID)).thenReturn(true);
        SendMessage sendMessage = handler.handleSaveCountCallback(TEST_DATA, USER_ID);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(USER_ID.toString(), TEXT_ENTER_COUNT));
        verify(contextHandler, Mockito.atLeastOnce())
                .saveContext(USER_ID, ContextHandler.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        verify(contextHandler, Mockito.atLeastOnce()).saveRecordName(USER_ID, RECORD_NAME);
    }

    @Test
    void handleSaveCountCallbackWithNoContextTest() {
        when(contextHandler.hasContext(USER_ID)).thenReturn(false);
        SendMessage sendMessage = handler.handleSaveCountCallback(TEST_DATA, USER_ID);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(USER_ID.toString(), TEXT_ERROR_COMMAND_PROCESSING));
    }

    @Test
    void handleSaveCountCallbackWithoutRecordNameTest() {
        when(contextHandler.hasContext(USER_ID)).thenReturn(true);
        SendMessage sendMessage = handler.handleSaveCountCallback(ADD_COUNT_BTN_DATA, USER_ID);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(USER_ID.toString(), TEXT_ERROR_COMMAND_PROCESSING));
    }
}
