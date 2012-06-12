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

package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;

import java.lang.reflect.Method;

/**
 * Helps resolve annotations from config proxies w.r.t default values
 * @author Steve Ash
 */
public class ConfigAnnotationResolver {

    public Config getConfigAnnotation(Method method) {
        Config config = method.getAnnotation(Config.class);
        if (config != null)
            return config;

        try {
            Method defaultMethod = DefaultValuesClass.class.getDeclaredMethod("defaultMethod");
            return defaultMethod.getAnnotation(Config.class);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e); // cant happen
        }
    }

    public ConfigProxy getConfigProxy(Class<?> interfaze) {
        ConfigProxy configProxy = interfaze.getAnnotation(ConfigProxy.class);
        if (configProxy != null)
            return configProxy;

        return DefaultValuesClass.class.getAnnotation(ConfigProxy.class);
    }

    @ConfigProxy
    private static class DefaultValuesClass {
        @Config
        void defaultMethod() {
        }
    }
}
