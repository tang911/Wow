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

package me.ahoo.wow.apiclient.command

import me.ahoo.wow.api.command.validation.CommandValidator
import me.ahoo.wow.command.CommandResult
import me.ahoo.wow.command.CommandResultException
import org.springframework.web.reactive.function.client.WebClientResponseException

interface RestCommandGateway {

    companion object {
        fun CommandRequest.validate() {
            if (body is CommandValidator) {
                body.validate()
            }
        }

        fun WebClientResponseException.toException(): Exception {
            try {
                val commandResult = getResponseBodyAs(CommandResult::class.java)
                if (commandResult != null) {
                    return CommandResultException(commandResult, this)
                }
            } catch (ignore: Throwable) {
                // ignore
            }
            return this
        }
    }
}