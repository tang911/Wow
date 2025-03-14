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

package me.ahoo.wow.command.wait

import reactor.core.Scannable
import reactor.core.publisher.Flux
import reactor.core.publisher.Sinks
import java.util.*

interface WaitingFor : WaitStrategy {
    val stage: CommandStage

    companion object {

        fun processed(): WaitingFor = WaitingForProcessed()

        fun snapshot(): WaitingFor = WaitingForSnapshot()

        fun projected(contextName: String, processorName: String = ""): WaitingFor =
            WaitingForProjected(
                contextName = contextName,
                processorName = processorName
            )

        fun eventHandled(contextName: String, processorName: String = ""): WaitingFor =
            WaitingForEventHandled(
                contextName = contextName,
                processorName = processorName
            )

        fun sagaHandled(contextName: String, processorName: String = ""): WaitingFor =
            WaitingForSagaHandled(
                contextName = contextName,
                processorName = processorName
            )

        fun stage(stage: CommandStage, contextName: String, processorName: String = ""): WaitingFor {
            return when (stage) {
                CommandStage.PROCESSED -> processed()
                CommandStage.SNAPSHOT -> snapshot()
                CommandStage.PROJECTED -> projected(contextName, processorName)
                CommandStage.EVENT_HANDLED -> eventHandled(contextName, processorName)
                CommandStage.SAGA_HANDLED -> sagaHandled(contextName, processorName)
                CommandStage.SENT -> throw IllegalArgumentException("Unsupported stage: $stage")
            }
        }

        fun stage(stage: String, contextName: String, processorName: String = ""): WaitingFor =
            stage(
                stage = CommandStage.valueOf(stage.uppercase(Locale.getDefault())),
                contextName = contextName,
                processorName = processorName
            )
    }
}

abstract class AbstractWaitingFor : WaitingFor {

    private val sink: Sinks.Many<WaitSignal> = Sinks.many().unicast().onBackpressureBuffer()
    override val cancelled: Boolean
        get() = Scannable.from(sink).scanOrDefault(Scannable.Attr.CANCELLED, false)

    override val terminated: Boolean
        get() = Scannable.from(sink).scanOrDefault(Scannable.Attr.TERMINATED, false)

    override fun waiting(): Flux<WaitSignal> {
        return sink.asFlux()
    }

    override fun next(signal: WaitSignal) {
        sink.tryEmitNext(signal).orThrow()
    }

    override fun error(throwable: Throwable) {
        sink.tryEmitError(throwable).orThrow()
    }

    override fun complete() {
        sink.tryEmitComplete().orThrow()
    }
}
