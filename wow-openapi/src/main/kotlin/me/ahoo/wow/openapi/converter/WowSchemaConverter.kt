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

package me.ahoo.wow.openapi.converter

import com.fasterxml.jackson.databind.JavaType
import io.swagger.v3.core.converter.AnnotatedType
import io.swagger.v3.core.converter.ModelConverter
import io.swagger.v3.core.converter.ModelConverterContext
import io.swagger.v3.oas.models.media.Schema
import me.ahoo.wow.openapi.context.CurrentOpenAPIComponentContext
import me.ahoo.wow.openapi.converter.BoundedContextSchemaNameConverter.Companion.resolveName
import me.ahoo.wow.schema.JavaTypeResolver.toResolvedType

class WowSchemaConverter : ModelConverter {
    override fun resolve(
        type: AnnotatedType,
        context: ModelConverterContext,
        chain: Iterator<ModelConverter>
    ): Schema<*>? {
        resolveSchema(type).let {
            if (it != null) {
                return it
            }
        }
        if (chain.hasNext()) {
            return chain.next().resolve(type, context, chain)
        }
        return null
    }

    private fun resolveSchema(annotatedType: AnnotatedType): Schema<*>? {
        val current = CurrentOpenAPIComponentContext.current ?: return null
        val schemaType = annotatedType.type
        var schema: Schema<*>? = null
        when (schemaType) {
            is Class<*> -> {
                annotatedType.resolveName(schemaType)
                schema = current.schema(schemaType)
            }

            is JavaType -> {
                val resolvedType = schemaType.toResolvedType()
                annotatedType.resolveName(resolvedType)
                schema = current.schema(resolvedType)
            }
        }
        if (schema != null) {
            schema.name = annotatedType.name
        }
        return schema
    }
}
