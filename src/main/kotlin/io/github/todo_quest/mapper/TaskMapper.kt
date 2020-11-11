package io.github.todo_quest.mapper

import io.github.todo_quest.domain.Task
import io.github.todo_quest.domain.TaskStatus.IN_PROGRESS
import io.github.todo_quest.dto.request.TaskAddRequest
import java.time.LocalDateTime
import java.util.*

class TaskMapper : Mapper<Task, TaskAddRequest> {

    override fun from(source: TaskAddRequest): Task =
        Task(
            UUID.randomUUID(),
            source.title ?: "",
            IN_PROGRESS,
            LocalDateTime.now())
}