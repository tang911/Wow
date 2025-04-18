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

package me.ahoo.wow.query.snapshot.filter

import me.ahoo.wow.api.annotation.ORDER_LAST
import me.ahoo.wow.api.annotation.Order
import me.ahoo.wow.api.query.DynamicDocument
import me.ahoo.wow.api.query.MaterializedSnapshot
import me.ahoo.wow.filter.FilterChain
import me.ahoo.wow.filter.FilterType
import me.ahoo.wow.query.filter.QueryContext
import me.ahoo.wow.query.filter.QueryFilter
import me.ahoo.wow.query.filter.QueryType
import me.ahoo.wow.query.snapshot.SnapshotQueryServiceFactory
import reactor.core.publisher.Mono

@FilterType(SnapshotQueryHandler::class)
interface SnapshotQueryFilter : QueryFilter<QueryContext<*, *>>

@Order(ORDER_LAST)
@FilterType(SnapshotQueryHandler::class)
class TailSnapshotQueryFilter<S : Any>(private val queryServiceFactory: SnapshotQueryServiceFactory) :
    SnapshotQueryFilter {
    override fun filter(
        context: QueryContext<*, *>,
        next: FilterChain<QueryContext<*, *>>
    ): Mono<Void> {
        val queryService = queryServiceFactory.create<S>(context.namedAggregate)
        when (context.queryType) {
            QueryType.SINGLE -> {
                context.asSingleQuery<MaterializedSnapshot<S>>().setResult {
                    queryService.single(it)
                }
            }

            QueryType.DYNAMIC_SINGLE -> {
                context.asSingleQuery<DynamicDocument>().setResult {
                    queryService.dynamicSingle(it)
                }
            }

            QueryType.LIST -> {
                context.asListQuery<MaterializedSnapshot<S>>().setResult {
                    queryService.list(it)
                }
            }

            QueryType.DYNAMIC_LIST -> {
                context.asListQuery<DynamicDocument>().setResult {
                    queryService.dynamicList(it)
                }
            }

            QueryType.PAGED -> {
                context.asPagedQuery<MaterializedSnapshot<S>>().setResult {
                    queryService.paged(it)
                }
            }

            QueryType.DYNAMIC_PAGED -> {
                context.asPagedQuery<DynamicDocument>().setResult {
                    queryService.dynamicPaged(it)
                }
            }

            QueryType.COUNT -> {
                context.asCountQuery().setResult {
                    queryService.count(it)
                }
            }
        }
        return next.filter(context)
    }
}
