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

/**
 * A "smart" strategy that inspects the expression engine and uses its combination delimiter if its the
 * default engine or uses a slash if its the XPath engine.  If its some custom expression engine then it
 * just defaults to dot
 * @author Steve Ash
 */
public class SmartDelimitedKeyCombinationStrategy implements KeyCombinationStrategy {

    private final DotDelimitedKeyCombinationStrategy dotCombine = new DotDelimitedKeyCombinationStrategy();
    private final SlashDelimitedKeyCombinationStrategy slashCombine = new SlashDelimitedKeyCombinationStrategy();

    @Override
    public String combineKey(String baseKey, String localKey, HierarchicalConfiguration config) {

        ExpressionEngine xengine = config.getExpressionEngine();
        if (xengine instanceof DefaultExpressionEngine) {
            DefaultExpressionEngine defaultEngine = (DefaultExpressionEngine) xengine;
            String delim = defaultEngine.getPropertyDelimiter();
            return getStrategyForDelim(delim).combineKey(baseKey, localKey, config);
        }

        if (xengine instanceof XPathExpressionEngine)
            return slashCombine.combineKey(baseKey, localKey, config);

        // default to dot
        return dotCombine.combineKey(baseKey, localKey, config);
    }

    private KeyCombinationStrategy getStrategyForDelim(final String delim) {
        if (delim.equals("."))
            return dotCombine;
        if (delim.equals("/"))
            return slashCombine;

        return new DelimitedKeyCombinationStrategy() {
            @Override
            protected String getDelimiter() {
                return delim;
            }
        };
    }
}
