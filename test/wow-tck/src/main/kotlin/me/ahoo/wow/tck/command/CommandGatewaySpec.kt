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

package me.ahoo.wow.tck.command

import com.google.common.hash.BloomFilter
import com.google.common.hash.Funnels
import me.ahoo.wow.api.command.CommandMessage
import me.ahoo.wow.api.messaging.TopicKind
import me.ahoo.wow.api.messaging.function.FunctionInfoData
import me.ahoo.wow.api.messaging.function.FunctionKind
import me.ahoo.wow.api.modeling.NamedAggregate
import me.ahoo.wow.command.CommandBus
import me.ahoo.wow.command.CommandGateway
import me.ahoo.wow.command.CommandResultException
import me.ahoo.wow.command.DefaultCommandGateway
import me.ahoo.wow.command.DuplicateRequestIdException
import me.ahoo.wow.command.ServerCommandExchange
import me.ahoo.wow.command.toCommandMessage
import me.ahoo.wow.command.wait.CommandStage
import me.ahoo.wow.command.wait.SimpleCommandWaitEndpoint
import me.ahoo.wow.command.wait.SimpleWaitSignal
import me.ahoo.wow.command.wait.SimpleWaitStrategyRegistrar
import me.ahoo.wow.configuration.requiredNamedAggregate
import me.ahoo.wow.exception.ErrorCodes
import me.ahoo.wow.id.GlobalIdGenerator
import me.ahoo.wow.infra.idempotency.BloomFilterIdempotencyChecker
import me.ahoo.wow.infra.idempotency.DefaultAggregateIdempotencyCheckerProvider
import me.ahoo.wow.infra.idempotency.IdempotencyChecker
import me.ahoo.wow.tck.messaging.MessageBusSpec
import me.ahoo.wow.tck.mock.MockCreateAggregate
import me.ahoo.wow.test.validation.TestValidator
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.kotlin.test.test
import java.time.Duration

abstract class CommandGatewaySpec : MessageBusSpec<CommandMessage<*>, ServerCommandExchange<*>, CommandGateway>() {
    override val topicKind: TopicKind
        get() = TopicKind.COMMAND
    override val namedAggregate: NamedAggregate
        get() = requiredNamedAggregate<MockCreateAggregate>()

    override fun createMessage(): CommandMessage<*> {
        return MockCreateAggregate(
            id = GlobalIdGenerator.generateAsString(),
            data = GlobalIdGenerator.generateAsString(),
        ).toCommandMessage()
    }

    protected val waitStrategyRegistrar = SimpleWaitStrategyRegistrar
    protected val idempotencyChecker: IdempotencyChecker = BloomFilterIdempotencyChecker(
        Duration.ofSeconds(1),
    ) {
        BloomFilter.create(Funnels.stringFunnel(Charsets.UTF_8), 1000000)
    }

    protected abstract fun createCommandBus(): CommandBus

    override fun createMessageBus(): CommandGateway {
        return DefaultCommandGateway(
            commandWaitEndpoint = SimpleCommandWaitEndpoint(""),
            commandBus = createCommandBus(),
            validator = TestValidator,
            idempotencyCheckerProvider = DefaultAggregateIdempotencyCheckerProvider { idempotencyChecker },
            waitStrategyRegistrar = waitStrategyRegistrar,
        )
    }

    @Test
    fun sendAndWaitForSent() {
        val message = createMessage()
        verify {
            sendAndWaitForSent(message)
                .test()
                .expectNextCount(1)
                .verifyComplete()
        }
    }

    @Test
    fun sendAndWaitForProcessed() {
        val message = createMessage()
        verify {
            sendAndWaitForProcessed(message)
                .timeout(Duration.ofMillis(100))
                .test()
                .verifyTimeout(Duration.ofMillis(150))
        }
    }

    @Test
    fun sendAndWaitForSnapshot() {
        val message = createMessage()
        verify {
            sendAndWaitForSnapshot(message)
                .timeout(Duration.ofMillis(100))
                .test()
                .verifyTimeout(Duration.ofMillis(150))
        }
    }

    @Test
    fun sendGivenDuplicate() {
        val message = createMessage()
        verify {
            sendAndWaitForSent(message)
                .test()
                .expectNextCount(1)
                .verifyComplete()
            sendAndWaitForSent(message)
                .test()
                .consumeErrorWith {
                    assertThat(it, instanceOf(CommandResultException::class.java))
                    val commandResultException = it as CommandResultException
                    assertThat(commandResultException.commandResult.errorCode, equalTo(ErrorCodes.DUPLICATE_REQUEST_ID))
                    assertThat(commandResultException.cause, instanceOf(DuplicateRequestIdException::class.java))
                }
                .verify()
        }
    }

    @Test
    fun sendThenWaitingForAggregate() {
        val message = createMessage()
        verify {
            sendAndWaitForProcessed(message)
                .doOnRequest {
                    Mono.fromCallable {
                        waitStrategyRegistrar.next(
                            SimpleWaitSignal(
                                message.commandId,
                                CommandStage.PROCESSED,
                                FunctionInfoData(FunctionKind.COMMAND, message.contextName, "", "")
                            ),
                        )
                    }.delaySubscription(Duration.ofMillis(10)).subscribe()
                }
                .test()
                .expectNextCount(1)
                .verifyComplete()
        }
    }
}
