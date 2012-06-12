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

package com.github.steveash.typedconfig.keycombine;

import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.tree.DefaultExpressionEngine;
import org.apache.commons.configuration.tree.ExpressionEngine;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steve Ash
 */
public class SmartDelimitedKeyCombinationStrategyTest {

    private SmartDelimitedKeyCombinationStrategy strategy = new SmartDelimitedKeyCombinationStrategy();

    @Test
    public void shouldCombineForDefaultExpressionEngineDelimiter() throws Exception {
        DefaultExpressionEngine engine = mock(DefaultExpressionEngine.class);
        when(engine.getPropertyDelimiter()).thenReturn("*");
        HierarchicalConfiguration config = mock(HierarchicalConfiguration.class);
        when(config.getExpressionEngine()).thenReturn(engine);

        assertEquals("base*local", strategy.combineKey("base", "local", config));
    }

    @Test
    public void shouldCombineWithSlashForXpathExpressionEngine() throws Exception {
        XPathExpressionEngine engine = mock(XPathExpressionEngine.class);
        HierarchicalConfiguration config = mock(HierarchicalConfiguration.class);
        when(config.getExpressionEngine()).thenReturn(engine);

        assertEquals("base/local", strategy.combineKey("base", "local", config));
    }

    @Test
    public void shouldDefaultToDot() throws Exception {
        ExpressionEngine someWeirdEngine = mock(ExpressionEngine.class);
        HierarchicalConfiguration config = mock(HierarchicalConfiguration.class);
        when(config.getExpressionEngine()).thenReturn(someWeirdEngine);

        assertEquals("base.local", strategy.combineKey("base", "local", config));
    }
}
