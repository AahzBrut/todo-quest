package io.github.todo_quest.dto.response

import com.fasterxml.jackson.annotation.JsonFormat
import java.time.LocalDateTime
import java.util.*

data class TaskResponse(
    val uuid: UUID,
    val title: String,
    val status: Boolean,

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    val createdAt: LocalDateTime
)