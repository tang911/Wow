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

import me.ahoo.wow.filter.FilterChainBuilder
import me.ahoo.wow.filter.LogErrorHandler
import me.ahoo.wow.query.filter.QueryContext
import me.ahoo.wow.query.snapshot.NoOpSnapshotQueryServiceFactory
import me.ahoo.wow.query.snapshot.filter.DefaultSnapshotQueryHandler
import me.ahoo.wow.query.snapshot.filter.SnapshotQueryHandler
import me.ahoo.wow.query.snapshot.filter.TailSnapshotQueryFilter

object MockQueryHandler {
    private val tailSnapshotQueryFilter = TailSnapshotQueryFilter<Any>(NoOpSnapshotQueryServiceFactory)
    private val snapshotQueryFilterChain = FilterChainBuilder<QueryContext<*, *>>()
        .addFilters(listOf(tailSnapshotQueryFilter))
        .filterCondition(SnapshotQueryHandler::class)
        .build()
    val queryHandler = DefaultSnapshotQueryHandler(
        snapshotQueryFilterChain,
        LogErrorHandler()
    )
}
