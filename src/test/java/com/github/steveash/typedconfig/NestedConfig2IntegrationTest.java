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

import org.apache.commons.configuration.XMLConfiguration;
import org.hibernate.validator.constraints.NotEmpty;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;

import javax.validation.ConstraintViolationException;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class NestedConfig2IntegrationTest {

    private Proxy proxy;

    static interface Proxy {
        int getA();

        @Config("nested.b")
        List<Integer> getBs();

        List<Float> getC();

        @NotEmpty // should fail
        List<Integer> getD();

        @NotEmpty // should not
        List<Integer> getE();
    }

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault().make(Proxy.class, new XMLConfiguration("nestedConfig2.xml"));
    }

    @Test
    public void testSimpleTypeLists() throws Exception {
        assertEquals(42, proxy.getA());
        assertEquals(4, proxy.getBs().size());
        assertEquals(100, (int) proxy.getBs().get(0));
        assertEquals(101, (int) proxy.getBs().get(1));
        assertEquals(102, (int) proxy.getBs().get(2));
        assertEquals(103, (int) proxy.getBs().get(3));

        assertEquals(4, proxy.getC().size());
        assertEquals(1.2, (float) proxy.getC().get(0), 0.01);
        assertEquals(1.3, (float) proxy.getC().get(1), 0.01);
        assertEquals(1.4, (float) proxy.getC().get(2), 0.01);
        assertEquals(1.5, (float) proxy.getC().get(3), 0.01);
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldValidateBadListItems() throws Exception {
        proxy.getD();
    }

    @Test
    public void shouldValidateGoodListItems() throws Exception {
        List<Integer> results = proxy.getE();
        assertEquals(3, results.size());
    }
}
