package io.github.todo_quest.feature

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.github.todo_quest.feature.helper.FeatureTemplate
import io.github.todo_quest.feature.helper.Properties
import io.github.todo_quest.feature.helper.PropertiesManager
import io.ktor.application.Application
import io.ktor.application.install
import java.time.Duration
import kotlin.reflect.KClass

class Hikari private constructor() {
    companion
    object HikariCPFeature : FeatureTemplate.FeatureObjectTemplate<Application, HikariCPFeature, HikariCPFeature, HikariCPFeature.HikariConf>() {
        override val configClazz: KClass<HikariConf> = HikariConf::class
        override fun init(pipeline: Application, configure: HikariCPFeature.() -> Unit): HikariCPFeature {
            pipeline.attributes.computeIfAbsent(PropertiesManager.key){
                pipeline.install(PropertiesManager)
            }
            config ?: throw Exception("hikari configuration invalid!")
            this.apply(configure)
            return this
        }
        @Properties("datasource")
        class HikariConf(
            var url: String,
            var username: String? = null,
            var password: String? = null,
            var driverClassName: String? = null,
            var poolSize: Int = 1,
            var cachePrepStmts: Boolean = true,
            var prepStmtCacheSize: Int = 250,
            var prepStmtCacheSqlLimit: Int = 2048,
            var isolateInternalQueries: Boolean = false,
            var maxLifetime: Duration = Duration.ofMinutes(30),
            var idleTimeout: Duration = Duration.ofMinutes(10),
            var connectionTimeout: Duration = Duration.ofSeconds(30),
            var validationTimeout: Duration = Duration.ofSeconds(5),
            var initializationFailTimeout: Long = 1,
            var readOnly: Boolean = false,
            var autoCommit: Boolean = true,
            var allowPoolSuspension: Boolean = false,
            var schema: String = "",
            var catalog: String = "",
            var connectionInitSql: String = "",
            var connectionTestQuery: String = "",
            var transactionIsolation: String? = null,
            var registerMbeans: Boolean = false,
            var minimumIdle: Int=1,
            var dataSourceProperty:Map<String,Any>?=null,
            var extra: MutableMap<String, HikariConf> = mutableMapOf()
        )

        val datasource by lazy {
            generateDatasource(null,this.config!!)
        }

        val other by lazy {
            this.config!!.extra.map { (k, v) ->
                k to lazy { generateDatasource(k,v) }
            }.toMap()
        }
        private var configInject:(HikariConfig.(name:String?)->HikariConfig)?=null

        fun setConfigInject(ij:HikariConfig.(name:String?)->HikariConfig){
            configInject=ij
        }
        private fun generateDatasource(name:String?,conf: HikariConf) = HikariDataSource(HikariConfig().apply {
            conf.driverClassName?.let{driverClassName=it}
            jdbcUrl = conf.url
            username=conf.username
            isIsolateInternalQueries=conf.isolateInternalQueries
            maxLifetime=conf.maxLifetime.toMillis()
            idleTimeout=conf.idleTimeout.toMillis()
            connectionTimeout=conf.connectionTimeout.toMillis()
            initializationFailTimeout=conf.initializationFailTimeout
            isReadOnly=conf.readOnly
            isAutoCommit=conf.autoCommit
            isAllowPoolSuspension=conf.allowPoolSuspension
            isRegisterMbeans=conf.registerMbeans
            transactionIsolation=conf.transactionIsolation
            validationTimeout=conf.validationTimeout.toMillis()
            minimumIdle=conf.minimumIdle
            schema=conf.schema.takeIf { it.isNotBlank() }
            catalog=conf.catalog.takeIf { it.isNotBlank() }
            connectionInitSql=conf.connectionInitSql.takeIf { it.isNotBlank() }
            connectionTestQuery=conf.connectionTestQuery.takeIf { it.isNotBlank() }
            password=conf.password
            maximumPoolSize = conf.poolSize
            addDataSourceProperty(
                "cachePrepStmts",
                conf.cachePrepStmts
            )
            addDataSourceProperty(
                "prepStmtCacheSize",
                conf.prepStmtCacheSize
            )
            addDataSourceProperty(
                "prepStmtCacheSqlLimit",
                conf.prepStmtCacheSqlLimit
            )
            conf.dataSourceProperty?.forEach { t, u ->
                addDataSourceProperty(t,u)
            }
        }.let { configInject?.invoke(it,name)?:it })
    }
}