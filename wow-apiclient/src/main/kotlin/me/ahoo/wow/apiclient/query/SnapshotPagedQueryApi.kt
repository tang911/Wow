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

package me.ahoo.wow.apiclient.query

import me.ahoo.wow.api.query.IPagedQuery
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.service.annotation.PostExchange

const val SNAPSHOT_PAGED_QUERY_RESOURCE_NAME = "$SNAPSHOT_RESOURCE_NAME/paged"
const val SNAPSHOT_PAGED_QUERY_STATE_RESOURCE_NAME = "$SNAPSHOT_PAGED_QUERY_RESOURCE_NAME/state"

interface SnapshotPagedQueryApi<R, RD, RS> : SnapshotQueryApi {
    @PostExchange(SNAPSHOT_PAGED_QUERY_RESOURCE_NAME)
    fun paged(@RequestBody pagedQuery: IPagedQuery): R

    @PostExchange(SNAPSHOT_PAGED_QUERY_RESOURCE_NAME)
    fun dynamicPaged(@RequestBody pagedQuery: IPagedQuery): RD

    @PostExchange(SNAPSHOT_PAGED_QUERY_STATE_RESOURCE_NAME)
    fun pagedState(@RequestBody pagedQuery: IPagedQuery): RS
}
