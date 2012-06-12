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
import com.google.common.io.Files;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Steve Ash
 */
public class NestedConfig3ReloadIntegrationTest {

    private Proxy proxy;
    private XMLConfiguration xmlConfig;

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
        xmlConfig = new XMLConfiguration("nestedConfig3.xml");
        proxy = ConfigProxyFactory.getDefault().make(Proxy.class, xmlConfig);
    }

    @Test
    public void shouldUpdateCachedValuesAfterUpdate() throws Exception {
        assertEquals(2, proxy.getChildren().size());
        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("bob", proxy.getChildren().get(1).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
        assertEquals("Dallas", proxy.getChildren().get(1).getAddress().getCity());
        assertEquals("TN", proxy.getChildren().get(0).getAddress().getState());
        assertEquals("TX", proxy.getChildren().get(1).getAddress().getState());

        xmlConfig.setProperty("child(1).address.city", "Nashville");

        assertEquals(2, proxy.getChildren().size());
        assertEquals("steve", proxy.getChildren().get(0).getName());
        assertEquals("bob", proxy.getChildren().get(1).getName());
        assertEquals("Memphis", proxy.getChildren().get(0).getAddress().getCity());
        assertEquals("Nashville", proxy.getChildren().get(1).getAddress().getCity());
        assertEquals("TN", proxy.getChildren().get(0).getAddress().getState());
        assertEquals("TX", proxy.getChildren().get(1).getAddress().getState());
    }

    @Test
    public void shouldUpdateReferencesToContainerSubnodes() throws Exception {

        Child firstChild = proxy.getChildren().get(0);
        Address firstAddress = firstChild.getAddress();

        assertEquals("steve", firstChild.getName());
        assertEquals("Memphis", firstAddress.getCity());

        xmlConfig.setProperty("child(0).name", "bubba");
        xmlConfig.setProperty("child(0).address.city", "Nashville");

        assertEquals("bubba", firstChild.getName());
        assertEquals("Nashville", firstAddress.getCity());
    }

    @Test
    public void shouldUpdateNestedTypeReferences() throws Exception {
        NestedProxy nestedType = proxy.getNestedType();
        NestedNestedProxy nestedNestedProxy = nestedType.getNestedNestedType();
        assertEquals(42, proxy.getA());
        assertEquals(123, nestedType.getB());
        assertEquals(456, nestedNestedProxy.getC());

        xmlConfig.setProperty("a", 43);
        xmlConfig.setProperty("nestedType.b", 987);
        xmlConfig.setProperty("nestedType.nestedNestedType.c", 654);

        assertEquals(43, proxy.getA());
        assertEquals(987, nestedType.getB());
        assertEquals(654, nestedNestedProxy.getC());
    }
}
