package io.github.todo_quest.dto.response

import java.util.*

data class TaskResponse(
    val uuid: UUID,
    val title: String,
    val status: Boolean,
    val createdAt: String
)