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

import com.github.steveash.typedconfig.PropertyUtil;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Steve Ash
 */
public class PropertyUtilTest {

    static interface Proxy {

        String getSomething();
        String getSomethingWithAParam(String cantDoThisInJavaBean);
        String notAProperty();
        boolean isSomething();
        void setSomething(String param);
        void setSomethingWithoutAParam();
    }

    @Test
    public void testGetProperty() throws Exception {
        assertEquals("something", PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("getSomething")));
        assertNull(PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("notAProperty")));
        assertNull(PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("getSomethingWithAParam", String.class)));
        assertEquals("something", PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("isSomething")));
    }

    @Test
    public void testSetProperty() throws Exception {
        assertEquals("something", PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("setSomething", String.class)));
        assertNull(PropertyUtil.getPropertyName(Proxy.class.getDeclaredMethod("setSomethingWithoutAParam")));
    }
}
