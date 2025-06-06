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

object Https {
    object Header {
        const val ACCEPT = "Accept"
    }

    object Code {
        const val OK = "200"
        const val BAD_REQUEST = "400"
        const val NOT_FOUND = "404"
        const val REQUEST_TIMEOUT = "408"
        const val CONFLICT = "409"
        const val GONE = "410"
        const val TOO_MANY_REQUESTS = "429"
    }

    object Method {
        const val GET = "GET"
        const val POST = "POST"
        const val PUT = "PUT"
        const val DELETE = "DELETE"
        const val PATCH = "PATCH"
        const val HEAD = "HEAD"
        const val OPTIONS = "OPTIONS"
        const val TRACE = "TRACE"
    }

    object MediaType {
        const val APPLICATION_JSON = "application/json"
        const val APPLICATION_SQL = "application/sql"
        const val TEXT_PLAIN = "text/plain"
        const val TEXT_EVENT_STREAM: String = "text/event-stream"
    }
}
