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

import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class ProxyValueResolverTest {

    private ConfigProxyFactory cpf;
    private HierarchicalConfiguration mock;

    static interface Child {
        int getSome1();

        int getSome2();
    }

    @Before
    public void setUp() throws Exception {
        mock = mock(HierarchicalConfiguration.class);
        cpf = ConfigProxyFactory.getDefault();
    }

    @Test
    public void shouldReturnConfigValue() throws Exception {
        when(mock.getInteger("some1", null)).thenReturn(42);
        when(mock.getInteger("some2", null)).thenReturn(23);

        Child c1 = cpf.make(Child.class, mock);
        assertEquals(42, c1.getSome1());
        assertEquals(23, c1.getSome2());
    }

    @Test
    public void shouldEqualEachOther() throws Exception {
        when(mock.getInteger("some1", null)).thenReturn(42);
        when(mock.getInteger("some2", null)).thenReturn(23);

        Child c1 = cpf.make(Child.class, mock);
        Child c2 = cpf.make(Child.class, mock);

        assertTrue(c1.equals(c2));
        assertEquals(c1.hashCode(), c2.hashCode());
    }

    @Test
    public void shouldNotEqualEachOther() throws Exception {
        when(mock.getInteger("some1", null)).thenReturn(42);
        when(mock.getInteger("some2", null)).thenReturn(23);

        HierarchicalConfiguration mock2 = mock(HierarchicalConfiguration.class);
        when(mock2.getInteger("some1", null)).thenReturn(420);
        when(mock2.getInteger("some2", null)).thenReturn(230);

        Child c1 = cpf.make(Child.class, mock);
        Child c2 = cpf.make(Child.class, mock2);

        assertFalse(c1.equals(c2));
        assertFalse(c1.hashCode() == c2.hashCode());
    }

    @Test
    public void shouldPrintToString() throws Exception {
        when(mock.getInteger("some1", null)).thenReturn(42);
        when(mock.getInteger("some2", null)).thenReturn(23);
        Child c1 = cpf.make(Child.class, mock);
        assertEquals("Child[getSome1=42,getSome2=23]", c1.toString());
    }
}
