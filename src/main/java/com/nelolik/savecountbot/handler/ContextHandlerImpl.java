package com.nelolik.savecountbot.handler;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class ContextHandlerImpl implements ContextHandler {

    Map<Long, ContextPhase> phaseRepository;

    Map<Long, String> nameRepository;

    public ContextHandlerImpl() {
        phaseRepository = new HashMap<>();
        nameRepository = new HashMap<>();
    }

    @Override
    public boolean hasContext(Long userId) {
        return phaseRepository.containsKey(userId);
    }

    @Override
    public ContextPhase getContext(Long userId) {
        return phaseRepository.get(userId);
    }

    @Override
    public ContextPhase saveContext(Long userId, ContextPhase phase) {
        return phaseRepository.put(userId, phase);
    }

    @Override
    public String getRecordName(Long userId) {
        return nameRepository.get(userId);
    }

    @Override
    public String saveRecordName(Long userId, String recordName) {
        return nameRepository.put(userId, recordName);
    }

    @Override
    public ContextPhase deleteContext(Long userId) {
        return phaseRepository.remove(userId);
    }
}
