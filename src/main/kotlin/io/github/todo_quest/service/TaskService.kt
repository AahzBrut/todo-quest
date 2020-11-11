package io.github.todo_quest.service

import io.github.todo_quest.domain.Task
import io.github.todo_quest.domain.TaskStatus
import io.github.todo_quest.dto.request.TaskAddRequest
import io.github.todo_quest.dto.request.TaskStatusChangeRequest
import io.github.todo_quest.dto.request.TaskStatusFilter
import io.github.todo_quest.dto.request.TaskStatusFilter.ALL
import io.github.todo_quest.dto.request.TaskStatusFilter.DONE
import io.github.todo_quest.dto.request.TaskStatusFilter.UNDONE
import io.github.todo_quest.dto.response.TaskResponse
import io.github.todo_quest.mapper.Mapper
import io.github.todo_quest.mapper.TaskMapper
import io.github.todo_quest.mapper.TaskResponseMapper
import io.github.todo_quest.repository.TaskRepository
import java.util.*

class TaskService(
    private val taskRepository: TaskRepository
) {

    private val responseMapper: Mapper<TaskResponse, Task> by lazy { TaskResponseMapper() }
    private val taskMapper: Mapper<Task, TaskAddRequest> by lazy { TaskMapper() }

    fun getAllTasks(filter: TaskStatusFilter): List<TaskResponse> =
        responseMapper.from(
            taskRepository
                .findAllTasks()
                .filter { task ->
                    when (filter) {
                        ALL -> true
                        DONE -> task.status == TaskStatus.DONE
                        UNDONE -> task.status == TaskStatus.IN_PROGRESS
                    }
                }
        )

    fun addTask(taskName: TaskAddRequest): TaskResponse {
        val newTask = taskMapper.from(taskName)
        taskRepository.save(newTask)
        return responseMapper.from(newTask)
    }

    fun getTask(taskId: UUID): TaskResponse? =
        taskRepository.getOne(taskId)?.let { responseMapper.from(it) }

    fun changeStatus(taskId: UUID, taskStatus: TaskStatusChangeRequest): TaskResponse? {
        val task = taskRepository.getOne(taskId)
        return task?.let {
            it.status = if (taskStatus.isDone!!) TaskStatus.DONE else TaskStatus.IN_PROGRESS
            taskRepository.save(it)
            responseMapper.from(it)
        }
    }
}