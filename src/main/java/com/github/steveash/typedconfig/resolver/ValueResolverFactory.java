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

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;

/**
 * @author Steve Ash
 */
public interface ValueResolverFactory {

    /**
     * Returns a threadsafe resolver instance that knows how to resolve for this given info
     *
     *
     * @param binding
     * @param config the configuration instance to use
     * @param context
     * @return
     */
    ValueResolver makeForThis(ConfigBinding binding, HierarchicalConfiguration config,
                                 ConfigFactoryContext context);

    /**
     *
     * @param configBinding
     * @return true if this factory is able to construct valueResolvers for this particular proxy method
     */
    boolean canResolveFor(ConfigBinding configBinding);

    /**
     * @return type of value that this value works with.  In general there are simple values and complex values.
     * The latter being hierarchical, nested in which case the complex class handles itself
     */
    ValueType getValueType();

}
