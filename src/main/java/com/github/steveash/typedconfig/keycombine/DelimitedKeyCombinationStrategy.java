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
import org.apache.commons.lang.StringUtils;

/**
 * @author Steve Ash
 */
public abstract class DelimitedKeyCombinationStrategy implements KeyCombinationStrategy {

    @Override
    public String combineKey(String baseKey, String localKey, HierarchicalConfiguration config) {
        String delim = getDelimiter();
        if (StringUtils.isBlank(baseKey))
            return localKey;
        
        if (baseKey.endsWith(delim) && localKey.startsWith(delim))
            return baseKey + localKey.substring(1);
        
        if (baseKey.endsWith(delim) || localKey.startsWith(delim))
            return baseKey + localKey;
        
        return baseKey + delim + localKey;
    }

    protected abstract String getDelimiter();
}
