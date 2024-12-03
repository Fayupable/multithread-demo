package com.fayupable.multithread_demo.config;


import org.springframework.beans.factory.annotation.Value;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SnowflakeGenerator {
    private static final int TIMESTAMP_BITS = 41;
    private static final int DATACENTER_BITS = 5;
    private static final int WORKER_BITS = 5;
    private static final int SEQUENCE_BITS = 12;

    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_BITS);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BITS);

    private static final long TIMESTAMP_SHIFT = DATACENTER_BITS + WORKER_BITS + SEQUENCE_BITS;
    private static final long DATACENTER_SHIFT = WORKER_BITS + SEQUENCE_BITS;
    private static final long WORKER_SHIFT = SEQUENCE_BITS;

    // 2024-01-01
    private static final long EPOCH = 1704067200000L;

    @Value("${snowflake.datacenter-id}")
    private long datacenterId;

    @Value("${snowflake.worker-id}")
    private long workerId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;
    private final Random random;

    private static volatile SnowflakeGenerator instance;
    private static final Object lock = new Object();

    private SnowflakeGenerator(long datacenterId, long workerId) {
        if (datacenterId > MAX_DATACENTER_ID || datacenterId < 0) {
            throw new IllegalArgumentException(
                    String.format("Datacenter ID must be between %d and %d", 0, MAX_DATACENTER_ID));
        }
        if (workerId > MAX_WORKER_ID || workerId < 0) {
            throw new IllegalArgumentException(
                    String.format("Worker ID must be between %d and %d", 0, MAX_WORKER_ID));
        }

        this.datacenterId = datacenterId;
        this.workerId = workerId;
        this.random = new SecureRandom();
    }

    public static SnowflakeGenerator getInstance(long datacenterId, long workerId) {
        if (instance == null) {
            synchronized (lock) {
                if (instance == null) {
                    instance = new SnowflakeGenerator(datacenterId, workerId);
                }
            }
        }
        return instance;
    }

    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException(
                    String.format("System clock is behind by %d milliseconds! ID generation rejected.",
                            lastTimestamp - currentTimestamp));
        }

        if (currentTimestamp == lastTimestamp) {
            sequence = (sequence + random.nextInt(10) + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                currentTimestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = random.nextInt(100);
        }

        lastTimestamp = currentTimestamp;

        return ((currentTimestamp - EPOCH) << TIMESTAMP_SHIFT) |
                (datacenterId << DATACENTER_SHIFT) |
                (workerId << WORKER_SHIFT) |
                sequence;
    }

    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    public static Map<String, Long> parseId(long id) {
        Map<String, Long> components = new HashMap<>();

        long timestamp = (id >> TIMESTAMP_SHIFT) + EPOCH;
        long datacenterId = (id >> DATACENTER_SHIFT) & MAX_DATACENTER_ID;
        long workerId = (id >> WORKER_SHIFT) & MAX_WORKER_ID;
        long sequence = id & MAX_SEQUENCE;

        components.put("timestamp", timestamp);
        components.put("datacenterId", datacenterId);
        components.put("workerId", workerId);
        components.put("sequence", sequence);

        return components;
    }
}