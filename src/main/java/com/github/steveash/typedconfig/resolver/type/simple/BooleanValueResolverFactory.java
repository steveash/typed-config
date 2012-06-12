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

package com.github.steveash.typedconfig.resolver.type.simple;

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.resolver.ConvertableValueResolver;
import com.github.steveash.typedconfig.resolver.SimpleValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueResolver;

/**
 * @author Steve Ash
 */
public class BooleanValueResolverFactory extends SimpleValueResolverFactory {

    @Override
    public ValueResolver makeForThis(final ConfigBinding binding, final HierarchicalConfiguration config,
                                              ConfigFactoryContext context) {
        if (binding.getOptions().contains(Option.CHECK_KEY_EXISTS)) {
            return makeJustCheckKeyExistsResolver(binding.getConfigKeyToLookup(), config);
        }
        return makeNormalBooleanResolver(binding.getConfigKeyToLookup(), config);
    }

    private ValueResolver makeJustCheckKeyExistsResolver(final String configKeyToLookup,
                                                                  final HierarchicalConfiguration config) {
        return new ValueResolver() {
            @Override
            public Boolean resolve() {
                if (config.containsKey(configKeyToLookup))
                    return true;

                // it may be a subconfiguration
                try {
                    config.configurationAt(configKeyToLookup);
                    return true; // throws exception otherwise
                } catch (RuntimeException e) {
                    return false;
                }
            }

            @Override
            public Boolean convertDefaultValue(String defaultValue) {
                throw new IllegalStateException("Cant use a default value with the 'check exists' option");
            }

            @Override
            public String configurationKeyToLookup() {
                return configKeyToLookup;
            }
        };
    }

    private ConvertableValueResolver makeNormalBooleanResolver(final String key,
                                                                        final HierarchicalConfiguration config) {
        return new ConvertableValueResolver(Boolean.class, key) {
            @Override
            public Boolean resolve() {
                return config.getBoolean(key, null);
            }
        };
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().isAssignableFrom(Boolean.class) ||
                configBinding.getDataType().isAssignableFrom(Boolean.TYPE)
                ;
    }
}
