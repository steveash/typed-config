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

package com.github.steveash.typedconfig.resolver.type;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.MapKey;

import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class ConfigurationValueResolverFactoryIntegrationTest {
    private Proxy proxy;
    private XMLConfiguration xmlConfig;

    static interface Proxy {
        int getA();
        HierarchicalConfiguration getConfig();

        @MapKey("name")
        @Config("child")
        Map<String, Child> getChildren();
    }

    static interface Child {
        String getName();
        HierarchicalConfiguration getConfig();
    }

    @Before
    public void setUp() throws Exception {
        xmlConfig = new XMLConfiguration("configIntegration.xml");
        proxy = ConfigProxyFactory.getDefault().make(Proxy.class, xmlConfig);
    }

    @Test
    public void testConfiguration() throws Exception {
        assertEquals(42, proxy.getConfig().getInt("a"));
        assertEquals("steve", proxy.getChildren().get("steve").getConfig().getString("name"));
    }
}
