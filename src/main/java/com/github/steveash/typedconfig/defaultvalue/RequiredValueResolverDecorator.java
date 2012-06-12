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

package com.github.steveash.typedconfig.defaultvalue;

import com.github.steveash.typedconfig.exception.RequiredConfigurationKeyNotPresentException;
import com.github.steveash.typedconfig.resolver.ForwardingValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolver;

/**
 * @author Steve Ash
 */
public class RequiredValueResolverDecorator extends ForwardingValueResolver {

    public RequiredValueResolverDecorator(ValueResolver delegate) {
        super(delegate);
    }

    @Override
    public Object resolve() {
        Object o = delegate.resolve();
        if (o != null)
            return o;

        throw RequiredConfigurationKeyNotPresentException.makeForMissingKey(delegate.configurationKeyToLookup());
    }
}
