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

/**
 * The value type controls how types are nested within one another
 * @author Steve Ash
 */
public enum ValueType {
    /**
     * containers of simple types dont get a new nested configuration context -- they are just simple values
     * that are represented by a single leaf node in the configuration graph (String, Double, etc.) are simple
     * types.
     */
    Simple,

    /**
     * Nested types do get a new nested configuration context -- they are _single_ non-leaf nodes in the
     * configuration graph.  A proxy that has a property which returns another proxy is an example-- the
     * child proxy in that case is nested, because it gets another configuration context
     */
    Nested,

    /**
     * Container types are special.  They represent a collection of other types.  The collecion can be either
     * Simple or Nested.
     */
    Container
}
