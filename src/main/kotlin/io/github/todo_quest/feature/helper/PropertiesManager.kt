package io.github.todo_quest.feature.helper

import com.typesafe.config.*
import io.github.config4k.*
import io.github.config4k.readers.*
import io.ktor.application.*
import io.ktor.util.*
import java.lang.reflect.*
import kotlin.reflect.*


class PropertiesManager private constructor() {
    companion object PropertiesFeature : ApplicationFeature<Application, Config, PropertiesManager> {

        private inline fun <reified T : Annotation> KClass<*>.findAnnotationSafe() = try {
            this.annotations.firstOrNull { it is T } as T?
        } catch (e: UnsupportedOperationException) {
            this.java.annotations.firstOrNull { it is T } as T?
        }

        override fun install(pipeline: Application, configure: Config.() -> Unit): PropertiesManager {
            conf = ConfigFactory.load()
            return PropertiesManager()
        }

        override val key: AttributeKey<PropertiesManager> =
            AttributeKey(this::class.simpleName ?: "factory.${System.currentTimeMillis()}")

        private lateinit var conf: Config

        @Suppress("unused")
        inline fun <reified T : Any> properties(path: String? = null) = properties(T::class, path)

        fun <T : Any> properties(clazz: KClass<T>, path: String? = null) = (
                this.conf.takeIf { this::conf.isInitialized } ?: throw Throwable("config not initialized")
                ).let { cfg ->
                path
                    ?.let {
                        cfg.extract(path, clazz)
                    }
                    ?: clazz.findAnnotationSafe<Properties>()
                        ?.let { annotation ->
                            cfg.extract(annotation.path, clazz)
                                ?: clazz.constructors.firstOrNull()?.call()
                                ?: throw Exception("not found empty constructor for config class ${clazz.simpleName}")
                        }
                    //
                    ?: cfg.extract(path, clazz)
            }

        @Suppress("unused")
        inline fun <reified T : Any> fetchMap(path: String? = null) = fetchMap(T::class, path)

        fun <T : Any> fetchMap(clazz: KClass<T>, path: String? = null) = (this.conf.takeIf { this::conf.isInitialized }
            ?: throw Throwable("config not initialized")).let { cfg ->
            when {
                cfg.isEmpty -> emptyMap<String, T>()
                path == null -> cfg.root().keys.map { k ->
                    k to (cfg.extract(k, clazz) ?: throw Throwable("config under $k invalid"))
                }.toMap()
                !cfg.hasPath(path) -> null
                else -> cfg.getConfig(path).root().keys.map { k ->
                    k to (cfg.extract(k, clazz) ?: throw Throwable("config under $k invalid"))
                }.toMap()
            }
        }

        @Suppress("UNCHECKED_CAST", "UNUSED")
        private fun <T : Any> Config.tryExtract(kClass: KClass<T>, path: String): T? =
            when (kClass) {
                String::class -> this.getString(path) as? T
                Double::class -> this.getDouble(path) as? T
                Int::class -> this.getInt(path) as? T
                Long::class -> this.getLong(path) as? T
                Boolean::class -> this.getBoolean(path) as? T
                else -> extract(path, kClass)
            }

        fun dump() = this.conf.takeIf { this::conf.isInitialized }

        @Suppress("UNCHECKED_CAST")
        private fun <T : Any> Config.extract(path: String? = null, clazz: KClass<T>): T? {
            val genericType = object : TypeReference<T>() {}.genericType().let { ClassContainer(clazz, it) }
            val result = if (path.isNullOrBlank()) {
                SelectReader.extractWithoutPath(genericType, this)
            } else SelectReader.getReader(genericType)(this, path)
            return try {
                result as T
            } catch (e: Exception) {
                throw result?.let { e } ?: ConfigException.BadPath(
                    path, "take a look at your config"
                )
            }
        }

        @Suppress("unused")
        inline fun <reified T> extract(path: String = "") = dump()?.extract<T>(path)

        @Suppress("unused")
        private fun getGenericMap(
            type: ParameterizedType,
            typeArguments: Map<String, ClassContainer> = emptyMap()
        ): Map<String, ClassContainer> {
            val typeParameters = (type.rawType as Class<*>).kotlin.typeParameters
            return type.actualTypeArguments.mapIndexed { index, r ->
                val typeParameterName = typeParameters[index].name
                val impl = if (r is WildcardType) r.upperBounds[0] else r
                typeParameterName to if (impl is TypeVariable<*>) {
                    requireNotNull(typeArguments[impl.name]) { "no type argument for ${impl.name} found" }
                } else {
                    val wild = ((if (impl is ParameterizedType) impl.rawType else impl) as Class<*>).kotlin
                    if (impl is ParameterizedType) ClassContainer(wild, getGenericMap(impl, typeArguments))
                    else ClassContainer(wild)
                }
            }.toMap()
        }
    }
}