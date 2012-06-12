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
import com.google.common.collect.ImmutableMap.Builder;
import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Resolves a proxy for a given interface type
 *
 * @author Steve Ash
 */
public class ProxyValueResolver implements ValueResolver {

    private final ConfigBinding parentBinding;
    private final HierarchicalConfiguration config;
    private final ConfigFactoryContext context;

    public ProxyValueResolver(ConfigBinding binding, HierarchicalConfiguration config, ConfigFactoryContext context) {
        this.parentBinding = binding;
        this.config = config;
        this.context = context;
    }

    @Override
    public Object resolve() {
        return make(parentBinding.getDataType().getRawType(), config);
    }

    @Override
    public Object convertDefaultValue(String defaultValue) {
        throw new IllegalStateException("cannot use a default value on a proxy interface");
    }

    @Override
    public String configurationKeyToLookup() {
        return parentBinding.getConfigKeyToLookup();
    }

    public <T> T make(Class<T> interfaze, HierarchicalConfiguration configuration) {
        Builder<Method, ValueResolver> builder = ImmutableMap.builder();
        for (Method method : interfaze.getDeclaredMethods()) {
            builder.put(method, makeResolverFor(interfaze, method, configuration));
        }
        return makeProxyForResolvers(interfaze, builder.build());
    }

    private ValueResolver makeResolverFor(Class<?> interfaze, Method method, HierarchicalConfiguration config) {

        ConfigBinding newMethodBinding = context.getBindingFor(interfaze, method, config);
        ValueResolver resolver = context.makeResolverForBinding(config, newMethodBinding, parentBinding);

        resolver = context.getDefaultStrategy().decorateForDefaults(
                resolver, config, newMethodBinding, context, interfaze, method);
        resolver = context.getValidationStrategy().decorateForValidation(resolver, interfaze, method);
        resolver = context.getCacheStrategy().decorateForCaching(resolver, newMethodBinding, context);

        return resolver;
    }

    @SuppressWarnings("unchecked")
    private <T> T makeProxyForResolvers(Class<?> interfaze, final ImmutableMap<Method, ValueResolver> resolverMap) {
        InvocationHandler handler = new InvocationHandler() {

            @Override
            public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
                return resolverMap.get(method).resolve();
            }
        };

        return (T) Proxy.newProxyInstance(getClass().getClassLoader(), new Class<?>[]{interfaze}, handler);
    }
}
