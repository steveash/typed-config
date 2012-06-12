package com.github.steveash.typedconfig.resolver;

import org.junit.Test;
import com.github.steveash.typedconfig.ConfigBinding;

import java.util.Arrays;

import static junit.framework.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;

/**
 * @author Steve Ash
 */
public class ValueResolverRegistryTest {

    @Test
    public void shouldReturnFactoryThatCanHandleType() throws Exception {
        ValueResolverFactory factory = mock(ValueResolverFactory.class);
        ConfigBinding configBinding = ConfigBinding.makeShimForKey("");
        given(factory.canResolveFor(configBinding)).willReturn(true);
        ValueResolverRegistry registry = new ValueResolverRegistry(
                Arrays.<ValueResolverFactory>asList(factory));

        assertNotNull(registry.lookup(configBinding));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionIfNoFactoryCanHandleType() throws Exception {
        ValueResolverFactory factory = mock(ValueResolverFactory.class);
        ConfigBinding binding = mock(ConfigBinding.class);
        given(factory.canResolveFor(binding)).willReturn(false);
        ValueResolverRegistry registry = new ValueResolverRegistry(
                        Arrays.<ValueResolverFactory>asList(factory));

        assertNotNull(registry.lookup(binding));
    }
}