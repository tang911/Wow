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

package me.ahoo.wow.query.dsl

import me.ahoo.test.asserts.assert
import me.ahoo.wow.api.query.Condition
import me.ahoo.wow.api.query.Sort
import org.junit.jupiter.api.Test

class PagedQueryDslTest {

    @Suppress("LongMethod")
    @Test
    fun build() {
        val pagedQuery = pagedQuery {
            pagination {
                index(1)
                size(10)
            }
            sort {
                "field1".asc()
            }
            condition {
                "field1" eq "value1"
                "field2" ne "value2"
                "filed3" gt 1
                "field4" lt 1
                "field5" gte 1
                "field6" lte 1
                "field7" contains "value7"
                "field8" isIn listOf("value8")
                "field9" notIn listOf("value9")
                "field10" between (1 to 2)
                "field11" all listOf("value11")
                "field12" startsWith "value12"
                "field13" elemMatch {
                    "field14" eq "value14"
                }
                "field15".isNull()
                "field16".notNull()
                and {
                    "field3" eq "value3"
                    "field4" eq "value4"
                }
                or {
                    "field3" eq "value3"
                    "field4" eq "value4"
                }
            }
        }
        pagedQuery.pagination.index.assert().isOne()
        pagedQuery.pagination.size.assert().isEqualTo(10)
        pagedQuery.sort.assert().isEqualTo(listOf(Sort("field1", Sort.Direction.ASC)))
        pagedQuery.condition.assert().isEqualTo(
            Condition.and(
                listOf(
                    Condition.eq("field1", "value1"),
                    Condition.ne("field2", "value2"),
                    Condition.gt("filed3", 1),
                    Condition.lt("field4", 1),
                    Condition.gte("field5", 1),
                    Condition.lte("field6", 1),
                    Condition.contains("field7", "value7"),
                    Condition.isIn("field8", listOf("value8")),
                    Condition.notIn("field9", listOf("value9")),
                    Condition.between("field10", 1, 2),
                    Condition.all("field11", listOf("value11")),
                    Condition.startsWith("field12", "value12"),
                    Condition.elemMatch("field13", Condition.eq("field14", "value14")),
                    Condition.isNull("field15"),
                    Condition.notNull("field16"),
                    Condition.and(
                        listOf(
                            Condition.eq("field3", "value3"),
                            Condition.eq("field4", "value4")
                        )
                    ),
                    Condition.or(
                        listOf(
                            Condition.eq("field3", "value3"),
                            Condition.eq("field4", "value4")
                        )
                    )
                )
            )
        )
    }
}
