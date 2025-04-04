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

package me.ahoo.wow.messaging.dispatcher

import io.github.oshai.kotlinlogging.KotlinLogging
import me.ahoo.wow.api.modeling.NamedAggregate
import me.ahoo.wow.messaging.MessageDispatcher
import me.ahoo.wow.messaging.writeReceiverGroup
import me.ahoo.wow.metrics.Metrics.writeMetricsSubscriber
import me.ahoo.wow.serialization.toJsonString
import reactor.core.publisher.Flux

abstract class AbstractDispatcher<T : Any> : MessageDispatcher {
    companion object {
        private val log = KotlinLogging.logger {}
    }

    /**
     * must be [me.ahoo.wow.modeling.MaterializedNamedAggregate]
     */
    abstract val namedAggregates: Set<NamedAggregate>

    abstract fun receiveMessage(namedAggregate: NamedAggregate): Flux<T>
    abstract fun newAggregateDispatcher(namedAggregate: NamedAggregate, messageFlux: Flux<T>): MessageDispatcher
    protected val aggregateDispatchers by lazy {
        namedAggregates
            .map {
                val messageFlux = receiveMessage(it)
                    .writeReceiverGroup(name)
                    .writeMetricsSubscriber(name)
                newAggregateDispatcher(it, messageFlux)
            }
    }

    override fun run() {
        log.info {
            "[$name] Run subscribe to namedAggregates:${namedAggregates.toJsonString()}."
        }
        if (namedAggregates.isEmpty()) {
            log.warn {
                "[$name] Ignore start because namedAggregates is empty."
            }
            return
        }
        aggregateDispatchers.forEach { it.run() }
    }

    override fun close() {
        log.info {
            "[$name] Close."
        }
        aggregateDispatchers.forEach { it.close() }
    }
}
