package me.ahoo.wow.webflux.route.snapshot

import io.mockk.every
import io.mockk.mockk
import me.ahoo.wow.api.query.Condition
import me.ahoo.wow.api.query.PagedQuery
import me.ahoo.wow.id.GlobalIdGenerator
import me.ahoo.wow.id.generateGlobalId
import me.ahoo.wow.openapi.route.aggregateRouteMetadata
import me.ahoo.wow.openapi.snapshot.PagedQuerySnapshotRouteSpec
import me.ahoo.wow.serialization.MessageRecords
import me.ahoo.wow.tck.mock.MOCK_AGGREGATE_METADATA
import me.ahoo.wow.webflux.exception.DefaultRequestExceptionHandler
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.web.reactive.function.server.ServerRequest
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class PagedQuerySnapshotHandlerFunctionFactoryTest {

    @Test
    fun handle() {
        val handlerFunction = PagedQuerySnapshotHandlerFunctionFactory(
            MockQueryHandler.queryHandler,
            exceptionHandler = DefaultRequestExceptionHandler,
        ).create(
            PagedQuerySnapshotRouteSpec(
                MOCK_AGGREGATE_METADATA,
                aggregateRouteMetadata = MOCK_AGGREGATE_METADATA.command.aggregateType.aggregateRouteMetadata(),
                true
            )
        )
        val request = mockk<ServerRequest> {
            every { pathVariables()[MessageRecords.TENANT_ID] } returns GlobalIdGenerator.generateAsString()
            every { pathVariables()[MessageRecords.OWNER_ID] } returns generateGlobalId()
            every { bodyToMono(PagedQuery::class.java) } returns PagedQuery(Condition.ALL).toMono()
        }

        handlerFunction.handle(request)
            .test()
            .consumeNextWith {
                assertThat(it.statusCode(), equalTo(HttpStatus.OK))
            }.verifyComplete()
    }
}
