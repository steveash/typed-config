package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Integration Test to exercise a number of features
 * @author Steve Ash
 */
public class ConfigProxySimple1IntegrationTest {

    private Simple1 proxy;

    // no config proxy annotation
    static interface Simple1 {

        String getMyConfig1();

        @Config("myConfig2")
        String myConfig2Value();
    }

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault().make(Simple1.class, new XMLConfiguration("simple1Integration.xml"));
    }

    @Test
    public void testWithConfigValueAnnotation() throws Exception {
        assertEquals("456", proxy.myConfig2Value());
    }

    @Test
    public void testWithoutConfigValueAnnotation() throws Exception {
        assertEquals("123", proxy.getMyConfig1());
    }
}
