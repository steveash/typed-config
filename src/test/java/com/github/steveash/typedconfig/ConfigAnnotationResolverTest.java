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

import com.github.steveash.typedconfig.ConfigAnnotationResolver;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Steve Ash
 */
public class ConfigAnnotationResolverTest {

    private ConfigAnnotationResolver annotationResolver;

    @Before
    public void setUp() throws Exception {
        annotationResolver = new ConfigAnnotationResolver();
    }

    @Test
    public void shouldReturnDefaultConfigAnnotation() throws Exception {
        Method methodWithout = TestClassWithAnnotation.class.getDeclaredMethod("methodWithout");
        Config result = annotationResolver.getConfigAnnotation(methodWithout);
        assertNotNull(result);
        assertEquals("", result.value());
    }
    @Test
    public void shouldReturnPresentConfigAnnotation() throws Exception {
        Method methodWithout = TestClassWithAnnotation.class.getDeclaredMethod("methodWith");
        Config result = annotationResolver.getConfigAnnotation(methodWithout);
        assertNotNull(result);
        assertEquals("some.config.key", result.value());
    }

    @Test
    public void testGetConfigProxy() throws Exception {
        ConfigProxy proxy = annotationResolver.getConfigProxy(TestClassWithAnnotation.class);
        assertNotNull(proxy);
        assertEquals("some.prefix", proxy.basekey());
    }

    @Test
    public void testGetDefaultConfigProxy() throws Exception {
        ConfigProxy proxy = annotationResolver.getConfigProxy(TestClassWithoutAnnotation.class);
        assertNotNull(proxy);
        assertEquals("", proxy.basekey());
    }

    @ConfigProxy(basekey = "some.prefix")
    public static class TestClassWithAnnotation {
        @Config("some.config.key")
        public void methodWith() {

        }
        public void methodWithout() {

        }
    }

    // no ConfigProxy
    public static class TestClassWithoutAnnotation {

    }
}
