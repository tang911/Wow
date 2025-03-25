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

package me.ahoo.wow.openapi.aggregate.command

import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.models.media.ArraySchema
import io.swagger.v3.oas.models.media.IntegerSchema
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.responses.ApiResponse
import me.ahoo.wow.api.Wow
import me.ahoo.wow.command.CommandResult
import me.ahoo.wow.command.wait.CommandStage
import me.ahoo.wow.exception.ErrorCodes
import me.ahoo.wow.openapi.CommonComponent
import me.ahoo.wow.openapi.CommonComponent.Header.errorCodeHeader
import me.ahoo.wow.openapi.Https
import me.ahoo.wow.openapi.aggregate.command.CommandComponent.Schema.commandResultSchema
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.AGGREGATE_ID
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.AGGREGATE_VERSION
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.COMMAND_AGGREGATE_CONTEXT
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.COMMAND_AGGREGATE_NAME
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.COMMAND_TYPE
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.LOCAL_FIRST
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.OWNER_ID
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.REQUEST_ID
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.TENANT_ID
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.WAIT_CONTEXT
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.WAIT_PROCESSOR
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.WAIT_STAGE
import me.ahoo.wow.openapi.aggregate.command.CommandRequestHeaders.WAIT_TIME_OUT
import me.ahoo.wow.openapi.context.OpenAPIComponentContext

object CommandComponent {
    object Header {
        const val COMMAND_HEADERS_PREFIX = "Command-"
        const val WAIT_CONTEXT = "${COMMAND_HEADERS_PREFIX}Wait-Context"
        const val TENANT_ID = "${COMMAND_HEADERS_PREFIX}Tenant-Id"
        const val OWNER_ID = "${COMMAND_HEADERS_PREFIX}Owner-Id"
        const val AGGREGATE_ID = "${COMMAND_HEADERS_PREFIX}Aggregate-Id"
        const val AGGREGATE_VERSION = "${COMMAND_HEADERS_PREFIX}Aggregate-Version"
        const val WAIT_STAGE = "${COMMAND_HEADERS_PREFIX}Wait-Stage"
        const val WAIT_TIME_OUT = "${COMMAND_HEADERS_PREFIX}Wait-Timout"

        const val WAIT_PROCESSOR = "${COMMAND_HEADERS_PREFIX}Wait-Processor"
        const val REQUEST_ID = "${COMMAND_HEADERS_PREFIX}Request-Id"
        const val LOCAL_FIRST = "${COMMAND_HEADERS_PREFIX}Local-First"

        const val COMMAND_AGGREGATE_CONTEXT = "${COMMAND_HEADERS_PREFIX}Aggregate-Context"
        const val COMMAND_AGGREGATE_NAME = "${COMMAND_HEADERS_PREFIX}Aggregate-Name"
        const val COMMAND_TYPE = "${COMMAND_HEADERS_PREFIX}Type"

        const val COMMAND_HEADER_X_PREFIX = "${COMMAND_HEADERS_PREFIX}Header-"
    }

    object Schema {
        fun OpenAPIComponentContext.commandResultSchema(): io.swagger.v3.oas.models.media.Schema<*> =
            schema(CommandResult::class.java)
    }

    object Parameter {
        fun OpenAPIComponentContext.waitContextPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = WAIT_CONTEXT
                schema = schema(String::class.java)
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.tenantIdPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = TENANT_ID
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.ownerIdPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = OWNER_ID
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.aggregateIdPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = AGGREGATE_ID
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.aggregateVersionPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = AGGREGATE_VERSION
                schema = IntegerSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.waitStagePathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = WAIT_STAGE
                schema = schema(CommandStage::class.java)
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.waitProcessorPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = WAIT_PROCESSOR
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.waitTimeOutPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = WAIT_TIME_OUT
                schema = IntegerSchema()
                `in`(ParameterIn.HEADER.toString())
                description = "Command timeout period. Milliseconds"
            }

        fun OpenAPIComponentContext.requestIdPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = REQUEST_ID
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.localFirstPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = LOCAL_FIRST
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.commandAggregateContextPathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = COMMAND_AGGREGATE_CONTEXT
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.commandAggregateNamePathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = COMMAND_AGGREGATE_NAME
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }

        fun OpenAPIComponentContext.commandTypePathParameter(): io.swagger.v3.oas.models.parameters.Parameter =
            parameter {
                name = COMMAND_TYPE
                schema = StringSchema()
                `in`(ParameterIn.HEADER.toString())
            }
    }

    object Response {
        fun OpenAPIComponentContext.okCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}${ErrorCodes.SUCCEEDED}") {
                description(ErrorCodes.SUCCEEDED_MESSAGE)
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
                content(name = Https.MediaType.TEXT_EVENT_STREAM, schema = ArraySchema().items(commandResultSchema()))
            }

        fun OpenAPIComponentContext.badRequestCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}Command${ErrorCodes.BAD_REQUEST}") {
                description("Command Bad Request")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }

        fun OpenAPIComponentContext.notFoundCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}Command${ErrorCodes.NOT_FOUND}") {
                description("Aggregate Not Found")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }

        fun OpenAPIComponentContext.requestTimeoutCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}Command${ErrorCodes.REQUEST_TIMEOUT}") {
                description("Command Request Timeout")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }

        fun OpenAPIComponentContext.tooManyRequestsCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}Command${ErrorCodes.TOO_MANY_REQUESTS}") {
                description("Command Too Many Requests")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }

        fun OpenAPIComponentContext.versionConflictCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}CommandVersionConflict") {
                description("Command Version Conflict")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }

        fun OpenAPIComponentContext.illegalAccessDeletedAggregateCommandResponse(): ApiResponse =
            response("${Wow.WOW_PREFIX}Command${ErrorCodes.ILLEGAL_ACCESS_DELETED_AGGREGATE}") {
                description("Illegal Access Deleted Aggregate")
                header(CommonComponent.Header.WOW_ERROR_CODE, errorCodeHeader())
                content(schema = commandResultSchema())
            }
    }
}
