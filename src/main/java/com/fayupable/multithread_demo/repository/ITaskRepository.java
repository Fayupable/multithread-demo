package com.fayupable.multithread_demo.repository;

import com.fayupable.multithread_demo.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ITaskRepository extends JpaRepository<Task, Long> {
}
