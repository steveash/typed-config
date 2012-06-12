package com.github.steveash.typedconfig;

import com.github.steveash.typedconfig.ConfigProxyFactory;
import org.apache.commons.configuration.XMLConfiguration;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;

import static org.junit.Assert.assertEquals;

/**
 * @author Steve Ash
 */
public class ConfigProxySimple2IntegrationTest {

    private Simple2 proxy;

    @ConfigProxy(basekey = "myBase.")
    static interface Simple2 {

        String myConfig1();

        @Config("myConfig2")
        String anotherProperty();
    }

    @Before
    public void setUp() throws Exception {
        proxy = ConfigProxyFactory.getDefault().make(Simple2.class,
                new XMLConfiguration("simple2Integration.xml"));
    }

    @Test
    public void testWithBaseAndWithoutConfigValueAnnotation() throws Exception {
        assertEquals("123", proxy.myConfig1());
        assertEquals("456", proxy.anotherProperty());
    }


}
