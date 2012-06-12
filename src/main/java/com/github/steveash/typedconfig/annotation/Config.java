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

import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.Option;

/**
 * For a class annotated with {@code @}{@link ConfigProxy}, maps a method to a key in an {@link HierarchicalConfiguration Apache Commons
 * configuration}.
 * <p>
 * Values coming out of the configuration will perform conversion as needed to return values of the correct type. Return types must be one of:
 * <ul>
 * <li>BigDecimal</li>
 * <li>BigInteger</li>
 * <li>Boolean</li>
 * <li>Double</li>
 * <li>Float</li>
 * <li>Integer</li>
 * <li>Long</li>
 * <li>Short</li>
 * <li>String</li>
 * <li>List</li>
 * </ul>
 * 
 * @author jonny
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Config {

	/**
	 * The name of the configuration key this configuration value maps to. This will be relative to the <tt>basekey</tt> in the enclosing class
	 * {@link ConfigProxy} annotation, if set.  If the key is not specified then the name of the method will be used
     * (stripping any prefixing get/is per the javabean specification).  A method called getMyProperty will search
     * the configuration for key "myProperty"
	 */
	String value() default "";

	/**
	 * If defaultKey is specified, the value for that key will be used if the key for this configuration value has no value.
	 */
	String defaultLookup() default "";

	/**
	 * If defaultValue is specified, that value will be used if the key for this configuration value has no value. (The default value will undergo
	 * conversion and validation as normal.)
	 */
	String defaultValue() default "";

	/**
	 * Additional options for configuration values
	 */
	Option[] options() default {Option.REQUIRED};

}
