package io.github.todo_quest.controller

import io.github.todo_quest.dto.request.TaskAddRequest
import io.github.todo_quest.dto.request.TaskStatusChangeRequest
import io.github.todo_quest.dto.request.TaskStatusFilter
import io.github.todo_quest.service.TaskService
import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.patch
import io.ktor.routing.post
import io.ktor.routing.route
import io.ktor.util.pipeline.PipelineContext
import java.util.*

fun Route.taskController(taskService: TaskService) {

    route("/api") {

        get("/tasks") {
            val filter = getFilterValue()
            call.respond(HttpStatusCode.OK, taskService.getAllTasks(filter))
        }

        post("/tasks") {
            val taskName = call.receive<TaskAddRequest>()
            call.respond(HttpStatusCode.Created, taskService.addTask(taskName))
        }

        get("/task/{id}") {
            val taskId = UUID.fromString(call.parameters["id"] ?: throw IllegalStateException("Must provide id"))
            val response = taskService.getTask(taskId)

            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }

        patch("/task/{id}") {
            val taskId = UUID.fromString(call.parameters["id"] ?: throw IllegalStateException("Must provide id"))
            val taskStatus = call.receive<TaskStatusChangeRequest>()
            val response = taskService.changeStatus(taskId, taskStatus)

            if (response != null) {
                call.respond(HttpStatusCode.OK, response)
            } else {
                call.respond(HttpStatusCode.NotFound)
            }
        }
    }
}

private fun PipelineContext<Unit, ApplicationCall>.getFilterValue() =
    try {
        TaskStatusFilter.valueOf((call.request.queryParameters["filter"] ?: "ALL").toUpperCase())
    } catch (ex: Exception) {
        TaskStatusFilter.ALL
    }