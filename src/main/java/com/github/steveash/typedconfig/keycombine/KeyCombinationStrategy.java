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

/**
 * Strategy for how to combine the basekey specified in @ConfigProxy annotations with the @Config values and
 * defaultLookup values.  Note that this is really dependent on the particular expression engine.  The default
 * expression engine wants to combine things with dots (basekey.localkey) wheres the xpath engine probably
 * wants slashes (basekey/localkey)
 * @author Steve Ash
 */
public interface KeyCombinationStrategy {

    String combineKey(String baseKey, String localKey, HierarchicalConfiguration config);
}
