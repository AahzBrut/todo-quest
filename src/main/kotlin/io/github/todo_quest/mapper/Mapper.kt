package io.github.todo_quest.mapper

interface Mapper<T,S> {

    fun from(source: S) : T

    fun from(sources: List<S>) : List<T> =
        sources.map { from(it) }.toList()
}