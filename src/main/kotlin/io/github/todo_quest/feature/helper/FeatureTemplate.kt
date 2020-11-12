package io.github.todo_quest.feature.helper

import io.ktor.application.ApplicationCall
import io.ktor.application.ApplicationFeature
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.Pipeline
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

abstract class FeatureTemplate<C : Any>(val conf: C) {
    abstract class FeatureObjectTemplate<P : Pipeline<*, ApplicationCall>, F : Any, C : Any, CONF : Any> :
        ApplicationFeature<P, C, F> {
        private val log = LoggerFactory.getLogger(this::class.java)
        private lateinit var _factory: F
        val factory by lazy { _factory }
        override val key: AttributeKey<F> =
            AttributeKey<F>(this::class.simpleName ?: "factory.${System.currentTimeMillis()}")
        protected abstract val configClazz: KClass<out CONF>
        protected open val config: CONF? by lazy { PropertiesManager.properties(configClazz) }
        abstract fun init(pipeline: P, configure: C.() -> Unit): F
        override fun install(pipeline: P, configure: C.() -> Unit): F =
            init(pipeline, configure).apply {
                //register attributes
                if(pipeline.attributes.contains(key)){
                    pipeline.attributes.put(AttributeKey("${key.name}_${this.hashCode()}"),this)
                }else{
                    pipeline.attributes.put(key,this)
                }
                this@FeatureObjectTemplate._factory = this
            }
    }
}