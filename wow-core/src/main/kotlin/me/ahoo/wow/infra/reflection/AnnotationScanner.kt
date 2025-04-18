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

package me.ahoo.wow.infra.reflection

import me.ahoo.wow.infra.reflection.MergedAnnotation.Companion.toMergedAnnotation
import kotlin.reflect.KAnnotatedElement
import kotlin.reflect.KClass

/**
 * AnnotationScanner
 *
 */
object AnnotationScanner {

    @Suppress("UNCHECKED_CAST")
    fun <A : Annotation> KAnnotatedElement.scanAnnotations(annotationClass: KClass<A>): List<A> {
        return this.toMergedAnnotation()
            .mergedAnnotations
            .filter { it.annotationClass == annotationClass } as List<A>
    }

    fun <A : Annotation> KAnnotatedElement.scanAnnotation(annotationClass: KClass<A>): A? {
        return scanAnnotations(annotationClass).firstOrNull<A>()
    }

    inline fun <reified A : Annotation> KAnnotatedElement.scanAnnotation(): A? {
        return scanAnnotation(A::class)
    }

    inline fun <reified A : Annotation> KAnnotatedElement.scanAnnotations(): List<A> {
        return scanAnnotations(A::class)
    }
}
