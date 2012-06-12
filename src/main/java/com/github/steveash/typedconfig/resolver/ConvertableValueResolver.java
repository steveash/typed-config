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

import org.apache.commons.beanutils.ConvertUtilsBean;

/**
 * Base class that provides support for default value conversion for anything that supports it
 * @author Steve Ash
 */
public abstract class ConvertableValueResolver implements ValueResolver {

    private final Class<?> targetClazz;
    private final String key;
    private final ConvertUtilsBean converter;

    public ConvertableValueResolver(Class<?> targetClazz, String key) {
        this.targetClazz = targetClazz;
        this.key = key;
        this.converter = new ConvertUtilsBean();
    }

    @Override
    public Object convertDefaultValue(String defaultValue) {
        return converter.convert(defaultValue, targetClazz);
    }

    @Override
    public String configurationKeyToLookup() {
        return key;
    }
}
