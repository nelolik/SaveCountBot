package com.nelolik.savecountbot.handler.context;

public interface ContextHandler {

    boolean hasContext(Long userId);

    ContextPhase getContext(Long userId);

    ContextPhase saveContext(Long userId, ContextPhase phase);

    String getRecordName(Long userId);

    String saveRecordName(Long userId, String recordName);

    ContextPhase deleteContext(Long userId);
}
