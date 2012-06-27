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

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Method;

/**
 * Interface type used to offer a static way to get to the underlying things that make up
 * a proxied type.  This really isn't intended to be used by a consumer of this, but allows
 * us to do things like implement equals (to pull things from the argument)
 */
public interface ProxiedConfiguration {
    Class<?> getInterfaceClass();
    ImmutableMap<Method, ValueResolver> getResolvers();
}
