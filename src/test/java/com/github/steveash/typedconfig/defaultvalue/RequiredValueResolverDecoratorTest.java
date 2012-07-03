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

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;
import com.github.steveash.typedconfig.exception.RequiredConfigurationKeyNotPresentException;
import com.github.steveash.typedconfig.resolver.ValueResolver;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steve Ash
 */
public class RequiredValueResolverDecoratorTest {
    @Test
    public void shouldReturnNonNullResponse() throws Exception {
        ValueResolver resolver = mock(ValueResolver.class);
        when(resolver.resolve()).thenReturn(123);

        assertEquals(123, new RequiredValueResolverDecorator(resolver, mock(HierarchicalConfiguration.class)).resolve());
    }

    @Test(expected = RequiredConfigurationKeyNotPresentException.class)
    public void shouldThrowWhenNullResponse() throws Exception {
        ValueResolver resolver = mock(ValueResolver.class);
        new RequiredValueResolverDecorator(resolver, mock(HierarchicalConfiguration.class)).resolve();
        fail();
    }
}
