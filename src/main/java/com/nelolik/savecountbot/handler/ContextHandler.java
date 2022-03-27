package com.nelolik.savecountbot.handler;

public interface ContextHandler {
    enum ContextPhase {
        SAVE_COUNT_REQUESTED,
        RECORD_NAME_FOR_SAVE_COUNT_ENTERED,
        NEW_RECORD_REQUESTED,
        DELETE_RECORD_REQUESTED
    }

    boolean hasContext(Long userId);

    ContextPhase getContext(Long userId);

    ContextPhase saveContext(Long userId, ContextPhase phase);

    String getRecordName(Long userId);

    String saveRecordName(Long userId, String recordName);

    ContextPhase deleteContext(Long userId);
}
