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

package com.github.steveash.typedconfig.defaultvalue;

import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.ConfigProxyFactoryTest;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.exception.RequiredConfigurationKeyNotPresentException;
import com.github.steveash.typedconfig.resolver.ValueResolver;

import java.util.Arrays;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steve Ash
 */
public class DefaultValueStrategyTest {

    private ValueResolver good1Resolver;
    private ValueResolver good2Resolver;
    private ValueResolver badResolver;
    private ConfigValueDefaultValueStrategy strategy;
    private HierarchicalConfiguration config;
    private ConfigBinding binding;
    private ConfigFactoryContext context;
    private Config ann;

    @Before
    public void setUp() throws Exception {
        good1Resolver = mock(ValueResolver.class);
        when(good1Resolver.resolve()).thenReturn("good1");

        good2Resolver = mock(ValueResolver.class);
        when(good2Resolver.resolve()).thenReturn("good2");

        badResolver = mock(ValueResolver.class);
        when(badResolver.resolve()).thenReturn(null);
        when(badResolver.convertDefaultValue(anyString())).then(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                String arg = (String) invocation.getArguments()[0];
                if (isBlank(arg)) return null;
                return arg;
            }
        });
        when(badResolver.configurationKeyToLookup()).thenReturn("aKey");

        config = mock(HierarchicalConfiguration.class);
        binding = mock(ConfigBinding.class);
        TypeToken type = TypeToken.of(String.class);
        when(binding.getDataType()).thenReturn(type);
        when(binding.getOptions()).thenReturn(Option.EmptyOptions);
        strategy = new ConfigValueDefaultValueStrategy();
        context = ConfigProxyFactoryTest.defaultContext;
    }

    static interface Proxy {
        String noAnnotation();
        @Config(defaultValue = "heresADefault")
        String withDefaultValue();
        @Config(defaultLookup = "some.lookup")
        String withDefaultLookup();
        @Config(defaultLookup = "some.lookup.thats.missing", defaultValue = "the default default")
        String withDefaultValueAndDefaultLookup();
        @Config(defaultLookup = "some.lookup.thats.missing")
        String withDefaultLookupThatsMissing();
    }

    @Test
    public void shouldReturnGoodValueFromResolver() throws Exception {
        String result = (String) strategy.decorateForDefaults(good1Resolver, config, binding, context, Proxy.class,
                Proxy.class.getDeclaredMethod("noAnnotation")
        ).resolve();
        assertEquals("good1", result);
    }

    @Test
    public void shouldReturnDefaultValueIfMissing() throws Exception {
        String result = (String) strategy.decorateForDefaults(badResolver, config, binding, context, Proxy.class,
                Proxy.class.getDeclaredMethod("withDefaultValue")
        ).resolve();
        assertEquals("heresADefault", result);
    }

    @Test
    public void shouldDoDefaultLookupIfMissing() throws Exception {
        when(config.getString("some.lookup", null)).thenReturn("foundTheLookup");
        String result = (String) strategy.decorateForDefaults(badResolver, config, binding, context, Proxy.class,
                        Proxy.class.getDeclaredMethod("withDefaultLookup")
        ).resolve();
        assertEquals("foundTheLookup", result);
    }

    @Test
    public void shouldDoLookupAndRevertToGivenDefaultIfBothMissing() throws Exception {
        String result = (String) strategy.decorateForDefaults(badResolver, config, binding, context, Proxy.class,
                        Proxy.class.getDeclaredMethod("withDefaultValueAndDefaultLookup")
        ).resolve();
        assertEquals("the default default", result);
    }

    @Test
    public void shouldReturnNullIfNotRequired() throws Exception {
        String result = (String) strategy.decorateForDefaults(badResolver, config, binding, context, Proxy.class,
                        Proxy.class.getDeclaredMethod("withDefaultLookupThatsMissing")
        ).resolve();
        assertNull(result);
    }

    @Test(expected = RequiredConfigurationKeyNotPresentException.class)
    public void shouldThrowExceptionIfIsRequired() throws Exception {
        when(binding.getOptions()).thenReturn(Arrays.asList(Option.REQUIRED));

        String result = (String) strategy.decorateForDefaults(badResolver, config, binding, context, Proxy.class,
                        Proxy.class.getDeclaredMethod("withDefaultLookupThatsMissing")).resolve();
        fail();
    }
}
