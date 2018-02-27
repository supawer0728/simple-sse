package com.parfait.simplesse.sse;

import com.parfait.simplesse.sse.model.MemoryInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Slf4j
@RestController
public class SseController {

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @CrossOrigin("*")
    @GetMapping("/memory")
    public SseEmitter memory() {

        SseEmitter emitter = new SseEmitter();
        this.emitters.add(emitter);

        emitter.onCompletion(() -> this.emitters.remove(emitter));
        emitter.onTimeout(() -> this.emitters.remove(emitter));
        log.info("timeout : {}", emitter.getTimeout());

        return emitter;
    }

    @EventListener
    public void onMemoryInfo(MemoryInfo memoryInfo) {

        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(memoryInfo);
                emitter.complete();
            } catch (IOException e) {
                deadEmitters.add(emitter);
            }

        });

        this.emitters.removeAll(deadEmitters);
    }
}
