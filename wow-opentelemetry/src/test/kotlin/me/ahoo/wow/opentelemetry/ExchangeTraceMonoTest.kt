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

package me.ahoo.wow.opentelemetry

import io.mockk.every
import io.mockk.mockk
import io.opentelemetry.api.trace.propagation.W3CTraceContextPropagator
import io.opentelemetry.context.Context
import io.opentelemetry.context.propagation.ContextPropagators
import io.opentelemetry.sdk.OpenTelemetrySdk
import io.opentelemetry.sdk.trace.SdkTracerProvider
import me.ahoo.wow.event.DomainEventExchange
import me.ahoo.wow.eventsourcing.state.StateEventExchange
import me.ahoo.wow.id.generateGlobalId
import me.ahoo.wow.messaging.DefaultHeader
import me.ahoo.wow.messaging.propagation.TraceMessagePropagator.Companion.withTraceId
import me.ahoo.wow.modeling.MaterializedNamedAggregate
import me.ahoo.wow.modeling.aggregateId
import me.ahoo.wow.opentelemetry.eventprocessor.EventProcessorInstrumenter
import me.ahoo.wow.opentelemetry.projection.ProjectionInstrumenter
import me.ahoo.wow.opentelemetry.saga.StatelessSagaInstrumenter
import me.ahoo.wow.opentelemetry.snapshot.SnapshotInstrumenter
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

class ExchangeTraceMonoTest {

    companion object {
        val TEST_NAMED_AGGREGATE = MaterializedNamedAggregate(
            contextName = "MonoTraceTest",
            aggregateName = "MonoTraceTest",
        )

        init {

            val sdkTracerProvider: SdkTracerProvider = SdkTracerProvider.builder()
                .build()
            OpenTelemetrySdk.builder()
                .setTracerProvider(sdkTracerProvider)
                .setPropagators(ContextPropagators.create(W3CTraceContextPropagator.getInstance()))
                .buildAndRegisterGlobal()
        }
    }

    @Test
    fun traceEventProcessor() {
        val exchange = mockk<DomainEventExchange<Any>> {
            every { message.id } returns generateGlobalId()
            every { message.header } returns DefaultHeader.empty()
            every { message.aggregateId } returns TEST_NAMED_AGGREGATE.aggregateId()
            every { getEventFunction() } returns mockk {
                every { qualifiedName } returns "traceEventProcessor"
                every { getError() } returns null
            }
        }

        ExchangeTraceMono(
            parentContext = Context.current(),
            instrumenter = EventProcessorInstrumenter.INSTRUMENTER,
            request = exchange,
            source = Mono.empty(),
        ).test()
            .verifyComplete()
    }

    @Test
    fun traceEventProcessorWithTraceId() {
        val exchange = mockk<DomainEventExchange<Any>> {
            every { message.id } returns generateGlobalId()
            every { message.header } returns DefaultHeader.empty().withTraceId(generateGlobalId())
            every { message.aggregateId } returns TEST_NAMED_AGGREGATE.aggregateId()
            every { getEventFunction() } returns mockk {
                every { qualifiedName } returns "traceEventProcessor"
                every { getError() } returns null
            }
        }

        ExchangeTraceMono(
            parentContext = Context.current(),
            instrumenter = EventProcessorInstrumenter.INSTRUMENTER,
            request = exchange,
            source = Mono.empty(),
        ).test()
            .verifyComplete()
    }

    @Test
    fun traceSagaProcessor() {
        val exchange = mockk<DomainEventExchange<Any>> {
            every { message.id } returns generateGlobalId()
            every { message.header } returns DefaultHeader.empty()
            every { message.aggregateId } returns TEST_NAMED_AGGREGATE.aggregateId()
            every { getEventFunction() } returns mockk {
                every { processor } returns Any()
                every { qualifiedName } returns "traceSagaProcessor"
                every { getError() } returns null
            }
        }

        ExchangeTraceMono(
            parentContext = Context.current(),
            instrumenter = StatelessSagaInstrumenter.INSTRUMENTER,
            request = exchange,
            source = Mono.empty(),
        ).test()
            .verifyComplete()
    }

    @Test
    fun traceProjectionProcessor() {
        val exchange = mockk<DomainEventExchange<Any>> {
            every { message.id } returns generateGlobalId()
            every { message.header } returns DefaultHeader.empty()
            every { message.aggregateId } returns TEST_NAMED_AGGREGATE.aggregateId()
            every { getEventFunction() } returns mockk {
                every { processor } returns Any()
                every { qualifiedName } returns "traceProjectionProcessor"
            }
            every { getError() } returns null
        }

        ExchangeTraceMono(
            parentContext = Context.current(),
            instrumenter = ProjectionInstrumenter.INSTRUMENTER,
            request = exchange,
            source = Mono.empty(),
        ).test()
            .verifyComplete()
    }

    @Test
    fun traceSnapshotProcessor() {
        val exchange = mockk<StateEventExchange<*>> {
            every { message.id } returns generateGlobalId()
            every { message.requestId } returns generateGlobalId()
            every { message.header } returns DefaultHeader.empty()
            every { message.aggregateName } returns TEST_NAMED_AGGREGATE.aggregateName
            every { message.aggregateId } returns TEST_NAMED_AGGREGATE.aggregateId()
            every { getError() } returns null
        }

        ExchangeTraceMono(
            parentContext = Context.current(),
            instrumenter = SnapshotInstrumenter.INSTRUMENTER,
            request = exchange,
            source = Mono.empty(),
        ).test()
            .verifyComplete()
    }
}
