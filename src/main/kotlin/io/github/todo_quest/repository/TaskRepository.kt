package io.github.todo_quest.repository

import io.github.todo_quest.domain.Task
import io.github.todo_quest.domain.TaskStatus.DONE
import io.github.todo_quest.domain.TaskStatus.IN_PROGRESS
import java.time.LocalDateTime
import java.util.*


class TaskRepository {

    private val tasks = mutableListOf(
        Task(UUID.randomUUID(), "new Task 1", IN_PROGRESS, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 2", DONE, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 3", IN_PROGRESS, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 4", DONE, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 5", IN_PROGRESS, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 6", DONE, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 7", IN_PROGRESS, LocalDateTime.now()),
        Task(UUID.randomUUID(), "new Task 8", DONE, LocalDateTime.now()),
    )

    fun findAllTasks(): List<Task> =
        tasks

    fun save(newTask: Task) {
        if (tasks.filter { it.uuid!! == newTask.uuid }.any()) {
            tasks.filter { it.uuid!! == newTask.uuid }.forEach {
                it.title = newTask.title
                it.status = newTask.status
            }
        } else {
            tasks.add(newTask)
        }
    }

    fun getOne(taskId: UUID) =
        tasks.firstOrNull {
            it.uuid?.equals(taskId) ?: false
        }
}