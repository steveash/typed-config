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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.annotation.Config;

/**
 * @author Steve Ash
 */
public class LookupValueResolverTest {

    private HierarchicalConfiguration config;
    private Method method;
    private ValueResolverForBindingFactory factory;
    private ConfigBinding nullBinding;
    private ConfigBinding oneBinding;

    static interface TestIface {
        @Config(options = Option.LOOKUP_RESULT)
        String getSomething();
    }

    @Before
    public void setUp() throws Exception {
        config = mock(HierarchicalConfiguration.class);
        when(config.getString("key.one", null)).thenReturn("key.two");
        when(config.getString("key.null", null)).thenReturn(null);
        method = TestIface.class.getDeclaredMethod("getSomething");

        factory = new ValueResolverForBindingFactory() {
            @Override
            public ValueResolver makeResolverForBinding(ConfigBinding binding, Class<?> interfaze, Method method,
                    HierarchicalConfiguration config) {
                return new InstanceValueResolver(binding.getConfigKeyToLookup());
            }
        };

        nullBinding = ConfigBinding.makeShimForKey("key.null").withOption(Option.LOOKUP_RESULT);
        oneBinding = ConfigBinding.makeShimForKey("key.one").withOption(Option.LOOKUP_RESULT);
    }

    @Test
    public void shouldReturnNullForNoKey() throws Exception {
        LookupValueResolver resolver = new LookupValueResolver(config, nullBinding, TestIface.class, method, factory);
        assertEquals(null, resolver.resolve());
    }

    @Test
    public void shouldReturnLookupKey() throws Exception {
        LookupValueResolver resolver = new LookupValueResolver(config, oneBinding, TestIface.class, method, factory);
        assertEquals("key.two", resolver.resolve());
    }

    @Test
    public void shouldReturnChangingConfigValues() throws Exception {
        when(config.getString("key.one", null)).thenReturn("first", "second");
        LookupValueResolver resolver = new LookupValueResolver(config, oneBinding, TestIface.class, method, factory);
        assertEquals("first", resolver.resolve());
        assertEquals("second", resolver.resolve());
    }

    @Test
    public void shouldReturnNullIfConfigIsRemoved() throws Exception {
        when(config.getString("key.one", null)).thenReturn("first", null, "second");
        LookupValueResolver resolver = new LookupValueResolver(config, oneBinding, TestIface.class, method, factory);
        assertEquals("first", resolver.resolve());
        assertEquals(null, resolver.resolve());
        assertEquals("second", resolver.resolve());
    }
}
