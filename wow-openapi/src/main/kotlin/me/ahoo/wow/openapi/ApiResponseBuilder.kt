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

package me.ahoo.wow.openapi

import io.swagger.v3.oas.models.headers.Header
import io.swagger.v3.oas.models.media.Content
import io.swagger.v3.oas.models.media.MediaType
import io.swagger.v3.oas.models.media.Schema
import io.swagger.v3.oas.models.responses.ApiResponse

class ApiResponseBuilder {
    companion object {
        const val DEFAULT_MEDIA_TYPE_NAME = Https.MediaType.APPLICATION_JSON
    }

    private val apiResponse = ApiResponse().content(Content())
    fun description(description: String): ApiResponseBuilder {
        apiResponse.description(description)
        return this
    }

    fun header(headerName: String, header: Header): ApiResponseBuilder {
        apiResponse.addHeaderObject(headerName, header)
        return this
    }

    fun content(mediaTypeName: String = DEFAULT_MEDIA_TYPE_NAME, mediaType: MediaType): ApiResponseBuilder {
        apiResponse.content.addMediaType(mediaTypeName, mediaType)
        return this
    }

    fun content(mediaTypeName: String = DEFAULT_MEDIA_TYPE_NAME, schema: Schema<*>): ApiResponseBuilder {
        apiResponse.content.addMediaType(mediaTypeName, MediaType().schema(schema))
        return this
    }

    fun listContent(schema: Schema<*>): ApiResponseBuilder {
        apiResponse.content.addMediaType(Https.MediaType.APPLICATION_JSON, MediaType().schema(schema))
        apiResponse.content.addMediaType(Https.MediaType.TEXT_EVENT_STREAM, MediaType().schema(schema))
        return this
    }

    fun build(): ApiResponse {
        return apiResponse
    }
}
