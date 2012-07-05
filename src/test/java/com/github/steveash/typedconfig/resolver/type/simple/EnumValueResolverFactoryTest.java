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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.google.common.reflect.TypeToken;

/**
 * @author Steve Ash
 */
public class EnumValueResolverFactoryTest {

    private HierarchicalConfiguration mock;
    private ConfigBinding binding;

    enum Color { RED, BLUE }

    @Before
    public void setUp() throws Exception {
        mock = mock(HierarchicalConfiguration.class);
        binding = ConfigBinding.makeForKeyAndType("a", TypeToken.of(Color.class));
    }

    @Test
    public void shouldReturnEnumValue() throws Exception {
        when(mock.getString("a", null)).thenReturn("RED", "BLUE");

        ValueResolver resolver = new EnumValueResolverFactory().makeForThis(binding, mock, null);
        assertEquals(Color.RED, resolver.resolve());
        assertEquals(Color.BLUE, resolver.resolve());
    }

    @Test
    public void shouldReturnNullIfConfigValueMissing() throws Exception {
        when(mock.getString("a", null)).thenReturn(null);

        ValueResolver resolver = new EnumValueResolverFactory().makeForThis(binding, mock, null);
        assertEquals(null, resolver.resolve());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowIfInvalidEnumValue() throws Exception {
        when(mock.getString("a", null)).thenReturn("THISDOESNTEXIST");

        ValueResolver resolver = new EnumValueResolverFactory().makeForThis(binding, mock, null);
        resolver.resolve();
    }
}
