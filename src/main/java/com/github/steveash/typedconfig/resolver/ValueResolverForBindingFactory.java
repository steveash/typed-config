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

import java.lang.reflect.Method;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.github.steveash.typedconfig.ConfigBinding;

/**
 * Simple abstraction for any thing that can return value resolvers for bindings; this is mainly here just as a
 * marker for the ValueProxyResolver (to reduce coupling in the lookupResolver)
 * @author Steve Ash
 */
interface ValueResolverForBindingFactory {

    ValueResolver makeResolverForBinding(ConfigBinding binding, Class<?> interfaze, Method method,
            HierarchicalConfiguration config);
}
