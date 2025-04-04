/*
 * Copyright [2021-present] [ahoo wang <ahoowang@qq.com>].
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

package me.ahoo.wow.infra.accessor.method;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * <a href="https://detekt.dev/docs/rules/performance/#spreadoperator">
 * SpreadOperator
 * </a>
 */
public final class FastInvoke {
    private FastInvoke() {
    }

    /**
     * invoke target object method
     *
     * @param method method
     * @param target target
     * @param args   args
     * @param <T>    result type
     * @return result
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    @SuppressWarnings({"AvoidObjectArrays", "unchecked"})
    public static <T> T invoke(@NotNull Method method, Object target, Object[] args)
            throws InvocationTargetException, IllegalAccessException {
        return (T) method.invoke(target, args);
    }

    /**
     * safe invoke target object method
     *
     * @param method method
     * @param target target
     * @param args   args
     * @param <T>    result type
     * @return result
     * @throws InvocationTargetException InvocationTargetException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public static <T> T safeInvoke(@NotNull Method method, Object target, Object[] args)
            throws Throwable {
        try {
            return invoke(method, target, args);
        } catch (InvocationTargetException targetException) {
            throw targetException.getTargetException();
        }
    }

    /**
     * create instance
     *
     * @param constructor create instance
     * @param args        args
     * @param <T>         instance type
     * @return instance
     * @throws InvocationTargetException InvocationTargetException
     * @throws InstantiationException    InstantiationException
     * @throws IllegalAccessException    IllegalAccessException
     */
    @NotNull
    @SuppressWarnings("AvoidObjectArrays")
    public static <T> T newInstance(@NotNull Constructor<T> constructor, Object[] args)
            throws InvocationTargetException, InstantiationException,
            IllegalAccessException {
        return constructor.newInstance(args);
    }

    /**
     * safe create instance
     *
     * @param constructor create instance
     * @param args        args
     * @param <T>         instance type
     * @return instance
     * @throws InvocationTargetException InvocationTargetException
     * @throws InstantiationException    InstantiationException
     * @throws IllegalAccessException    IllegalAccessException
     */
    public static <T> T safeNewInstance(@NotNull Constructor<T> constructor, Object[] args)
            throws Throwable {
        try {
            return newInstance(constructor, args);
        } catch (InvocationTargetException targetException) {
            throw targetException.getTargetException();
        }
    }
}
