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

package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Steve Ash
 */
public class NestedConfig3IntegrationTest {

    private Proxy proxy;

    static interface Proxy {
        int getA();
        NestedProxy getNestedType();

        @Config("child")
        List<Child> getChildren();
    }

    static interface NestedProxy {
        int getB();
        NestedNestedProxy getNestedNestedType();
    }

    static interface NestedNestedProxy {
        int getC();
    }

    static interface Address {
        String getCity();
        String getState();
    }

    static interface Child {
        String getName();
        Address getAddress();
    }

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault().make(Proxy.class, new XMLConfiguration("nestedConfig3.xml"));
    }

    @Test
    public void testNestedTypes() throws Exception {
        assertEquals(42, proxy.getA());
        assertNotNull(proxy.getNestedType());
        assertEquals(123, proxy.getNestedType().getB());
        assertNotNull(proxy.getNestedType().getNestedNestedType());
        assertEquals(456, proxy.getNestedType().getNestedNestedType().getC());
    }

    @Test
    public void testContainersOfNestedTypes() throws Exception {
        assertEquals(2, proxy.getChildren().size());
        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("bob", proxy.getChildren().get(1).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
        assertEquals("Dallas", proxy.getChildren().get(1).getAddress().getCity());
        assertEquals("TN", proxy.getChildren().get(0).getAddress().getState());
        assertEquals("TX", proxy.getChildren().get(1).getAddress().getState());
    }
}
