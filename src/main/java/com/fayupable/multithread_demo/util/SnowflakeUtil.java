package com.fayupable.multithread_demo.util;

import com.fayupable.multithread_demo.config.SnowflakeGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import jakarta.persistence.PrePersist;

import java.lang.reflect.Field;

@Component
public class SnowflakeUtil {

    private static SnowflakeGenerator generator;

    public SnowflakeUtil(@Value("${snowflake.datacenter-id}") long datacenterId,
                         @Value("${snowflake.worker-id}") long workerId) {
        generator = SnowflakeGenerator.getInstance(datacenterId, workerId);
    }

    /**
     * Generic ID assignment method.
     *
     * @param entity Any entity with an ID field
     * @param <T>    Entity type
     */
    public static <T> void assignId(T entity) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);

            if (idField.get(entity) == null) {
                idField.set(entity, generator.nextId());
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error assigning ID: " + e.getMessage(), e);
        }
    }

    /**
     * PrePersist method to assign ID.
     *
     * @param entity Entity that needs an automatic ID assignment
     * @param <T>    Entity type
     */
    @PrePersist
    public static <T> void prePersist(T entity) {
        assignId(entity);
    }
}