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

import java.util.ArrayList;
import java.util.List;

public enum Option {
	/**
	 * Marks a {@link com.github.steveash.typedconfig.annotation.Config} as required. If the key for the Config does not exist in the underlying configuration, or it does not have
	 * a value, an exception will be thrown when it is retrieved.
	 */
	REQUIRED,

	/**
	 * Changes the behaviour of getting a {@link com.github.steveash.typedconfig.annotation.Config} into a 'check exists' - the method will return true if the key for the Config
	 * exists in the configuration, regardless of its value and even if it has no value. The method must return a boolean if this option is used.
	 */
	CHECK_KEY_EXISTS

	;

    public static final List<Option> EmptyOptions = new ArrayList<Option>();
}
