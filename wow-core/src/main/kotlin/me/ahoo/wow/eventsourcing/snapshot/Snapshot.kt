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
package me.ahoo.wow.eventsourcing.snapshot

import me.ahoo.wow.api.modeling.SnapshotTimeCapable
import me.ahoo.wow.api.query.MaterializedSnapshot
import me.ahoo.wow.infra.Decorator
import me.ahoo.wow.modeling.state.ReadOnlyStateAggregate

interface Snapshot<S : Any> : ReadOnlyStateAggregate<S>, SnapshotTimeCapable

data class SimpleSnapshot<S : Any>(
    override val delegate: ReadOnlyStateAggregate<S>,
    override val snapshotTime: Long = System.currentTimeMillis()
) : Snapshot<S>,
    ReadOnlyStateAggregate<S> by delegate,
    Decorator<ReadOnlyStateAggregate<S>>

fun <S : Any> Snapshot<S>.materialize(): MaterializedSnapshot<S> {
    return MaterializedSnapshot(
        contextName = aggregateId.contextName,
        aggregateName = aggregateId.aggregateName,
        aggregateId = aggregateId.id,
        tenantId = aggregateId.tenantId,
        ownerId = ownerId,
        version = version,
        eventId = eventId,
        firstOperator = firstOperator,
        operator = operator,
        firstEventTime = firstEventTime,
        eventTime = eventTime,
        state = state,
        snapshotTime = snapshotTime,
        deleted = deleted
    )
}
