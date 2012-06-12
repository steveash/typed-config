package com.github.steveash.typedconfig.resolver;

import com.google.common.reflect.TypeToken;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.junit.Test;
import com.github.steveash.typedconfig.ConfigBinding;
import com.github.steveash.typedconfig.resolver.type.simple.ByteValueResolverFactory;

import java.lang.reflect.Method;

import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

/**
 * @author Steve Ash
 */
public class ByteValueResolverFactoryTest {
    @Test
    public void shouldReturnBoxedPrimitiveValue() throws Exception {
        HierarchicalConfiguration mock = mock(HierarchicalConfiguration.class);
        given(mock.getByte(eq("some.key"), (Byte)isNull())).willReturn((byte) 42);
        ValueResolver resolver = new ByteValueResolverFactory()
                .makeForThis(ConfigBinding.makeShimForKey("some.key"), mock, null);

        Byte result = (Byte) resolver.resolve();
        assertTrue(((byte) 42) == result);
    }

    @Test
    public void shouldResolveForPrimitiveType() throws Exception {
        ByteValueResolverFactory factory = new ByteValueResolverFactory();
        Method method = SampleClass.class.getDeclaredMethod("isPrimitiveReturn");

        assertTrue(factory.canResolveFor(ConfigBinding.makeRootBinding(TypeToken.of(method.getGenericReturnType()))));
    }

    @Test
    public void shouldResolveForBoxedType() throws Exception {
        ByteValueResolverFactory factory = new ByteValueResolverFactory();
        Method method = SampleClass.class.getDeclaredMethod("isBoxedReturn");

        assertTrue(factory.canResolveFor(ConfigBinding.makeRootBinding(TypeToken.of(method.getGenericReturnType()))));
    }

    static interface SampleClass {
        byte isPrimitiveReturn();
        Byte isBoxedReturn();
    }
}
