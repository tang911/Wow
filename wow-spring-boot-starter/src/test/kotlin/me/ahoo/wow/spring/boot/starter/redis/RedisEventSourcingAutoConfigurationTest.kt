/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com> (https://github.com/Ahoo-Wang)].
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.ahoo.wow.spring.boot.starter.redis

import io.mockk.mockk
import me.ahoo.wow.redis.eventsourcing.RedisEventStore
import me.ahoo.wow.redis.eventsourcing.RedisSnapshotRepository
import me.ahoo.wow.redis.prepare.RedisPrepareKeyFactory
import me.ahoo.wow.spring.boot.starter.enableWow
import me.ahoo.wow.spring.boot.starter.eventsourcing.StorageType
import me.ahoo.wow.spring.boot.starter.eventsourcing.snapshot.SnapshotProperties
import me.ahoo.wow.spring.boot.starter.eventsourcing.store.EventStoreProperties
import me.ahoo.wow.spring.boot.starter.prepare.PrepareProperties
import me.ahoo.wow.spring.boot.starter.prepare.PrepareStorage
import org.assertj.core.api.AssertionsForInterfaceTypes
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.assertj.AssertableApplicationContext
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.data.redis.core.ReactiveStringRedisTemplate

class RedisEventSourcingAutoConfigurationTest {
    private val contextRunner = ApplicationContextRunner()

    @Test
    fun contextLoads() {
        contextRunner
            .enableWow()
            .withPropertyValues(
                "${SnapshotProperties.STORAGE}=${StorageType.REDIS_NAME}",
                "${EventStoreProperties.STORAGE}=${StorageType.REDIS_NAME}",
                "${PrepareProperties.STORAGE}=${PrepareStorage.REDIS_NAME}",
            )
            .withBean(ReactiveStringRedisTemplate::class.java, {
                mockk<ReactiveStringRedisTemplate>()
            })
            .withUserConfiguration(
                RedisEventSourcingAutoConfiguration::class.java,
            )
            .run { context: AssertableApplicationContext ->
                AssertionsForInterfaceTypes.assertThat(context)
                    .hasSingleBean(RedisEventStore::class.java)
                    .hasSingleBean(RedisSnapshotRepository::class.java)
                    .hasSingleBean(RedisPrepareKeyFactory::class.java)
            }
    }
}
