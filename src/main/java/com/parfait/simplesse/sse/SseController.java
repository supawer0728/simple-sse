package com.parfait.simplesse.sse;

import com.parfait.simplesse.sse.model.MemoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@RestController
public class SseController {

    private final Map<SseEmitter, AtomicInteger> emitterCount = new ConcurrentHashMap<>();

    @CrossOrigin("*")
    @GetMapping("/memory")
    public SseEmitter memory() {

        SseEmitter emitter = new SseEmitter();
        this.emitterCount.put(emitter, new AtomicInteger(0));

        emitter.onCompletion(() -> this.emitterCount.remove(emitter));
        emitter.onTimeout(() -> this.emitterCount.remove(emitter));
        log.info("timeout : {}", emitter.getTimeout());

        return emitter;
    }

    @Scheduled(fixedRate = 3000L)
    public void onMemoryInfo() {

        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitterCount.forEach((emitter, count) -> {

            int increasedCount = count.incrementAndGet();
            try {
                emitter.send(MemoryInfo.create());
                // spring.mvc.async.request-timeout 시간 이내에 emitter.complete()를 호출해야 Exception이 발생하지 않음
                // 시간이 지나서 발생하는 Exception은 Dispatcher Servlet이 처리하며 여기서 딱히 잡지는 않음
                if (increasedCount > 2) {
                    emitter.complete();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                deadEmitters.add(emitter);
            }

        });

        deadEmitters.forEach(emitterCount::remove);
    }
}
