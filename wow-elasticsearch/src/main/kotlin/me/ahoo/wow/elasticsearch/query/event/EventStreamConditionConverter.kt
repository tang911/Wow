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

package me.ahoo.wow.elasticsearch.query.event

import co.elastic.clients.elasticsearch._types.FieldValue
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.term
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders.terms
import me.ahoo.wow.api.query.Condition
import me.ahoo.wow.elasticsearch.query.AbstractElasticsearchConditionConverter
import me.ahoo.wow.serialization.MessageRecords

object EventStreamConditionConverter : AbstractElasticsearchConditionConverter() {
    override fun convert(condition: Condition): Query = internalConvert(condition)
    override fun aggregateId(condition: Condition): Query {
        return term {
            it.field(MessageRecords.AGGREGATE_ID)
                .value(FieldValue.of(condition.value))
        }
    }

    override fun aggregateIds(condition: Condition): Query {
        return terms {
            it.field(MessageRecords.AGGREGATE_ID)
                .terms { builder ->
                    condition.valueAs<List<Any>>().map {
                        FieldValue.of(it)
                    }.toList().let { builder.value(it) }
                }
        }
    }
}
