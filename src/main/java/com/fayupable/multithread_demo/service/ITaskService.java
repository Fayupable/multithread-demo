package com.fayupable.multithread_demo.service;

import com.fayupable.multithread_demo.dto.TaskDto;
import com.fayupable.multithread_demo.entity.Task;
import com.fayupable.multithread_demo.request.AddTaskRequest;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ITaskService {
    CompletableFuture<Task> addTask(AddTaskRequest task);

    CompletableFuture<Task> getTask(Long id);

    CompletableFuture<List<Task>> getAllTasks();

    CompletableFuture<Task> removeTask(Long id);

    TaskDto convertToDto(Task task);

    List<TaskDto> convertToDto(List<Task> tasks);

}
