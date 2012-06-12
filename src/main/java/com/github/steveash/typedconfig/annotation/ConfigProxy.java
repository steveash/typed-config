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

/**
 * Interfaces with this annotation will be bound to proxies at runtime that look up values against an {@link HierarchicalConfiguration Apache Commons
 * configuration} instance. Interfaces with this annotation should contain methods with the {@code @}{@link Config} annotation.
 * <p>
 * Proxy implementations are provided by the {@link com.github.steveash.typedconfig.ConfigProxyFactory}. This class can be used to manually create proxies, or can be tied into a
 * dependency-injection framework to provide automatic injection of proxies.
 * 
 * @author jonny
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigProxy {

	/**
	 * The text value that will prefix all of the key lookups below.  The way that the basekey and the keys
     * specified in the @Config annotation can be set on the ConfigProxyFactory.  For example, if you
     * are using the xpath expression engine then you will want to combine them with a slash. if you are
     * using the
	 */
	String basekey() default "";
}
