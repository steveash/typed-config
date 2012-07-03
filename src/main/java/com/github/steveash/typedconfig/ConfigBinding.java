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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Represents a single configuration binding (abstracted away from how the binding was declared (method etc)
 *
 * @author Steve Ash
 */
public class ConfigBinding {

    private static final List<Annotation> emptyAnns = ImmutableList.of();

    public static ConfigBinding makeRootBinding(TypeToken<?> dataType) {
        return new ConfigBinding("", dataType, Option.EmptyOptions, emptyAnns);
    }

    public static ConfigBinding makeShimForKey(String key) {
        return new ConfigBinding(key, null, Option.EmptyOptions, emptyAnns);
    }

    public static ConfigBinding makeForKeyAndType(String key, TypeToken<?> dataType) {
        return new ConfigBinding(key, dataType, Option.EmptyOptions, emptyAnns);
    }

    private final TypeToken<?> dataType;
    private final List<Option> options;
    private final List<Annotation> annotations;
    private final String configKeyToLookup;

    public List<Annotation> getAnnotations() {
        return annotations;
    }

    /**
     * @param configKeyToLookup
     * @param dataType
     * @param options
     * @param anns
     */
    public ConfigBinding(String configKeyToLookup, TypeToken<?> dataType, List<Option> options, List<Annotation> anns) {
        this.configKeyToLookup = configKeyToLookup;
        this.dataType = dataType;
        this.options = options;
        annotations = anns;
    }

    public String getConfigKeyToLookup() {
        return configKeyToLookup;
    }

    public TypeToken<?> getDataType() {
        return dataType;
    }

    public List<Option> getOptions() {
        return options;
    }

    public ConfigBinding withKey(String newConfigKey) {
        return new ConfigBinding(newConfigKey, dataType, options, annotations);
    }

    public ConfigBinding withDataType(TypeToken<?> newDataType) {
        return new ConfigBinding(this.configKeyToLookup, newDataType, options, annotations);
    }

    public ConfigBinding withOptions(List<Option> newOptions) {
        return new ConfigBinding(configKeyToLookup, dataType, newOptions, annotations);
    }

    public ConfigBinding withOption(Option optionToAdd) {
        List<Option> newOptions = Lists.newArrayList(options);
        newOptions.add(optionToAdd);
        return withOptions(newOptions);
    }

    public ConfigBinding withoutOption(Option optionToRemove) {
        List<Option> newOptions = Lists.newArrayList(options);
        newOptions.remove(optionToRemove);
        return withOptions(newOptions);
    }

    public boolean containsOption(Option option) {
        return options.contains(option);
    }
}
