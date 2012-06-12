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

package com.github.steveash.typedconfig.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the javabean property to be used on the child object to retrieve the key for that object.  The key type
 * must be simple and must have a correct equals and hashcode implementation
 * @author Steve Ash
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface MapKey {

    /**
     * @return javabean property to use as the key
     */
    String value();

    /**
     * @return if true then calling get with a key value that doesn't exist will cause an IllegalArgumentException
     * to be thrown.  This is not the typical behavior of a map but mimics the default "required" nature of proxy
     * values (@see Config#options default).  Plus it doesn't propagate nulls around
     */
    boolean required() default true;
}
