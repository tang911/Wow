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

package me.ahoo.wow.webflux.route.appender

import io.swagger.v3.oas.annotations.enums.ParameterIn
import me.ahoo.wow.api.Wow
import me.ahoo.wow.api.naming.NamedBoundedContext
import me.ahoo.wow.eventsourcing.AggregateIdScanner.Companion.FIRST_CURSOR_ID
import me.ahoo.wow.eventsourcing.EventStore
import me.ahoo.wow.eventsourcing.state.StateEventBus
import me.ahoo.wow.messaging.compensation.CompensationConfig
import me.ahoo.wow.modeling.asStringWithAlias
import me.ahoo.wow.modeling.matedata.AggregateMetadata
import me.ahoo.wow.modeling.state.StateAggregateFactory
import me.ahoo.wow.route.AggregateRoutePathSpec.Companion.asIgnoreTenantAggregateRoutePathSpec
import me.ahoo.wow.webflux.exception.ExceptionHandler
import me.ahoo.wow.webflux.route.BatchResult
import me.ahoo.wow.webflux.route.RegenerateStateEventFunction
import me.ahoo.wow.webflux.route.appender.RoutePaths.BATCH_CURSOR_ID
import me.ahoo.wow.webflux.route.appender.RoutePaths.BATCH_LIMIT
import org.springdoc.core.fn.builders.operation.Builder
import org.springdoc.webflux.core.fn.SpringdocRouteBuilder
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.server.RequestPredicates
import java.util.function.Consumer

@Suppress("LongParameterList")
class RegenerateStateEventRouteAppender(
    private val currentContext: NamedBoundedContext,
    private val aggregateMetadata: AggregateMetadata<*, *>,
    private val routerFunctionBuilder: SpringdocRouteBuilder,
    private val stateAggregateFactory: StateAggregateFactory,
    private val eventStore: EventStore,
    private val stateEventBus: StateEventBus,
    private val exceptionHandler: ExceptionHandler
) {
    fun append() {
        val routePrefix = aggregateMetadata.asIgnoreTenantAggregateRoutePathSpec(currentContext).aggregateNamePath
        routerFunctionBuilder
            .POST(
                "$routePrefix/state/{$BATCH_CURSOR_ID}/{$BATCH_LIMIT}",
                RequestPredicates.accept(MediaType.APPLICATION_JSON),
                RegenerateStateEventFunction(
                    aggregateMetadata = aggregateMetadata,
                    stateAggregateFactory = stateAggregateFactory,
                    eventStore = eventStore,
                    stateEventBus = stateEventBus,
                    exceptionHandler = exceptionHandler,
                ),
                batchRegenerateSnapshotOperation(),
            )
    }

    private fun batchRegenerateSnapshotOperation(): Consumer<Builder> {
        return Consumer<Builder> {
            it
                .tag(Wow.WOW)
                .tag(aggregateMetadata.asStringWithAlias())
                .summary("Regenerate State Event")
                .operationId("${aggregateMetadata.asStringWithAlias()}.regenerateStateEvent")
                .parameter(
                    org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder()
                        .name(BATCH_CURSOR_ID)
                        .`in`(ParameterIn.PATH)
                        .implementation(String::class.java)
                        .example(FIRST_CURSOR_ID),
                )
                .parameter(
                    org.springdoc.core.fn.builders.parameter.Builder.parameterBuilder()
                        .name(BATCH_LIMIT)
                        .`in`(ParameterIn.PATH)
                        .implementation(Int::class.java)
                        .example(Int.MAX_VALUE.toString()),
                )
                .requestBody(
                    org.springdoc.core.fn.builders.requestbody.Builder.requestBodyBuilder()
                        .required(true)
                        .implementation(CompensationConfig::class.java),
                )
                .response(
                    org.springdoc.core.fn.builders.apiresponse.Builder.responseBuilder()
                        .responseCode(HttpStatus.OK.value().toString())
                        .description(HttpStatus.OK.reasonPhrase)
                        .implementation(BatchResult::class.java),
                )
        }
    }
}