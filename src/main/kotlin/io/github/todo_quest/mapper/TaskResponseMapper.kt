package io.github.todo_quest.mapper

import io.github.todo_quest.domain.Task
import io.github.todo_quest.dto.response.TaskResponse

class TaskResponseMapper : Mapper<TaskResponse, Task> {

    override fun from(source: Task): TaskResponse =
            TaskResponse(
                source.uuid!!,
                source.title,
                source.status.isComplete(),
                source.createdAt
            )
}