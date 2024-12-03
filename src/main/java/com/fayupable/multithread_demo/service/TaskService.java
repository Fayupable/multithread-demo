package com.fayupable.multithread_demo.service;

import com.fayupable.multithread_demo.dto.TaskDto;
import com.fayupable.multithread_demo.entity.Person;
import com.fayupable.multithread_demo.entity.Task;
import com.fayupable.multithread_demo.repository.ITaskRepository;
import com.fayupable.multithread_demo.request.AddTaskRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@Service

public class TaskService implements ITaskService {
    private final ITaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private static final Logger log = Logger.getLogger(TaskService.class.getName());

    public TaskService(ITaskRepository taskRepository, ModelMapper modelMapper) {
        this.taskRepository = taskRepository;
        this.modelMapper = modelMapper;
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Task> addTask(AddTaskRequest taskRequest) {
        log.info("Thread name: " + Thread.currentThread().getName());
        log.info("Thread id: " + Thread.currentThread().getId());
        log.info("Thread priority: " + Thread.currentThread().getPriority());
        log.info("Thread state: " + Thread.currentThread().getState());
        return CompletableFuture.supplyAsync(() -> Optional.of(taskRequest)
                .map(this::createTask)
                .map(taskRepository::save)
                .orElseThrow(() -> new RuntimeException("Task not created")));
    }

    private Task createTask(AddTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus("PENDING");
        task.setStartDate(request.getStartDate());
        task.setEndDate(request.getEndDate());

        Person person = new Person();
        person.setId(request.getPersonId());
        task.setPerson(person);


        return task;
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Task> getTask(Long id) {
        Optional<Task> task = taskRepository.findById(id);
        return CompletableFuture.completedFuture(task.orElseThrow(() -> new RuntimeException("Task not found with id: " + id)));
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<List<Task>> getAllTasks() {
        List<Task> tasks = taskRepository.findAll();
        return CompletableFuture.completedFuture(tasks);
    }

    @Async("taskExecutor")
    @Override
    public CompletableFuture<Task> removeTask(Long id) {
        taskRepository.deleteById(id);
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public TaskDto convertToDto(Task task) {
        return modelMapper.map(task, TaskDto.class);
    }

    @Override
    public List<TaskDto> convertToDto(List<Task> tasks) {
        return tasks.stream().map(this::convertToDto).toList();
    }
}