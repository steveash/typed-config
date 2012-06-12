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

package com.github.steveash.typedconfig.caching;

import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.resolver.CachingValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolver;

/**
 * This strategy always caches everything forever and doesn't even bother listening to events.  So this is
 * only useful for immutable configurations that are only setup once and never changed.
 * @author Steve Ash
 */
public class CacheEverythingForeverStategy implements CacheStrategy {

    @Override
    public ValueResolver decorateForCaching(ValueResolver resolver, ConfigBinding binding,
                                            ConfigFactoryContext context) {
        return new CachingValueResolver(resolver);
        // don't even bother listening to events because we're caching everything forever
    }
}
