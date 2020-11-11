package io.github.todo_quest.domain

enum class TaskStatus {
    DONE,
    IN_PROGRESS;

    fun isComplete(): Boolean = this == DONE
}