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

package com.github.steveash.typedconfig.resolver.type;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueType;

/**
 * @author Steve Ash
 */
public class ConfigurationValueResolverFactory implements ValueResolverFactory {
    @Override
    public ValueResolver makeForThis(final ConfigBinding binding, final HierarchicalConfiguration config,
                                                    ConfigFactoryContext context) {
        return new ValueResolver() {
            @Override
            public Configuration resolve() {
                return config;
            }

            @Override
            public Configuration convertDefaultValue(String defaultValue) {
                throw new IllegalStateException("should never happen as cant default the configuration node. cant " +
                        "use configuration inside of a container type");
            }

            @Override
            public String configurationKeyToLookup() {
                return binding.getConfigKeyToLookup();
            }
        };
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().isAssignableFrom(HierarchicalConfiguration.class);
    }

    @Override
    public ValueType getValueType() {
        return ValueType.Simple;
    }
}
