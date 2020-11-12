package io.github.todo_quest

import com.fasterxml.jackson.databind.SerializationFeature
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.todo_quest.controller.taskController
import io.github.todo_quest.feature.Hikari
import io.github.todo_quest.repository.TaskRepository
import io.github.todo_quest.service.TaskService
import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.ContentNegotiation
import io.ktor.features.DefaultHeaders
import io.ktor.jackson.jackson
import io.ktor.response.respondText
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.text.DateFormat

@Suppress("unused")
fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)

    install(ContentNegotiation) {
        jackson {
            configure(SerializationFeature.INDENT_OUTPUT, true)
            findAndRegisterModules()
        }
    }

    var dataSource: HikariDataSource? = null

    install(Hikari){
            dataSource = datasource
    }

    val taskRepository = TaskRepository()
    val taskService = TaskService(taskRepository)

    install(Routing) {
        getRouter()
        taskController(taskService)
    }
}

private fun Routing.getRouter() {
    get("/") {
        call.respondText("OK")
    }
}