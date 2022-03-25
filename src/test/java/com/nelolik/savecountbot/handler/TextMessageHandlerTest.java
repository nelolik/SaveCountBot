package com.nelolik.savecountbot.handler;

import com.nelolik.savecountbot.model.Counts;
import com.nelolik.savecountbot.model.Records;
import com.nelolik.savecountbot.repositroy.CountsRepository;
import com.nelolik.savecountbot.repositroy.RecordsRepository;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;

import static com.nelolik.savecountbot.handler.TextMessageHandler.COMMAND_NEW_RECORD;
import static com.nelolik.savecountbot.handler.TextMessageHandlerImpl.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@SpringBootTest
public class TextMessageHandlerTest {

    @MockBean
    private RecordsRepository recordsRepository;

    @MockBean
    private CountsRepository countsRepository;

    @Mock
    private Message message;

    @MockBean
    private ContextHandler contextHandler;

    @Autowired
    private TextMessageHandler handler;

    private static final Long CHAT_ID = 1234567l;
    private static final List<Records> RECORDS = new ArrayList<>();
    private static final List<List<Counts>> COUNTS_LIST = new ArrayList<>();
    private static final String LIST_OF_RECORDS_TEXT = "Record: Name1, count: 600\n" +
            "Record: Name2, count: 600\n" +
            "Record: Name3, count: 600\n";
    private static final String WRONG_COMMAND = "/wrong_command";

    @BeforeAll
    static void initTest() {
        long countId = 1l;
        for (long recordId = 1; recordId < 4; recordId++) {
            RECORDS.add(new Records(recordId, CHAT_ID, "Name" + recordId));
            List<Counts> recordCounts = new ArrayList<>();
            for (long c = 1; c < 4; c++) {
                recordCounts.add(new Counts(countId++, recordId, c * 100, new Date()));
            }
            COUNTS_LIST.add(recordCounts);
        }
    }

    @BeforeEach
    void initBeforeEach() {
        when(message.getChatId()).thenReturn(CHAT_ID);
    }

    @Test
    void handleHelloCommandTest() {
        SendMessage sendMessage = handler.handleHelloCommand(message);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getText(), m.getChatId()))
                .isEqualTo(Tuple.tuple(HELLO_MESSAGE, CHAT_ID.toString()));
    }

    @Test
    void handleEmptyLisOfRecordsTest() {
        when(recordsRepository.findByUserid(CHAT_ID)).thenReturn(null);
        SendMessage sendMessage = handler.handleLisOfRecordsCommand(message);
        assertThat(sendMessage).isNotNull().extracting(m -> m.getText()).isEqualTo(TEXT_NO_RECORD);
    }

    @Test
    void handleListOfRecordsTest() {
        when(recordsRepository.findByUserid(CHAT_ID)).thenReturn(RECORDS);
        for (Records r :
                RECORDS) {
            when(countsRepository.findByRecordid(r.getId()))
                    .thenReturn(COUNTS_LIST.get(r.getId().intValue() - 1));
        }
        SendMessage sendMessage = handler.handleLisOfRecordsCommand(message);
        assertThat(sendMessage).isNotNull().extracting(m -> m.getText()).isEqualTo(LIST_OF_RECORDS_TEXT);
    }

    @Test
    void handleNewRecordCommandWithoutArgs() {
        when(message.getText()).thenReturn(COMMAND_NEW_RECORD);
        SendMessage sendMessage = handler.handleNewRecordCommand(message);
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, ContextHandler.ContextPhase.NEW_RECORD_REQUESTED);
    }

    @Test
    void handleNewRecordCommandWithWrongCommand() {
        when(message.getText()).thenReturn(WRONG_COMMAND);

        SendMessage sendMessage = handler.handleNewRecordCommand(message);
        assertThat(sendMessage).isNull();
    }

//    @Test
//    void handleNewRecordCommandWithNameArg() {
//        when(message)
//    }
}
