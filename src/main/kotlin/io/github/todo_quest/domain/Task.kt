package io.github.todo_quest.domain

import java.time.LocalDateTime
import java.util.*

data class Task(
    var uuid: UUID?,
    var title: String,
    var status: TaskStatus,
    var createdAt: LocalDateTime
)