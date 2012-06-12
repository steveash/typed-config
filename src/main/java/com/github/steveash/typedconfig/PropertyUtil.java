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

import java.beans.Introspector;
import java.lang.reflect.Method;

/**
 * @author Steve Ash
 */
public class PropertyUtil {

    private static final String getPrefix = "get";
    private static final String setPrefix = "set";
    private static final String isPrefix = "is";

    public static boolean isProperty(Method method) {
        return getPropertyName(method) != null;
    }

    public static String getPropertyName(Method method) {
        String name = method.getName();
        int argCount = method.getParameterTypes().length;
        if (argCount == 0) {
            if (name.startsWith(getPrefix))
                return Introspector.decapitalize(name.substring(getPrefix.length()));
            if (name.startsWith(isPrefix)) {
                return Introspector.decapitalize(name.substring(isPrefix.length()));
            }
        } else if (argCount == 1) {
            if (name.startsWith(setPrefix))
                return Introspector.decapitalize(name.substring(setPrefix.length()));
        }
        return null;
    }
}
