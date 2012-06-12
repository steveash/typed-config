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
import com.github.steveash.typedconfig.resolver.ConvertableValueResolver;
import com.github.steveash.typedconfig.resolver.SimpleValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueResolver;

/**
 * @author Steve Ash
 */
public class FloatValueResolverFactory extends SimpleValueResolverFactory {

    @Override
    public ValueResolver makeForThis(final ConfigBinding binding, final HierarchicalConfiguration config,
                                            ConfigFactoryContext context) {

        final String key = binding.getConfigKeyToLookup();
        return new ConvertableValueResolver(Float.class, key) {
            @Override
            public Float resolve() {
                return config.getFloat(binding.getConfigKeyToLookup(), null);
            }
        };
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().isAssignableFrom(Float.class) ||
                configBinding.getDataType().isAssignableFrom(Float.TYPE)
                ;
    }
}
