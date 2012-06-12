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

import com.google.common.collect.ImmutableSortedSet;
import com.github.steveash.typedconfig.ConfigBinding;

import java.util.Collection;
import java.util.TreeSet;

/**
 * @author Steve Ash
 */
public class SortedSetValueResolverFactory extends AbstractContainerValueResolverFactory {
    @Override
    protected Collection<Object> makeEmptyCollection(int size) {
        return new TreeSet<Object>();
    }

    @Override
    protected Object makeReturnValueFrom(Collection<Object> containedValues, ConfigBinding binding) {
        return ImmutableSortedSet.copyOf(containedValues);
    }

    @Override
    public boolean canResolveFor(ConfigBinding configBinding) {
        return configBinding.getDataType().getRawType().isAssignableFrom(ImmutableSortedSet.class);
    }
}
