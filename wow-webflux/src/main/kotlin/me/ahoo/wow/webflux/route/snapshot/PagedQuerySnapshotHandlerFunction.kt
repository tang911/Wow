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

package me.ahoo.wow.webflux.route.snapshot

import me.ahoo.wow.api.query.PagedQuery
import me.ahoo.wow.modeling.matedata.AggregateMetadata
import me.ahoo.wow.openapi.snapshot.PagedQuerySnapshotRouteSpec
import me.ahoo.wow.query.filter.Contexts.writeRawRequest
import me.ahoo.wow.query.snapshot.filter.SnapshotQueryHandler
import me.ahoo.wow.webflux.exception.RequestExceptionHandler
import me.ahoo.wow.webflux.exception.toServerResponse
import me.ahoo.wow.webflux.route.RouteHandlerFunctionFactory
import me.ahoo.wow.webflux.route.command.CommandParser.getTenantId
import org.springframework.web.reactive.function.server.HandlerFunction
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono

class PagedQuerySnapshotHandlerFunction(
    private val aggregateMetadata: AggregateMetadata<*, *>,
    private val snapshotQueryHandler: SnapshotQueryHandler,
    private val exceptionHandler: RequestExceptionHandler
) : HandlerFunction<ServerResponse> {

    override fun handle(request: ServerRequest): Mono<ServerResponse> {
        val tenantId = request.getTenantId(aggregateMetadata)
        return request.bodyToMono(PagedQuery::class.java)
            .flatMap {
                val pagedQuery = if (tenantId == null) it else it.appendTenantId(tenantId)
                snapshotQueryHandler.dynamicPaged(aggregateMetadata, pagedQuery)
                    .writeRawRequest(request)
            }.toServerResponse(request, exceptionHandler)
    }
}

class PagedQuerySnapshotHandlerFunctionFactory(
    private val snapshotQueryHandler: SnapshotQueryHandler,
    private val exceptionHandler: RequestExceptionHandler
) : RouteHandlerFunctionFactory<PagedQuerySnapshotRouteSpec> {
    override val supportedSpec: Class<PagedQuerySnapshotRouteSpec>
        get() = PagedQuerySnapshotRouteSpec::class.java

    override fun create(spec: PagedQuerySnapshotRouteSpec): HandlerFunction<ServerResponse> {
        return PagedQuerySnapshotHandlerFunction(
            spec.aggregateMetadata,
            snapshotQueryHandler,
            exceptionHandler
        )
    }
}
