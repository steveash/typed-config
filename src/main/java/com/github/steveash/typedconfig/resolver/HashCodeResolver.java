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

import com.google.common.collect.ImmutableMap;
import com.google.common.hash.HashCode;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.lang.reflect.Method;

/**
 * @author Steve Ash
 */
public class HashCodeResolver implements ValueResolver {

    private final Class<?> interfaze;
    private final ImmutableMap<Method, ValueResolver> proxyMethodResolvers;

    public HashCodeResolver(Class<?> interfaze, ImmutableMap<Method, ValueResolver> resolvers) {
        this.interfaze = interfaze;
        this.proxyMethodResolvers = resolvers;
    }

    @Override
    public Object resolve() {
        HashCodeBuilder builder = new HashCodeBuilder(2711, 2789);
        for (ValueResolver resolver : proxyMethodResolvers.values()) {
            builder.append(resolver.resolve());
        }
        builder.append(interfaze.getCanonicalName());
        return builder.build();
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
