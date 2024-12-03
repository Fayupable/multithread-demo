package com.fayupable.multithread_demo.controller;

import com.fayupable.multithread_demo.dto.TaskDto;
import com.fayupable.multithread_demo.request.AddTaskRequest;
import com.fayupable.multithread_demo.service.ITaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private final ITaskService taskService;

    public TaskController(ITaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<TaskDto>> addTask(@RequestBody AddTaskRequest addTaskRequest) {
        return taskService.addTask(addTaskRequest)
                .thenApply(task -> ResponseEntity.status(HttpStatus.CREATED).body(taskService.convertToDto(task)));
    }

}