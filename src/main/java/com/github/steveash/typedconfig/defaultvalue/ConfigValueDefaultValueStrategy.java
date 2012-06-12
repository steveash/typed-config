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

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.Option;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.exception.InvalidProxyException;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.resolver.ValueType;

import java.lang.reflect.Method;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * Defines the strategy of decorating the resolver by inspecting the ConfigValue annotation on the
 * method.  The strategy is:
 *   If the defaultLookup is set try that
 *     If defaultLookup finds nothing then move on to try default Value
 *   If the defaultValue is set try that
 *   If neither of these resolve and the property is marked as required (or its primitive which is implicitly reqd)
 *     then throw a requiredPropertyMissing exception
 *
 * @author Steve Ash
 */
public class ConfigValueDefaultValueStrategy implements DefaultValueStrategy {

    public ValueResolver decorateForDefaults(ValueResolver resolver, HierarchicalConfiguration config,
                                             ConfigBinding binding, ConfigFactoryContext context,
                                             Class<?> interfaze, Method method) {

        Config configValue = context.getAnnotationResolver().getConfigAnnotation(method);
        ValueType valueType = context.getValueTypeForBinding(binding);

        String defaultLookup = configValue.defaultLookup();
        if (isNotBlank(defaultLookup)) {

            ValueResolver defaultResolver = context.makeResolverForBinding(
                    config, ConfigBinding.makeForKeyAndType(defaultLookup, binding.getDataType()), null);
            resolver = new DefaultLookupValueResolverDecorator(resolver, defaultResolver);
        }

        String defaultValue = configValue.defaultValue();
        if (isNotBlank(defaultValue)) {
            throwIfNotSimpleValueType(valueType, binding);
            resolver = new DefaultGivenValueResolverDecorator(resolver, defaultValue);
        }

        if (isValueRequired(binding)) {
            resolver = new RequiredValueResolverDecorator(resolver);
        }
        return resolver;
    }

    private void throwIfNotSimpleValueType(ValueType valueType, ConfigBinding binding) {
        if (valueType != ValueType.Simple)
            throw new InvalidProxyException("The proxy method looking up key " + binding.getConfigKeyToLookup() +
                    " returns data type: " + binding.getDataType() + " which is not simple and cannot have a default " +
                    "value set for it.");
    }

    // value is required at a certain point in the pipeline for primitives (which would otherwise be unboxed
    // throwing a NPE or if they are marked as "required"
    private boolean isValueRequired(ConfigBinding newMethodBinding) {
        return newMethodBinding.getOptions().contains(Option.REQUIRED) ||
                newMethodBinding.getDataType().getRawType().isPrimitive();
    }
}
