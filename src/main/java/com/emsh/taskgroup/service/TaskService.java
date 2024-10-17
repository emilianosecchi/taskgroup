package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateTaskRequest;
import com.emsh.taskgroup.event.events.TaskCompletedEvent;
import com.emsh.taskgroup.event.events.TaskCreatedEvent;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.TaskPriority;
import com.emsh.taskgroup.model.Task;
import com.emsh.taskgroup.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.util.EnumUtils;

import java.time.LocalDate;

@Service
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final GroupService groupService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService, GroupService groupService, ApplicationEventPublisher applicationEventPublisher) {
        this.taskRepository = taskRepository;
        this.userService = userService;
        this.groupService = groupService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public Task findTaskById(Long taskId) throws CustomApiException {
        var task = taskRepository.findById(taskId);
        return task.orElseThrow(
                () -> new CustomApiException("La tarea no existe.", HttpStatus.BAD_REQUEST)
        );
    }

    public Task createTask(CreateTaskRequest request) throws CustomApiException {
        var creatorUser = userService.findUserById(request.getCreatorUserId());
        var group = groupService.findGroupById(request.getGroupId());
        var task = Task.builder()
                .creator(creatorUser)
                .creationDate(LocalDate.now())
                .description(request.getDescription())
                .priority(this.validateTaskPriority(request.getTaskPriority()))
                .group(group)
                .build();
        taskRepository.save(task);
        applicationEventPublisher.publishEvent(new TaskCreatedEvent(this, task));
        return task;
    }

    public void deleteTask(Long taskId) throws CustomApiException {
        var task = this.findTaskById(taskId);
        taskRepository.delete(task);
    }

    public void markTaskAsCompleted(Long taskId, Long finisherUserId) throws CustomApiException {
        var finisherUser = userService.findUserById(finisherUserId);
        var task = this.findTaskById(taskId);
        task.setFinisher(finisherUser);
        task.setEndDate(LocalDate.now());
        taskRepository.save(task);
        this.applicationEventPublisher.publishEvent(new TaskCompletedEvent(this, task));
    }

    public TaskPriority validateTaskPriority(String taskPriorityStr) throws CustomApiException {
        try {
            return EnumUtils.findEnumInsensitiveCase(TaskPriority.class, taskPriorityStr);
        } catch (IllegalArgumentException e) {
            throw new CustomApiException("La prioridad seleccionada no es válida.", HttpStatus.BAD_REQUEST);
        } catch (NullPointerException e) {
            throw new CustomApiException("La prioridad de la tarea no puede ser nula.", HttpStatus.BAD_REQUEST);
        }
    }

}