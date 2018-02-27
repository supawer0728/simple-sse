package com.parfait.simplesse.sse.service;

import com.parfait.simplesse.sse.model.MemoryInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class SseService {

    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public SseService(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(fixedRate = 3000L)
    public void doSomething() {

        this.eventPublisher.publishEvent(MemoryInfo.create());
    }
}
