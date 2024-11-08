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

package me.ahoo.wow.example.server.cart

import me.ahoo.wow.apiclient.query.queryState
import me.ahoo.wow.example.api.cart.CartData
import me.ahoo.wow.example.api.client.CartQueryClient
import me.ahoo.wow.example.api.client.CartQuerySyncClient
import me.ahoo.wow.query.dsl.singleQuery
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.service.annotation.GetExchange
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@RestController
class CartController(
    private val cartQueryClient: CartQueryClient,
    private val cartQuerySyncClient: CartQuerySyncClient
) {

    @GetExchange("/cart/me")
    fun me(): Mono<CartData> {
        return singleQuery {

        }.queryState(cartQueryClient)
    }

    @GetExchange("/cart/me/sync")
    fun meSync(): Mono<CartData?> {
        return Mono.fromCallable {
            singleQuery {

            }.queryState(cartQuerySyncClient)
        }.subscribeOn(Schedulers.boundedElastic())
    }
}