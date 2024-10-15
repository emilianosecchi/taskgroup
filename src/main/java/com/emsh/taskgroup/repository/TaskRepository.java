package com.emsh.taskgroup.repository;

import com.emsh.taskgroup.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {
}
