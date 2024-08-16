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

import me.ahoo.wow.query.snapshot.filter.Contexts.getRawRequest
import me.ahoo.wow.query.snapshot.filter.Contexts.writeRawRequest
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.*
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.test.test

class ContextsTest {

    @Test
    fun writeRawRequest() {
        Mono.deferContextual {
            assertThat(it.getRawRequest<ContextsTest>(), equalTo(this))
            Mono.empty<Void>()
        }.writeRawRequest(this)
            .test()
            .verifyComplete()
    }
}