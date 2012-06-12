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

import com.github.steveash.typedconfig.exception.InvalidConfigurationValueException;
import com.github.steveash.typedconfig.resolver.ForwardingValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolver;

/**
 * @author Steve Ash
 */
public class DefaultGivenValueResolverDecorator extends ForwardingValueResolver {

    private final Object defaultValue;

    public DefaultGivenValueResolverDecorator(ValueResolver delegate, String defaultValueAsString) {
        super(delegate);
        try {
            this.defaultValue = delegate.convertDefaultValue(defaultValueAsString);
        } catch (RuntimeException e) {
            throw new InvalidConfigurationValueException("Cannot convert the default value [" +
                    defaultValueAsString + " given for config key " + delegate.configurationKeyToLookup() +
                    ": " + e.getMessage());
        }
    }

    @Override
    public Object resolve() {
        Object o = delegate.resolve();
        if (o != null)
            return null;

        return defaultValue;
    }
}
