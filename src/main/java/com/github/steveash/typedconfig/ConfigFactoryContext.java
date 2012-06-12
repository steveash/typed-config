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

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.commons.configuration.SubnodeConfiguration;
import org.apache.commons.configuration.event.ConfigurationEvent;
import org.apache.commons.configuration.event.ConfigurationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;
import com.github.steveash.typedconfig.caching.CacheStrategy;
import com.github.steveash.typedconfig.defaultvalue.DefaultValueStrategy;
import com.github.steveash.typedconfig.keycombine.KeyCombinationStrategy;
import com.github.steveash.typedconfig.resolver.ValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueResolverRegistry;
import com.github.steveash.typedconfig.resolver.ValueType;
import com.github.steveash.typedconfig.validation.ValidationStrategy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

/**
 * This is the internal SPI that value resolvers et al use to resolve their configuration values
 * Thread safe
 *
 * @author Steve Ash
 */
public class ConfigFactoryContext implements ConfigurationListener {
    private static final Logger log = LoggerFactory.getLogger(ConfigFactoryContext.class);

    private final ValueResolverRegistry registry;
    private final ValidationStrategy validationStrategy;
    private final DefaultValueStrategy defaultStrategy;
    private final KeyCombinationStrategy keyStrategy;
    private final CacheStrategy cacheStrategy;
    private final ConfigAnnotationResolver annotationResolver;
    private final EventBus eventBus = new EventBus("config-proxy");

    public ConfigFactoryContext(ValueResolverRegistry registry, ValidationStrategy validationStrategy,
                                DefaultValueStrategy defaultStrategy, KeyCombinationStrategy keyStrategy,
                                CacheStrategy cacheStrategy) {
        this.registry = registry;
        this.keyStrategy = keyStrategy;
        this.validationStrategy = validationStrategy;
        this.defaultStrategy = defaultStrategy;
        this.cacheStrategy = cacheStrategy;
        this.annotationResolver = new ConfigAnnotationResolver();

        eventBus.register(this);
    }

    /**
     * Produces a resolver that knows how to deal with the given binding for the given config; does not
     * use any decorators or anything just the binding and the value type for the factory that is chosen
     * by the registry
     *
     *
     * @param config
     * @param newMethodBinding
     *@param parentBinding @return
     */
    public ValueResolver makeResolverForBinding(HierarchicalConfiguration config, ConfigBinding newMethodBinding,
                                                ConfigBinding parentBinding) {
        ValueResolverFactory factory = getRegistry().lookup(newMethodBinding);

        switch (factory.getValueType()) {
            case Container:
            case Simple:
                return factory.makeForThis(newMethodBinding, config, this);

            case Nested:
                Preconditions.checkNotNull(parentBinding);
                // config points to the location of the nested context
                SubnodeConfiguration subConfig = config.configurationAt(newMethodBinding.getConfigKeyToLookup(), true);
                ConfigBinding subBinding = newMethodBinding.withKey(subConfig.getSubnodeKey());
                return factory.makeForThis(subBinding, subConfig, this);

            default:
                throw new IllegalArgumentException("dont know how to handle value type: " + factory.getValueType());
        }
    }

    public ValueType getValueTypeForBinding(ConfigBinding binding) {
        return getRegistry().lookup(binding).getValueType();
    }

    public ValueResolverRegistry getRegistry() {
           return registry;
       }

    public ValidationStrategy getValidationStrategy() {
        return validationStrategy;
    }

    public DefaultValueStrategy getDefaultStrategy() {
        return defaultStrategy;
    }

    public CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public ConfigAnnotationResolver getAnnotationResolver() {
        return annotationResolver;
    }

    public KeyCombinationStrategy getKeyStrategy() {
        return keyStrategy;
    }

    /**
     * Given the class and method on the proxy this will create a new ConfigBinding whcih represents a particular
     * "binding" between a proxy call site and the configuration target (not the configuration instance, but the
     * location (key) within a configuration instance (w.r.t the same configuration context))
     * @param interfaze
     * @param method
     * @param config
     * @return
     */
    public ConfigBinding getBindingFor(Class<?> interfaze, Method method, HierarchicalConfiguration config) {

        ConfigProxy configProxy = getAnnotationResolver().getConfigProxy(interfaze);
        Config configValue = getAnnotationResolver().getConfigAnnotation(method);
        List<Option> options = Arrays.asList(configValue.options());

        String localKey = getLocalConfigKey(method, configValue);
        String baseKey = configProxy.basekey();
        String combinedLocalKey = getCombinedKey(baseKey, localKey, config);

        List<Annotation> anns = Lists.newArrayList(method.getDeclaredAnnotations());

        return new ConfigBinding(combinedLocalKey, TypeToken.of(method.getGenericReturnType()), options, anns);
    }

    private String getCombinedKey(String baseKey, String localKey, HierarchicalConfiguration config) {
        return keyStrategy.combineKey(baseKey, localKey, config);
    }

    private String getLocalConfigKey(Method method, Config config) {
        if (!config.value().equals(""))
            return config.value();

        if (PropertyUtil.isProperty(method)) {
            return Preconditions.checkNotNull(PropertyUtil.getPropertyName(method));
        }
        int argsCount = method.getParameterTypes().length;
        if (argsCount == 0) {
            return method.getName(); // default to the name. should we allow this?
        }
        throw new IllegalArgumentException("Method " + method.toGenericString() + " takes > 0 parameters which " +
                "is not supported.");
    }

    @Override
    public void configurationChanged(ConfigurationEvent event) {
        if (event.isBeforeUpdate())
            return;

//        System.out.println("Received event: " + event.getType() + " for property name: " + event.getPropertyName() +
//                        " -> " + event.getPropertyValue() + ", is before? " + event.isBeforeUpdate());
        eventBus.post(event);
    }

}
