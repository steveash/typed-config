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

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;

import com.github.steveash.typedconfig.annotation.Config;

/**
 * @author Steve Ash
 */
public class LookupIntegrationTest {

    private House proxy;

    static interface Person {
        String getTitle();
    }

    static interface House {
        @Config(options = Option.LOOKUP_RESULT)
        Person getHeadOfHouse();

        @Config("people.person")
        List<Person> getPeople();
    }

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault().make(House.class, new XMLConfiguration("lookupIntegration.xml"));
    }

    @Test
    public void shouldReturnLookedupValue() throws Exception {
        assertEquals(3, proxy.getPeople().size());
        assertEquals("father", proxy.getPeople().get(1).getTitle());
        assertEquals("father", proxy.getHeadOfHouse().getTitle());
    }
}
