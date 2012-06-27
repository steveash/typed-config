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

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * @author Steve Ash
 */
public class HierarchicalConfigurationSanityTest {

    private XMLConfiguration config;

    @Before
    public void setUp() throws Exception {
        config = new XMLConfiguration("nestedConfig1.xml");
    }

    @Test
    public void testNestedFirst() throws Exception {
        assertEquals(42, config.getInt("atParent"));
        SubnodeConfiguration nested = config.configurationAt("nested(0)");
        assertEquals("nested1", nested.getString("a"));
        assertEquals(42, nested.getParent().getInt("atParent"));
    }

    @Test
    public void testDoubleNested() throws Exception {
        assertEquals(42, config.getInt("atParent"));
        SubnodeConfiguration nested = config.configurationAt("doubleNested.nested");
        assertEquals("nested3", nested.getString("a"));
        assertEquals(42, nested.getParent().getInt("atParent"));
    }

    @Test
    public void testSubnodeLists() throws Exception {
        assertEquals(42, config.getInt("atParent"));
        List<HierarchicalConfiguration> nodes = config.configurationsAt("nested");

        assertEquals("nested1", nodes.get(0).getString("a"));
        assertEquals("nested2", nodes.get(1).getString("a"));

        HierarchicalConfiguration nestedConfig = nodes.get(0);
        assertTrue(nestedConfig instanceof SubnodeConfiguration);
        SubnodeConfiguration subConfig = (SubnodeConfiguration) nestedConfig;
        assertEquals(42, subConfig.getParent().getInt("atParent"));
    }

}
