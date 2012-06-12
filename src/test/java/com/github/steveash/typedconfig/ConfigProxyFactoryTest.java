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

import com.github.steveash.typedconfig.ConfigFactoryContext;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author Steve Ash
 */
public class ConfigProxyFactoryTest {

    public static final ConfigFactoryContext defaultContext = ConfigProxyFactory.builder().buildContext();

    static interface Proxy {

    }

    @Test
    public void shouldBuildDefaultWithoutException() throws Exception {
        ConfigProxyFactory.getDefault().make(Proxy.class, mock(HierarchicalConfiguration.class));
    }
}
