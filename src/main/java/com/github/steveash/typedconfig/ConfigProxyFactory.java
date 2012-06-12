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
import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration.HierarchicalConfiguration;
import com.github.steveash.typedconfig.caching.CacheEverythingForeverStategy;
import com.github.steveash.typedconfig.caching.CacheNestedProxyStrategy;
import com.github.steveash.typedconfig.caching.CacheNothingStrategy;
import com.github.steveash.typedconfig.caching.CacheStrategy;
import com.github.steveash.typedconfig.defaultvalue.ConfigValueDefaultValueStrategy;
import com.github.steveash.typedconfig.defaultvalue.DefaultValueStrategy;
import com.github.steveash.typedconfig.keycombine.KeyCombinationStrategy;
import com.github.steveash.typedconfig.keycombine.SmartDelimitedKeyCombinationStrategy;
import com.github.steveash.typedconfig.resolver.ValueResolverFactory;
import com.github.steveash.typedconfig.resolver.ValueResolverRegistry;
import com.github.steveash.typedconfig.validation.BeanValidatorValidationStrategy;
import com.github.steveash.typedconfig.validation.NoValidationStrategy;
import com.github.steveash.typedconfig.validation.ValidationStrategy;

import java.util.List;

public class ConfigProxyFactory {

    /**
     * @return the default factory which uses bean validation and only caches proxies.
     */
    public static ConfigProxyFactory getDefault() {
        return Holder.instance;
    }

    /**
     * @return a builder that helps in creating a config proxy factory parameterized to meet your needs
     */
    public static Builder builder() {
        return new Builder();
    }

    private static final class Holder {
        private static final ConfigProxyFactory instance = builder().build();
    }

    public static class Builder {

        private final List<ValueResolverFactory> userFactories = Lists.newArrayList();
        private ValidationStrategy validationStrategy = new BeanValidatorValidationStrategy();
        private DefaultValueStrategy defaultStrategy = new ConfigValueDefaultValueStrategy();
        private KeyCombinationStrategy keyStrategy = new SmartDelimitedKeyCombinationStrategy();
        private CacheStrategy cacheStrategy = new CacheNestedProxyStrategy();

        private Builder() { }

        public ConfigProxyFactory build() {
            return new ConfigProxyFactory(buildContext());
        }

        ConfigFactoryContext buildContext() {
            return new ConfigFactoryContext(
                    ValueResolverRegistry.makeRegistryWithUserTypes(userFactories),
                    validationStrategy,
                    defaultStrategy,
                    keyStrategy,
                    cacheStrategy
            );
        }

        /**
         * This means that every method invocation on a proxy will always go back to the underlying configuration
         * object.  We think #cacheOnlyProxies is behaviorally equivalent to this with some performance gain and
         * thus it should be preferred.
         * @return
         */
        public Builder cacheNothing() {
            this.cacheStrategy = new CacheNothingStrategy();
            return this;
        }

        /**
         * This strategy caches only the nested proxies so they aren't rebuilt on every access.  We can't think of
         * a use case where this would lead to unexpected behavior, and thus it is the default.  However, if you
         * always want to rebuild every proxy every time, then @see #cacheNothing
         * @return
         */
        public Builder cacheOnlyProxies() {
            this.cacheStrategy = new CacheNestedProxyStrategy();
            return this;
        }

        /**
         * If you are only loading the configuration once and it is immutable at runtime, then prefer the
         * cacheEverythingForever approach.  The underlying configuration will be accessed only the first time
         * to lazily init the value
         * @return
         */
        public Builder cacheEverythingForever() {
            this.cacheStrategy = new CacheEverythingForeverStategy();
            return this;
        }

        public Builder withCustomCacheStrategy(CacheStrategy cacheStrategy) {
            this.cacheStrategy = Preconditions.checkNotNull(cacheStrategy);
            return this;
        }

        public Builder withCustomKeyCombinationStrategy(KeyCombinationStrategy keyStrategy) {
            this.keyStrategy = Preconditions.checkNotNull(keyStrategy);
            return this;
        }

        public Builder withCustomDefaultValueStrategy(DefaultValueStrategy defaultStrategy) {
            this.defaultStrategy = Preconditions.checkNotNull(defaultStrategy);
            return this;
        }

        /**
         * If you have custom datatypes that you want to support then you can register your own value resovlers
         * to handle them
         * @param factory
         * @return
         */
        public Builder addValueResolver(ValueResolverFactory factory) {
            userFactories.add(Preconditions.checkNotNull(factory));
            return this;
        }

        /**
         * This is the default validation mode which uses the javax.validation jsr305 to perform validation
         * on the property members of the configuration proxies.  If you prefer to do whole-graph validation
         * instead of lazy on-access validation, then prefer the #noValidation approach.
         * @return
         */
        public Builder beanValidation() {
            this.validationStrategy = new BeanValidatorValidationStrategy();
            return this;
        }

        /**
         * This doesn't perform any validation on the values including required values.  This is appropriate if
         * you plan to pass the whole graph through the validator once on startup and never want to revalidate
         * things.  In this case, be sure to use the approach @Valid, @NotNull, etc. constraints to mark up the
         * proxy interfaces
         * @return
         */
        public Builder noValidation() {
            this.validationStrategy = new NoValidationStrategy();
            return this;
        }

        public Builder withCustomValidationStrategy(ValidationStrategy validationStrategy) {
            this.validationStrategy = Preconditions.checkNotNull(validationStrategy);
            return this;
        }
    }

    private final ConfigFactoryContext context;

    ConfigProxyFactory(ConfigFactoryContext context) {
        this.context = context;
    }

    /**
     * Simple convenience method that creates a proxy, implementing the given interface class, that retrieves values from the given {@link HierarchicalConfiguration}.
     * <p/>
     * <tt>interfaze</tt> must represent a class that will be used as a "strongly-typed" configuration proxy whcih may
     * be optionally annotated with {@link com.github.steveash.typedconfig.annotation.ConfigProxy} containing methods optionally annotated with {@link com.github.steveash.typedconfig.annotation.Config}
     *
     * @param interfaze
     * @param configuration
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T make(Class<T> interfaze, HierarchicalConfiguration configuration) {
        Preconditions.checkNotNull(interfaze);
        Preconditions.checkArgument(interfaze.isInterface(), "You can only build proxies for interfaces");

        // the context listens to the root configuration because the subnode configs dont seem to propagate events
        configuration.addConfigurationListener(context);

        ConfigBinding binding = ConfigBinding.makeRootBinding(TypeToken.of(interfaze));
        ValueResolverFactory factory = context.getRegistry().lookup(binding);

        return (T) factory.makeForThis(binding, configuration, context).resolve();
    }
}
