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
class TextMessageHandlerTest {

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

    private static final Long CHAT_ID = 1234567L;
    private static final List<Records> RECORDS = new ArrayList<>();
    private static final List<List<Counts>> COUNTS_LIST = new ArrayList<>();
    private static final String LIST_OF_RECORDS_TEXT = """
            Record: Name1, count: 600
            Record: Name2, count: 600
            Record: Name3, count: 600""";
    private static final String WRONG_COMMAND = "/wrong_command";
    private static final String RECORD_NAME = "Record Name";
    private static final String COMMAND_NEW_RECORD_WITH_ARG = COMMAND_NEW_RECORD + " " + RECORD_NAME;

    private static final String COMMAND_DELETE_RECORD_WITH_ARG = COMMAND_DELETE_RECORD + " " + RECORD_NAME;

    @BeforeAll
    static void initTest() {
        long countId = 1L;
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
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText).isEqualTo(TEXT_NO_RECORD);
    }

    @Test
    void handleListOfRecordsTest() {
        //Given:
        when(recordsRepository.findByUserid(CHAT_ID)).thenReturn(RECORDS);
        for (Records r :
                RECORDS) {
            when(countsRepository.findByRecordid(r.getId()))
                    .thenReturn(COUNTS_LIST.get(r.getId().intValue() - 1));
        }
        //When:
        SendMessage sendMessage = handler.handleLisOfRecordsCommand(message);
        //Then
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText).isEqualTo(LIST_OF_RECORDS_TEXT);
    }

    @Test
    void handleNewRecordCommandWithoutArgs() {
        //Given:
        when(message.getText()).thenReturn(COMMAND_NEW_RECORD);
        //When:
        SendMessage sendMessage = handler.handleNewRecordCommand(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_NEW_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, ContextHandler.ContextPhase.NEW_RECORD_REQUESTED);
    }

    @Test
    void handleNewRecordCommandWithWrongCommand() {
        //Given:
        when(message.getText()).thenReturn(WRONG_COMMAND);
        //When:
        SendMessage sendMessage = handler.handleNewRecordCommand(message);
        //Then:
        assertThat(sendMessage).isNull();
    }

    @Test
    void handleNewRecordCommandWithNameArg() {
        //Given:
        when(message.getText()). thenReturn(COMMAND_NEW_RECORD_WITH_ARG);
        //When:
        SendMessage sendMessage = handler.handleNewRecordCommand(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), String.format(FORMAT_NEW_RECORD_CREATED, RECORD_NAME)));
        verify(contextHandler,Mockito.only()).deleteContext(CHAT_ID);
    }

    @Test
    void handleDeleteRecordOnlyCommandTest() {
        //Given:
        when(message.getText()).thenReturn(COMMAND_DELETE_RECORD);
        //When:
        SendMessage sendMessage = handler.handleDeleteRecord(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_ENTER_DELETE_RECORD_NAME));
        verify(contextHandler, Mockito.only()).saveContext(CHAT_ID, ContextHandler.ContextPhase.DELETE_RECORD_REQUESTED);
    }

    @Test
    void handleNewRecordCommand() {
        //Then:
        when(message.getText()).thenReturn(COMMAND_NEW_COUNT);
        //When:
        SendMessage sendMessage = handler.handleNewCountCommand(message);
        //Then:
        assertThat(sendMessage).isNotNull()
                .extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), TEXT_CHOOSE_RECORD));
        verify(contextHandler, Mockito.atLeastOnce()).deleteContext(CHAT_ID);
        verify(contextHandler, Mockito.atLeastOnce()).saveContext(CHAT_ID, ContextHandler.ContextPhase.SAVE_COUNT_REQUESTED);
        verify(recordsRepository, Mockito.only()).findByUserid(CHAT_ID);
    }

    @Test
    void handleDeleteRecordCommandWithNameNotFoundTest() {
        //Given:
        when(message.getText()).thenReturn(COMMAND_DELETE_RECORD_WITH_ARG);
        //When:
        SendMessage sendMessage = handler.handleDeleteRecord(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), String.format(FORMAT_RECORD_NOT_FOUND, RECORD_NAME)));
        verify(contextHandler, Mockito.only()).deleteContext(CHAT_ID);
    }

    @Test
    void handleDeleteRecordCommandWithRealNameTest() {
        //Given:
        List<Records> sublistByName = RECORDS.subList(0, 1);
        Long recordId = sublistByName.get(0).getId();
        Long userId = sublistByName.get(0).getUserid();
        String recordName = sublistByName.get(0).getRecordName();
        when(message.getText()).thenReturn(COMMAND_DELETE_RECORD + " " + recordName);
        when(recordsRepository.findByRecordNameAndUserid(recordName, userId)).thenReturn(sublistByName);
        //When:
        SendMessage sendMessage = handler.handleDeleteRecord(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(m -> Tuple.tuple(m.getChatId(), m.getText()))
                .isEqualTo(Tuple.tuple(CHAT_ID.toString(), String.format(FORMAT_DELETE_RECORD_SUCCEED, recordName)));
        verify(recordsRepository, Mockito.atLeastOnce()).findByRecordNameAndUserid(recordName, userId);
        verify(recordsRepository, Mockito.atLeastOnce()).deleteById(recordId);
        verify(countsRepository, Mockito.only()).deleteAllByRecordid(recordId);
        verify(contextHandler, Mockito.only()).deleteContext(CHAT_ID);
    }

    @Test
    void handleTextMessageWithoutContext() {
        //Given:
        when(message.getText()).thenReturn("");
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(false);
        //When:
        SendMessage messageWithoutContext = handler.handleTextMessage(message);
        //Then:
        assertThat(messageWithoutContext).isNull();
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        SendMessage messageWithoutText = handler.handleTextMessage(message);
        assertThat(messageWithoutText).isNull();
    }

    @Test
    void handleTextMessageNewRecordContext() {
        //Given:
        when(message.getText()).thenReturn(RECORD_NAME);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID)).thenReturn(ContextHandler.ContextPhase.NEW_RECORD_REQUESTED);
        //When:
        SendMessage sendMessage = handler.handleTextMessage(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_NEW_RECORD_CREATED, RECORD_NAME));
        verify(recordsRepository, Mockito.atLeastOnce()).save(new Records(0L, CHAT_ID, RECORD_NAME));
        verify(contextHandler, Mockito.atLeastOnce()).deleteContext(CHAT_ID);
    }

    @Test
    void handleTextMessageDeleteRecordContext() {
        //Given:
        Records record = RECORDS.get(0);
        when(message.getText()).thenReturn(record.getRecordName());
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(recordsRepository.findByRecordNameAndUserid(record.getRecordName(), CHAT_ID))
                .thenReturn(RECORDS.subList(0, 1));
        when(contextHandler.getContext(CHAT_ID)).thenReturn(ContextHandler.ContextPhase.DELETE_RECORD_REQUESTED);
        when(recordsRepository.findByRecordNameAndUserid(RECORD_NAME, CHAT_ID)).thenReturn(RECORDS.subList(0, 1));
        //When:
        SendMessage sendMessage = handler.handleTextMessage(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(String.format(FORMAT_DELETE_RECORD_SUCCEED, record.getRecordName()));
        verify(recordsRepository, Mockito.atLeastOnce()).deleteById(record.getId());
        verify(countsRepository, Mockito.only()).deleteAllByRecordid(record.getId());
        verify(contextHandler, Mockito.atLeastOnce()).deleteContext(CHAT_ID);
    }

    @Test
    void handleTextMessageSaveCount() {
        //Given:
        Long value = 200L;
        List<Counts> counts = COUNTS_LIST.get(0);
        long count = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
        Records records = RECORDS.get(0);
        when(message.getText()).thenReturn(value.toString());
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID))
                .thenReturn(ContextHandler.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        when(contextHandler.getRecordName(CHAT_ID)).thenReturn(records.getRecordName());
        when(recordsRepository.findByRecordNameAndUserid(records.getRecordName(), records.getUserid()))
                .thenReturn(RECORDS.subList(0, 1));
        when(countsRepository.findByRecordid(records.getId())).thenReturn(counts);
        //When:
        SendMessage sendMessage = handler.handleTextMessage(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(records.getRecordName() + ": total " + count);
        verify(countsRepository, Mockito.atLeastOnce()).save(new Counts(0L, records.getId(), value, Mockito.any()));
        verify(contextHandler, Mockito.atLeastOnce()).deleteContext(CHAT_ID);
    }

    @Test
    void handleTextMessageSaveCountNotANumber() {
        //Given:
        String value = "Not a number";
        List<Counts> counts = COUNTS_LIST.get(0);
        long count = counts.stream().map(Counts::getCount).reduce(Long::sum).orElse(0L);
        Records records = RECORDS.get(0);
        when(message.getText()).thenReturn(value);
        when(contextHandler.hasContext(CHAT_ID)).thenReturn(true);
        when(contextHandler.getContext(CHAT_ID))
                .thenReturn(ContextHandler.ContextPhase.RECORD_NAME_FOR_SAVE_COUNT_ENTERED);
        //When::
        SendMessage sendMessage = handler.handleTextMessage(message);
        //Then:
        assertThat(sendMessage).isNotNull().extracting(SendMessage::getText)
                .isEqualTo(TEXT_ERROR_PARSE_LONG);
        verify(contextHandler, Mockito.never()).getRecordName(CHAT_ID);
        verify(recordsRepository, Mockito.never()).findByRecordNameAndUserid(Mockito.any(), Mockito.any());
        verify(countsRepository, Mockito.never()).findByRecordid(Mockito.any());
        verify(countsRepository, Mockito.never()).save(Mockito.any());
        verify(contextHandler, Mockito.never()).deleteContext(CHAT_ID);
    }
}
