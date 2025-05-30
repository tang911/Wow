package me.ahoo.wow.webflux.wait

import io.mockk.every
import io.mockk.mockk
import me.ahoo.test.asserts.assert
import me.ahoo.wow.command.COMMAND_GATEWAY_FUNCTION
import me.ahoo.wow.command.wait.CommandStage
import me.ahoo.wow.command.wait.SimpleWaitSignal
import me.ahoo.wow.command.wait.SimpleWaitStrategyRegistrar
import me.ahoo.wow.id.generateGlobalId
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class CommandWaitHandlerFunctionTest {

    @Test
    fun handle() {
        val commandWaitHandlerFunction = CommandWaitHandlerFunction(SimpleWaitStrategyRegistrar)
        val request = mockk<ServerRequest> {
            every { bodyToMono(SimpleWaitSignal::class.java) } returns SimpleWaitSignal(
                id = generateGlobalId(),
                commandId = "commandId",
                stage = CommandStage.SENT,
                function = COMMAND_GATEWAY_FUNCTION,
            ).toMono()
        }
        val response = commandWaitHandlerFunction.handle(request)
        response.test()
            .consumeNextWith {
                it.statusCode().is2xxSuccessful.assert().isTrue()
            }
            .verifyComplete()
    }
}
