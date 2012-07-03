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

import java.lang.reflect.Method;

import javax.annotation.Nonnull;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.lang3.StringUtils;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.Option;

/**
 * Implements the "lookup" behavior: with this behavior the value in the config is not the "real" value -- its a
 * "lookup key" which will be subsequently looked up in the config and that resulting "real" value will be returned
* @author Steve Ash
*/
public class LookupValueResolver implements ValueResolver {
    private volatile String lastLookupKey = null;
    private volatile ValueResolver lastResolver = null;

    private final HierarchicalConfiguration config;
    private final ConfigBinding originalBinding;
    private final Class<?> interfaze;
    private final Method method;
    private final ValueResolverForBindingFactory resolverFactory;

    public LookupValueResolver(HierarchicalConfiguration config, ConfigBinding originalBinding, Class<?> interfaze,
            Method method, ValueResolverForBindingFactory resolverFactory) {

        if (!originalBinding.containsOption(Option.LOOKUP_RESULT)) {
            throw new IllegalArgumentException("lookup resolver is only for lookup bindings");
        }

        this.resolverFactory = resolverFactory;
        this.config = config;
        this.originalBinding = originalBinding.withoutOption(Option.LOOKUP_RESULT);
        this.interfaze = interfaze;
        this.method = method;
    }

    @Override
    public Object resolve() {
        String currentLookupKey = config.getString(originalBinding.getConfigKeyToLookup(), null);
        if (StringUtils.isBlank(currentLookupKey)) {
            return returnNoLookup();
        }
        if (makeNewResolver(currentLookupKey)) {

            ConfigBinding newBinding = originalBinding.withKey(currentLookupKey);
            lastLookupKey = currentLookupKey;
            lastResolver = resolverFactory.makeResolverForBinding(newBinding, interfaze, method, config);
        }
        return lastResolver.resolve();
    }

    private Object returnNoLookup() {
        lastLookupKey = null;
        lastResolver = null;
        return null;
    }

    private boolean makeNewResolver(@Nonnull String currentLookupKey) {
        return (lastResolver == null || lastLookupKey == null || !lastLookupKey.equals(currentLookupKey));
    }

    @Override
    public Object convertDefaultValue(String defaultValue) {
        if (lastResolver == null) throw new IllegalStateException("can't call this until after resolve");
        return lastResolver.convertDefaultValue(defaultValue);
    }

    @Override
    public String configurationKeyToLookup() {
        if (lastResolver == null) throw new IllegalStateException("can't call this until after resolve");
        return lastResolver.configurationKeyToLookup();
    }
}
