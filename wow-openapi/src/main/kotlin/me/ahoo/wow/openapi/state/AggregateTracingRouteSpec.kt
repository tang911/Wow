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

package me.ahoo.wow.openapi.state

import io.swagger.v3.oas.models.parameters.RequestBody
import io.swagger.v3.oas.models.responses.ApiResponses
import me.ahoo.wow.api.naming.NamedBoundedContext
import me.ahoo.wow.eventsourcing.state.StateEvent
import me.ahoo.wow.openapi.AbstractAggregateRouteSpecFactory
import me.ahoo.wow.openapi.AggregateRouteSpec
import me.ahoo.wow.openapi.Https
import me.ahoo.wow.openapi.ResponseRef.Companion.toResponse
import me.ahoo.wow.openapi.RouteIdSpec
import me.ahoo.wow.openapi.SchemaRef.Companion.toArraySchema
import me.ahoo.wow.openapi.SchemaRef.Companion.toSchemaRef
import me.ahoo.wow.openapi.route.AggregateRouteMetadata

class AggregateTracingRouteSpec(
    override val currentContext: NamedBoundedContext,
    override val aggregateRouteMetadata: AggregateRouteMetadata<*>,
) : AggregateRouteSpec {
    override val id: String
        get() = RouteIdSpec()
            .aggregate(aggregateMetadata)
            .appendTenant(appendTenantPath)
            .resourceName("aggregate_tracing")
            .operation("get")
            .build()

    override val method: String
        get() = Https.Method.GET
    override val appendOwnerPath: Boolean
        get() = false
    override val appendIdPath: Boolean
        get() = true
    override val appendPathSuffix: String
        get() = "state/tracing"
    override val summary: String
        get() = "Get aggregate tracing"
    override val requestBody: RequestBody? = null
    val responseSchemaRef = StateEvent::class.java
        .toSchemaRef(StateEvent<*>::state.name, aggregateMetadata.state.aggregateType)
    override val responses: ApiResponses
        get() = responseSchemaRef.ref.toArraySchema().toResponse().let {
            ApiResponses().addApiResponse(Https.Code.OK, it)
        }
}

class AggregateTracingRouteSpecFactory : AbstractAggregateRouteSpecFactory() {

    override fun create(
        currentContext: NamedBoundedContext,
        aggregateRouteMetadata: AggregateRouteMetadata<*>
    ): List<AggregateRouteSpec> {
        val routeSpec = AggregateTracingRouteSpec(currentContext, aggregateRouteMetadata)
        routeSpec.responseSchemaRef.schemas.mergeSchemas()
        return listOf(routeSpec)
    }
}
