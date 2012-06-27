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

package com.github.steveash.typedconfig.resolver;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Steve Ash
 */
public class HashCodeResolverTest {

    private HashCodeResolver configA42;
    private HashCodeResolver configA43;
    private HashCodeResolver configB42;
    private HashCodeResolver configB43;

    static interface ConfigA {
        int getA();
    }

    static interface ConfigB {
        int getB();
    }

    @Before
    public void setUp() throws Exception {
        configA42 = new HashCodeResolver(ConfigA.class,
                ImmutableMap.<Method, ValueResolver>of(
                        ConfigA.class.getDeclaredMethod("getA"), new InstanceValueResolver(42)));
        configA43 = new HashCodeResolver(ConfigA.class,
                ImmutableMap.<Method, ValueResolver>of(
                        ConfigA.class.getDeclaredMethod("getA"), new InstanceValueResolver(43)));
        configB42 = new HashCodeResolver(ConfigB.class,
                ImmutableMap.<Method, ValueResolver>of(
                        ConfigB.class.getDeclaredMethod("getB"), new InstanceValueResolver(42)));
        configB43 = new HashCodeResolver(ConfigB.class,
                ImmutableMap.<Method, ValueResolver>of(
                        ConfigB.class.getDeclaredMethod("getB"), new InstanceValueResolver(43)));
    }

    @Test
    public void shouldHaveDiffHashCodeForDiffValueSameClass() throws Exception {
        Assert.assertTrue(!configA42.resolve().equals(configA43));
        Assert.assertTrue(!configB42.resolve().equals(configB43));
    }

    @Test
    public void shouldHaveDiffHashCodeForSameValueDiffClass() throws Exception {
        Assert.assertTrue(!configA42.resolve().equals(configB42.resolve()));
    }

    @Test
    public void shouldReturnANonZeroHashCode() throws Exception {
        assertEquals(-39006130, configA42.resolve());
    }
}
