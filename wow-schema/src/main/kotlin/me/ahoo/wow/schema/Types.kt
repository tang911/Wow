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

package me.ahoo.wow.schema

import me.ahoo.wow.api.command.CommandMessage
import me.ahoo.wow.api.event.DomainEvent
import me.ahoo.wow.api.modeling.AggregateId
import me.ahoo.wow.event.DomainEventStream
import me.ahoo.wow.eventsourcing.snapshot.Snapshot
import me.ahoo.wow.eventsourcing.state.StateEvent
import me.ahoo.wow.modeling.state.StateAggregate
import java.lang.reflect.AnnotatedElement

object Types {
    fun AnnotatedElement.isKotlinElement(): Boolean {
        return getAnnotation(Metadata::class.java) != null
    }

    fun Class<*>.isInstanceOf(clazz: Class<*>): Boolean {
        return clazz.isAssignableFrom(this)
    }

    fun Class<*>.isWowType(): Boolean {
        return isInstanceOf(AggregateId::class.java) ||
            isInstanceOf(CommandMessage::class.java) ||
            isInstanceOf(DomainEvent::class.java) ||
            isInstanceOf(DomainEventStream::class.java) ||
            isInstanceOf(Snapshot::class.java) ||
            isInstanceOf(StateAggregate::class.java) ||
            isInstanceOf(StateEvent::class.java)
    }

    @Suppress("ComplexCondition")
    fun Class<*>.isStdType(): Boolean {
        if (this.isArray || this.isPrimitive || this.isEnum) {
            return true
        }

        if (name.startsWith("java.") || name.startsWith("javax.") ||
            name.startsWith("kotlin.") || name.startsWith("kotlinx.")
        ) {
            return true
        }

        return when (this) {
            String::class.java -> true
            else -> false
        }
    }
}
