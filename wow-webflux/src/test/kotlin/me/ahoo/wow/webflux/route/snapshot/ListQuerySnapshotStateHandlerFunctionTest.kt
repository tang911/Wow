package me.ahoo.wow.webflux.route.snapshot

import me.ahoo.wow.api.query.Condition
import me.ahoo.wow.api.query.ListQuery
import me.ahoo.wow.id.GlobalIdGenerator
import me.ahoo.wow.id.generateGlobalId
import me.ahoo.wow.openapi.route.aggregateRouteMetadata
import me.ahoo.wow.openapi.snapshot.ListQuerySnapshotStateRouteSpec
import me.ahoo.wow.serialization.MessageRecords
import me.ahoo.wow.tck.mock.MOCK_AGGREGATE_METADATA
import me.ahoo.wow.webflux.exception.DefaultRequestExceptionHandler
import org.hamcrest.MatcherAssert.*
import org.hamcrest.Matchers.*
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.mock.web.reactive.function.server.MockServerRequest
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.test.test

class ListQuerySnapshotStateHandlerFunctionTest {

    @Test
    fun handle() {
        val handlerFunction = ListQuerySnapshotStateHandlerFunctionFactory(
            MockQueryHandler.queryHandler,
            exceptionHandler = DefaultRequestExceptionHandler,
        ).create(
            ListQuerySnapshotStateRouteSpec(
                MOCK_AGGREGATE_METADATA,
                aggregateRouteMetadata = MOCK_AGGREGATE_METADATA.command.aggregateType.aggregateRouteMetadata(),
                appendTenantPath = true,
                appendOwnerPath = false
            )
        )
        val request = MockServerRequest.builder()
            .pathVariable(MessageRecords.TENANT_ID, GlobalIdGenerator.generateAsString())
            .pathVariable(MessageRecords.OWNER_ID, generateGlobalId())
            .body(ListQuery(Condition.ALL).toMono())

        handlerFunction.handle(request)
            .test()
            .consumeNextWith {
                assertThat(it.statusCode(), equalTo(HttpStatus.OK))
            }.verifyComplete()
    }
}
