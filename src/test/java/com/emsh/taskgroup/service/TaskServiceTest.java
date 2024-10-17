package com.emsh.taskgroup.service;

import com.emsh.taskgroup.dto.request.CreateTaskRequest;
import com.emsh.taskgroup.event.events.TaskCompletedEvent;
import com.emsh.taskgroup.event.events.TaskCreatedEvent;
import com.emsh.taskgroup.exception.CustomApiException;
import com.emsh.taskgroup.model.Group;
import com.emsh.taskgroup.model.Task;
import com.emsh.taskgroup.model.TaskPriority;
import com.emsh.taskgroup.model.User;
import com.emsh.taskgroup.repository.TaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService underTest;

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private GroupService groupService;

    @Mock
    private ApplicationEventPublisher applicationEventPublisher;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void findTaskById() throws CustomApiException {
        // given
        User userCreator = Mockito.mock(User.class);
        Group group = Mockito.mock(Group.class);
        var task = Task.builder()
                .creator(userCreator)
                .group(group)
                .description("description")
                .creationDate(LocalDate.now())
                .priority(TaskPriority.NORMAL)
                .subElements(new ArrayList<>())
                .build();

        // when
        when(taskRepository.findById(Mockito.anyLong())).thenReturn(Optional.of(task));
        Task result = underTest.findTaskById(Mockito.anyLong());

        // then
        assertThat(result).isNotNull();
    }

    @Test
    void findTaskByIdThrowsExceptionWhenTaskNotFound() throws CustomApiException {
        when(taskRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        CustomApiException thrownException = assertThrows(CustomApiException.class, () -> underTest.findTaskById(Mockito.anyLong()));
        assertThat(thrownException.getMessage()).isEqualTo("La tarea no existe.");
    }

    @Test
    void createTask() throws CustomApiException {
        // given
        User mockUser = Mockito.mock(User.class);
        Group mockGroup = Mockito.mock(Group.class);
        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
                .taskPriority("NORMAL")
                .creatorUserId(mockUser.getId())
                .description("Una tarea de prueba")
                .groupId(mockGroup.getId())
                .build();
        when(userService.findUserById(Mockito.anyLong())).thenReturn(mockUser);
        when(groupService.findGroupById(Mockito.anyLong())).thenReturn(mockGroup);

        // when
        Task result = underTest.createTask(createTaskRequest);

        // then
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();
        assertThat(capturedTask.getDescription()).isEqualTo(createTaskRequest.getDescription());

        assertThat(result).isNotNull();
        assertThat(result.getPriority()).isEqualTo(TaskPriority.NORMAL);

        verify(applicationEventPublisher).publishEvent(any(TaskCreatedEvent.class));
    }

    @Test
    void createTaskThrowsExceptionWhenUserNotFound() throws CustomApiException {
        // given
        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
                .taskPriority("NORMAL")
                .creatorUserId(1L)
                .description("Una tarea de prueba")
                .groupId(2L)
                .build();

        // when
        when(userService.findUserById(Mockito.anyLong())).thenThrow(CustomApiException.class);

        // then
        assertThrows(CustomApiException.class, () -> underTest.createTask(createTaskRequest));
        verify(applicationEventPublisher, never()).publishEvent(any(TaskCreatedEvent.class));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void createTaskThrowsExceptionWhenGroupNotFound() throws CustomApiException {
        // given
        CreateTaskRequest createTaskRequest = CreateTaskRequest.builder()
                .taskPriority("NORMAL")
                .creatorUserId(1L)
                .description("Una tarea de prueba")
                .groupId(2L)
                .build();

        // when
        when(groupService.findGroupById(Mockito.anyLong())).thenThrow(CustomApiException.class);
        // then
        assertThrows(CustomApiException.class, () -> underTest.createTask(createTaskRequest));
        verify(applicationEventPublisher, never()).publishEvent(any(TaskCreatedEvent.class));
        verify(taskRepository, never()).save(any());
    }

    @Test
    void deleteTaskShouldCallRepositoryDelete() throws CustomApiException {
        // given
        Long taskId = 1L;
        Task task = Mockito.mock(Task.class);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(task));

        // when
        underTest.deleteTask(taskId);

        // then
        verify(taskRepository).delete(task);
    }

    @Test
    void markTaskAsCompleted() throws CustomApiException {
        // given
        Long taskId = 1L;
        Long finisherUserId = 2L;
        Task task = Task.builder().build();
        Task spyTask = Mockito.spy(task);
        User mockUser = Mockito.mock(User.class);

        // when
        when(userService.findUserById(finisherUserId)).thenReturn(mockUser);
        when(taskRepository.findById(taskId)).thenReturn(Optional.of(spyTask));
        underTest.markTaskAsCompleted(taskId, finisherUserId);

        // then
        ArgumentCaptor<Task> taskArgumentCaptor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(taskArgumentCaptor.capture());
        Task capturedTask = taskArgumentCaptor.getValue();
        assertThat(capturedTask.isCompleted()).isTrue();
        assertThat(capturedTask.getFinisher()).isEqualTo(mockUser);
        verify(applicationEventPublisher).publishEvent(any(TaskCompletedEvent.class));
    }

    @Test
    void validateTaskPriorityReturnsValidTaskPriority() throws CustomApiException {
        String taskPriorityString = "NORMAL";
        TaskPriority result = underTest.validateTaskPriority(taskPriorityString);
        assertThat(result).isEqualTo(TaskPriority.NORMAL);
    }

    @Test
    void validateTaskPriorityThrowsExceptionWhenTaskPriorityInvalid() throws CustomApiException {
        String taskPriorityInvalidString = "INVALID";
        var ex1 = assertThrowsExactly(CustomApiException.class, () -> underTest.validateTaskPriority(taskPriorityInvalidString));
        assertThat(ex1.getMessage()).isEqualTo("La prioridad seleccionada no es válida.");

        String nullTaskPriorityStr = null;
        var ex2 = assertThrowsExactly(CustomApiException.class, () -> underTest.validateTaskPriority(nullTaskPriorityStr));
        assertThat(ex2.getMessage()).isEqualTo("La prioridad de la tarea no puede ser nula.");

    }

}