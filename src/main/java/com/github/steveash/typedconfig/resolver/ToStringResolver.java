/*
 * Copyright (c) 2012 Jonathan Tyers, Steve Ash
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.steveash.typedconfig.resolver;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;
import java.util.Map.Entry;

/**
 * @author Steve Ash
 */
public class ToStringResolver implements ValueResolver {
    private final Class<?> interfaze;
    private final ImmutableMap<Method, ValueResolver> resolverMap;

    public ToStringResolver(Class<?> interfaze, ImmutableMap<Method, ValueResolver> resolverMap) {
        this.interfaze = interfaze;
        this.resolverMap = resolverMap;
    }

    @Override
    public Object resolve() {
        StringBuilder sb = new StringBuilder();
        sb.append(interfaze.getSimpleName());
        sb.append('[');
        for (Entry<Method, ValueResolver> entry : resolverMap.entrySet()) {
            sb.append(entry.getKey().getName()).append("=");
            Object result = entry.getValue().resolve();
            sb.append(result == null ? "null" : result.toString());
            sb.append(',');
        }
        sb.setCharAt(sb.length() - 1, ']');
        return sb.toString();
    }

    @Override
    public Object convertDefaultValue(String defaultValue) {
        throw new IllegalStateException();
    }

    @Override
    public String configurationKeyToLookup() {
        throw new IllegalStateException();
    }
}
