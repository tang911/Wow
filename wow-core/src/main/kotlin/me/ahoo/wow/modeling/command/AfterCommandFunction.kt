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

package me.ahoo.wow.modeling.command

import me.ahoo.wow.api.annotation.AfterCommand
import me.ahoo.wow.command.ServerCommandExchange
import me.ahoo.wow.messaging.function.MessageFunction
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

class AfterCommandFunction<C : Any>(val delegate: MessageFunction<C, ServerCommandExchange<*>, Mono<*>>) {

    private val include: Set<Class<*>>
    private val exclude: Set<Class<*>>

    init {
        val afterCommandAnnotation = delegate.getAnnotation(AfterCommand::class.java)
        if (afterCommandAnnotation == null) {
            include = emptySet()
            exclude = emptySet()
        } else {
            include = afterCommandAnnotation.include.map { it.java }.toSet()
            exclude = afterCommandAnnotation.exclude.map { it.java }.toSet()
        }
    }

    fun matchCommand(commandType: Class<*>): Boolean {
        if (exclude.contains(commandType)) {
            return false
        }

        if (include.isEmpty()) {
            return true
        }
        return include.contains(commandType)
    }

    inline fun afterCommand(
        exchange: ServerCommandExchange<*>,
        commandMono: () -> Mono<*>
    ): Mono<*> {
        if (!matchCommand(exchange.message.body.javaClass)) {
            return commandMono()
        }
        return commandMono().flatMap { commandEvent ->
            delegate.invoke(exchange).map { afterEvent ->
                mergeEvents(commandEvent, afterEvent) as Any
            }.switchIfEmpty(commandEvent.toMono())
        }
    }

    companion object {
        fun mergeEvents(commandEvent: Any, afterEvent: Any): List<Any> {
            val commandEvents: List<Any> = unfoldEvent(commandEvent)
            val afterEvents: List<Any> = unfoldEvent(afterEvent)
            return commandEvents + afterEvents
        }

        @Suppress("UNCHECKED_CAST")
        private fun unfoldEvent(event: Any): List<Any> {
            return when (event) {
                is Iterable<*> -> {
                    event.toList() as List<Any>
                }

                is Array<*> -> {
                    event.toList() as List<Any>
                }

                else -> {
                    listOf(event)
                }
            }
        }
    }
}
