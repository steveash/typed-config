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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.resolver.type.*;
import com.github.steveash.typedconfig.resolver.type.container.ListValueResolverFactory;
import com.github.steveash.typedconfig.resolver.type.container.MapValueResolverFactory;
import com.github.steveash.typedconfig.resolver.type.container.SetValueResolverFactory;
import com.github.steveash.typedconfig.resolver.type.container.SortedSetValueResolverFactory;
import com.github.steveash.typedconfig.resolver.type.simple.*;

/**
 * Provides registry services for ValueResolverFactories to lookup the appropriate factory for the given method handle
 *
 * @author Steve Ash
 */
public class ValueResolverRegistry {


    private static final ImmutableList<ValueResolverFactory> builtInTypes =
            ImmutableList.<ValueResolverFactory>builder()
                    // simple types
                    .add(new BooleanValueResolverFactory())
                    .add(new StringValueResolverFactory())
                    .add(new IntegerValueResolverFactory())
                    .add(new LongValueResolverFactory())
                    .add(new ByteValueResolverFactory())
                    .add(new ShortValueResolverFactory())
                    .add(new FloatValueResolverFactory())
                    .add(new DoubleValueResolverFactory())
                    .add(new BigIntegerValueResolverFactory())
                    .add(new BigDecimalValueResolverFactory())
                    .add(new ConfigurationValueResolverFactory())
                            // collections
                    .add(new ListValueResolverFactory())
                    .add(new SetValueResolverFactory())
                    .add(new SortedSetValueResolverFactory())
                    .add(new MapValueResolverFactory())
                            // catch all proxy resolver
                    .add(new ProxyValueResolverFactory())
                    .build();

    /**
     * @return the default registry that knows about all of the types that are supported out of the box
     */
    public static ValueResolverRegistry makeDefaultRegistry() {
        return new ValueResolverRegistry(builtInTypes);
    }

    public static ValueResolverRegistry makeRegistryWithUserTypes(Iterable<? extends ValueResolverFactory> userTypes) {
        return new ValueResolverRegistry(Iterables.concat(userTypes, builtInTypes));
    }

    public ValueResolverRegistry() {
        this(ImmutableList.<ValueResolverFactory>of());
    }

    public ValueResolverRegistry(Iterable<? extends ValueResolverFactory> factories) {
        this.factories = ImmutableList.copyOf(factories);
    }

    private final Iterable<ValueResolverFactory> factories;

    public ValueResolverFactory lookup(ConfigBinding binding) {
        for (ValueResolverFactory factory : factories) {
            if (factory.canResolveFor(binding)) {
                return factory;
            }
        }
        throw new IllegalArgumentException("No ValueResolverFactory is registered that can resolve configuration " +
                "for the " + binding.getConfigKeyToLookup() + ", type " + binding.getDataType());
    }
}
