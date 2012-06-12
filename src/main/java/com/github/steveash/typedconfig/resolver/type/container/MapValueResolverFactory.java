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
import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import org.apache.commons.beanutils.BeanUtils;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.annotation.MapKey;
import com.github.steveash.typedconfig.exception.InvalidProxyException;
import com.github.steveash.typedconfig.exception.RequiredConfigurationKeyNotPresentException;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * @author Steve Ash
 */
public class MapValueResolverFactory extends AbstractContainerValueResolverFactory {

    @Override
    protected Collection<Object> makeEmptyCollection(int size) {
        return Lists.newArrayListWithCapacity(size);
    }

    // maps aren't iterable so we need another resolution to get to the right generic type
    @Override
    protected TypeToken<?> getContainedType(TypeToken<?> mapType) {
        try {
            return getEntryType(mapType)
                    .resolveType(Entry.class.getDeclaredMethod("getValue").getGenericReturnType());

        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    protected TypeToken<?> getKeyType(TypeToken<?> mapType) {
        try {
            return getEntryType(mapType)
                    .resolveType(Entry.class.getDeclaredMethod("getKey").getGenericReturnType());

        } catch (NoSuchMethodException e) {
            throw Throwables.propagate(e);
        }
    }

    private TypeToken<?> getEntryType(TypeToken<?> mapType) throws NoSuchMethodException {
        return mapType
                .resolveType(Map.class.getDeclaredMethod("entrySet").getGenericReturnType())
                .resolveType(Set.class.getDeclaredMethod("iterator").getGenericReturnType())
                .resolveType(Iterator.class.getDeclaredMethod("next").getGenericReturnType());
    }

    @Override
    protected Object makeReturnValueFrom(Collection<Object> containedValues, ConfigBinding binding) {
        MapKey mapKeyAnnotation = findMapKeyAnnotation(binding);
        TypeToken<?> keyType = getKeyType(binding.getDataType());
        Builder<Object, Object> builder = ImmutableMap.builder();
        for (Object value : containedValues) {
            Object keyValue = getKeyValueOrThrow(mapKeyAnnotation.value(), value, binding);
            throwIfInvalidValidType(keyType, keyValue, binding);
            builder.put(keyValue, value);
        }

        Map<Object, Object> map = builder.build();
        if (mapKeyAnnotation.required())
            map = new RequiredValueMap<Object, Object>(map, binding);

        return map;
    }

    private void throwIfInvalidValidType(TypeToken<?> keyType, Object keyValue, ConfigBinding binding) {
        if (keyType.getRawType().isAssignableFrom(keyType.getRawType()))
            return;
        throw new InvalidProxyException("Trying to create the map for config key " + binding.getConfigKeyToLookup() +
                " but the @MapKey refers to child property with a value " + keyValue + " which is not assignable to " +
                "the key type which is " + keyType);
    }

    private Object getKeyValueOrThrow(String property, Object bean, ConfigBinding binding) {
        try {
            return BeanUtils.getProperty(bean, property);
        } catch (Exception e) {
            throw new InvalidProxyException("Trying to create the map for config key " + binding.getConfigKeyToLookup()
                    + " but the @MapKey refers to child property " + property + " which does not exist on type " +
                    bean.getClass() + " or invalid in some other way.", e);
        }
    }

    private MapKey findMapKeyAnnotation(ConfigBinding binding) {
        for (Annotation annotation : binding.getAnnotations()) {
            if (annotation.annotationType().equals(MapKey.class)) {
                return (MapKey) annotation;
            }
        }
        throw new InvalidProxyException("If you use a Map type in a proxy you must use the @MapKey annotation " +
                " to indicate what child property should be the key.  Can't create map for config key: " +
                binding.getConfigKeyToLookup() + " for map type " + binding.getDataType());
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        // since we wrap the map, and the immutable map can't be delegated to I have to return map
        return configBinding.getDataType().getRawType().isAssignableFrom(Map.class);
    }

    private static final class RequiredValueMap<K, V> extends ForwardingMap<K, V> {

        private final Map<K, V> delegate;
        private final ConfigBinding binding;

        private RequiredValueMap(Map<K, V> delegate, ConfigBinding binding) {
            this.delegate = delegate;
            this.binding = binding;
        }

        @Override
        protected Map<K, V> delegate() {
            return delegate;
        }

        @Override
        public V get(Object key) {
            V value = super.get(key);
            if (value == null) {
                throw new RequiredConfigurationKeyNotPresentException("Trying to read the map value at configuration " +
                        "location " + binding.getConfigKeyToLookup() + " for key " + key + " but there is no config " +
                        "value for that map key and @MapKey is marked as required");
            }
            return value;
        }
    }
}
