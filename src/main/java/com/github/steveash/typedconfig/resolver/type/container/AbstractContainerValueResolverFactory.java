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

package com.github.steveash.typedconfig.resolver.type.container;

import com.google.common.base.Throwables;
import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.exception.InvalidProxyException;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueType;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * The base factory which knows how to handle container types.  Implementors just need to be able to produce
 * the type of collection that they want to use and then are also given an opportunity
 *
 * @author Steve Ash
 */
public abstract class AbstractContainerValueResolverFactory implements ValueResolverFactory {
    @Override
    public ValueResolver makeForThis(final ConfigBinding containerBinding, final HierarchicalConfiguration config,
                                     final ConfigFactoryContext context) {
        return new ValueResolver() {
            @Override
            public Object resolve() {
                TypeToken<?> thisType = getContainedType(containerBinding.getDataType());
                ConfigBinding childBinding = containerBinding
                        .withKey("")
                        .withDataType(thisType)
                        .withOptions(Option.EmptyOptions);

                ValueResolverFactory childFactory = context.getRegistry().lookup(childBinding);

                switch (childFactory.getValueType()) {
                    case Simple:
                        return makeForSimpleType(childBinding, childFactory);
                    case Nested:
                        return makeForNestedType(childBinding, childFactory);
                    default:
                        throw new InvalidProxyException("The proxy method returning " + containerBinding.getDataType() +
                                " for configuration key " + containerBinding.getConfigKeyToLookup() + " uses a container type " +
                                "which returns " + thisType + " which is also a container type.  You can't have " +
                                "containers of container types.");
                }
            }

            private Object makeForNestedType(ConfigBinding childBinding, ValueResolverFactory childFactory) {
                List<HierarchicalConfiguration> childConfigs =
                        config.configurationsAt(containerBinding.getConfigKeyToLookup());
                Collection<Object> values = makeEmptyCollection(childConfigs.size());
                for (HierarchicalConfiguration childConfig : childConfigs) {
                    SubnodeConfiguration childConfigAsSub = (SubnodeConfiguration) childConfig;
                    ConfigBinding subBinding = childBinding.withKey(childConfigAsSub.getSubnodeKey());
                    ValueResolver r = childFactory.makeForThis(subBinding, childConfig, context);
                    values.add(r.resolve());
                }
                return makeReturnValueFrom(values, containerBinding);
            }

            private Object makeForSimpleType(ConfigBinding childBinding, ValueResolverFactory childFactory) {
                ValueResolver childResolver = childFactory.makeForThis(childBinding, config, context);
                List<Object> configValues = config.getList(containerBinding.getConfigKeyToLookup());

                Collection<Object> containedValues = makeEmptyCollection(configValues.size());
                for (Object o : configValues) {
                    if (!(o instanceof String))
                        throw new IllegalArgumentException("Can only use Configuration instances which return string " +
                                "representations of the values which we will then convert. XMLConfiguration does this");

                    containedValues.add(childResolver.convertDefaultValue((String) o));
                }
                return makeReturnValueFrom(containedValues, containerBinding);
            }


            @Override
            public Object convertDefaultValue(String defaultValue) {
                throw new IllegalStateException("cannot specify a defaults for container types");
            }

            @Override
            public String configurationKeyToLookup() {
                return containerBinding.getConfigKeyToLookup();
            }
        };
    }

    /**
     * Implementors must be able to provide a (possibly temporary) collection that the resolver will use to
     * collect values.  This collection will then be passed to #makeReturnValueFrom which can then transform it
     * however the implementor sees fit.  Note that the default implementation of makeReturnValueFrom just is
     * the identity function so if you create the final type here then that's all you need to do
     *
     * @param size
     * @return
     */
    protected abstract Collection<Object> makeEmptyCollection(int size);

    /**
     * If the type returned from #makeEmptyCollection is not the final return type, then implementors can transform
     * the values here and return whatever type they like (for example, immutableList might make a copy)
     *
     *
     * @param containedValues
     * @param binding
     * @return
     */
    protected Object makeReturnValueFrom(Collection<Object> containedValues, ConfigBinding binding) {
        return containedValues;
    }

    protected TypeToken<?> getContainedType(TypeToken<?> returnType) {
        try {
            TypeToken<?> iteratorType = returnType.resolveType(
                    Iterable.class.getMethod("iterator").getGenericReturnType());
            return iteratorType.resolveType(
                    Iterator.class.getMethod("next").getGenericReturnType());
        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    @Override
    public ValueType getValueType() {
        return ValueType.Container;
    }
}
