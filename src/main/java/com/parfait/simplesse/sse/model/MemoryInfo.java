package com.parfait.simplesse.sse.model;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemoryInfo {
    private final long heap;
    private final long nonHeap;
    private final long ts;

    private MemoryInfo(long heap, long nonHeap) {
        this.heap = heap;
        this.nonHeap = nonHeap;
        this.ts = System.currentTimeMillis();
    }

    public static MemoryInfo create() {
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();
        MemoryUsage heap = mem.getHeapMemoryUsage();
        MemoryUsage nonHeap = mem.getNonHeapMemoryUsage();

        return new MemoryInfo(heap.getUsed(), nonHeap.getUsed());
    }
}
