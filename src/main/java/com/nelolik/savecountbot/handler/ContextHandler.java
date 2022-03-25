package com.nelolik.savecountbot.handler;

public interface ContextHandler {
    enum ContextPhase {
        SAVE_RECORD_REQUESTED,
        NEW_RECORD_REQUESTED,
        RECORD_NAME_ENTERED
    }

    boolean hasContext(Long userId);

    ContextPhase getContext(Long userId);

    ContextPhase saveContext(Long userId, ContextPhase phase);

    String getRecordName(Long userId);

    String saveRecordName(Long userId, String recordName);

    ContextPhase deleteContext(Long userId);
}
