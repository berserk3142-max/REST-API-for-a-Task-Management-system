package com.taskmanager.service;

import com.taskmanager.dto.task.CreateTaskRequest;
import com.taskmanager.dto.task.TaskResponse;
import com.taskmanager.dto.task.UpdateStatusRequest;
import com.taskmanager.dto.task.UpdateTaskRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.User;
import com.taskmanager.enums.TaskPriority;
import com.taskmanager.enums.TaskStatus;
import com.taskmanager.exception.ResourceNotFoundException;
import com.taskmanager.repository.TaskRepository;
import com.taskmanager.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = new Task();
        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : TaskStatus.TODO);
        task.setPriority(request.getPriority() != null ? request.getPriority() : TaskPriority.MEDIUM);
        task.setDueDate(request.getDueDate());

        if (request.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        }

        Task savedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(savedTask);
    }

    @Transactional(readOnly = true)
    public Page<TaskResponse> getAllTasks(TaskStatus status, TaskPriority priority, Long assignedToId, Pageable pageable) {
        return taskRepository.findByFilters(status, priority, assignedToId, pageable)
                .map(TaskResponse::fromEntity);
    }

    @Transactional(readOnly = true)
    public TaskResponse getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));
        return TaskResponse.fromEntity(task);
    }

    public TaskResponse updateTask(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setStatus(request.getStatus() != null ? request.getStatus() : task.getStatus());
        task.setPriority(request.getPriority() != null ? request.getPriority() : task.getPriority());
        task.setDueDate(request.getDueDate());

        if (request.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(request.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + request.getAssignedToId()));
            task.setAssignedTo(assignedUser);
        } else {
            task.setAssignedTo(null);
        }

        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    public TaskResponse updateTaskStatus(Long id, UpdateStatusRequest request) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + id));

        task.setStatus(request.getStatus());
        Task updatedTask = taskRepository.save(task);
        return TaskResponse.fromEntity(updatedTask);
    }

    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task not found with id: " + id);
        }
        taskRepository.deleteById(id);
    }
}
