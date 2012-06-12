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

import com.google.common.eventbus.Subscribe;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Steve Ash
 */
public class CachingValueResolver extends ForwardingValueResolver {
    private static final Logger log = LoggerFactory.getLogger(CachingValueResolver.class);

    private Object cachedValue = null; // visibility will piggy-back on volatile flag
    private volatile boolean isValueInitialized = false; // need separate flag as null is valid value

    public CachingValueResolver(ValueResolver delegate) {
        super(delegate);
    }

    @Override
    public Object resolve() {
        if (isValueInitialized) // volatile read first
            return cachedValue;

        cachedValue = super.resolve();
        isValueInitialized = true; // volatile write last

        return cachedValue;
    }

    @Subscribe
    public void configurationChanged(ConfigurationEvent event) {
        isValueInitialized = false;
    }
}
