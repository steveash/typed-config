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

/**
 * Smartly combines them so that a single dot separates the base key and the local key which is probably what
 * you want if you are using the default expression engine
 * @author Steve Ash
 */
public class DotDelimitedKeyCombinationStrategy extends DelimitedKeyCombinationStrategy {
    @Override
    protected String getDelimiter() {
        return ".";
    }
}
