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

package me.ahoo.wow.query

import me.ahoo.wow.api.modeling.NamedAggregate
import me.ahoo.wow.api.query.Condition
import me.ahoo.wow.api.query.IPagedQuery
import me.ahoo.wow.api.query.IQuery
import me.ahoo.wow.api.query.PagedList
import me.ahoo.wow.eventsourcing.snapshot.Snapshot
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface SnapshotQueryService {
    fun <S : Any> single(tenantId: String, condition: Condition): Mono<Snapshot<S>>
    fun <S : Any> query(tenantId: String, query: IQuery): Flux<Snapshot<S>>
    fun <S : Any> pagedQuery(tenantId: String, pagedQuery: IPagedQuery): Mono<PagedList<Snapshot<S>>>
    fun count(tenantId: String, condition: Condition): Mono<Long>
}

object NoOpSnapshotQueryService : SnapshotQueryService {
    override fun <S : Any> single(tenantId: String, condition: Condition): Mono<Snapshot<S>> {
        return Mono.empty()
    }

    override fun <S : Any> query(tenantId: String, query: IQuery): Flux<Snapshot<S>> {
        return Flux.empty()
    }

    override fun <S : Any> pagedQuery(tenantId: String, pagedQuery: IPagedQuery): Mono<PagedList<Snapshot<S>>> {
        return Mono.just(PagedList(0, emptyList()))
    }

    override fun count(tenantId: String, condition: Condition): Mono<Long> {
        return Mono.just(0)
    }
}

interface SnapshotQueryServiceFactory {
    fun create(namedAggregate: NamedAggregate): SnapshotQueryService
}

object NoOpSnapshotQueryServiceFactory : SnapshotQueryServiceFactory {
    override fun create(namedAggregate: NamedAggregate): SnapshotQueryService {
        return NoOpSnapshotQueryService
    }
}