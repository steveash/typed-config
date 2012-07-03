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
package com.github.steveash.typedconfig.exception;

import org.apache.commons.configuration.HierarchicalConfiguration;

import com.github.steveash.typedconfig.ConfigurationPrinter;
import com.github.steveash.typedconfig.Option;

/**
 * Thrown by methods on a ConfigProxy that have the {@link Option#REQUIRED} option and the key does not exist (or does not have a value).
 *
 * @author jonny
 */
public class RequiredConfigurationKeyNotPresentException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    private static final ConfigurationPrinter printer = new ConfigurationPrinter();

    public static RequiredConfigurationKeyNotPresentException makeForMissingKey(String key,
            HierarchicalConfiguration config) {

        return new RequiredConfigurationKeyNotPresentException("The configuration key [" + key + "] was not found in " +
                "the configuration and the property is marked as 'required' or is a primitive value which cannot be " +
                "missing.  The current configuration is:\n" + printer.printToString(config));
    }

    public RequiredConfigurationKeyNotPresentException(final String msg) {
        super(msg);
    }
}
