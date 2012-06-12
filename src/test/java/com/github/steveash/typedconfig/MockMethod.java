package com.github.steveash.typedconfig;

import java.lang.reflect.Method;

/**
 * Test helper for getting simple Method handles for test cases since mockito cant mock final classes due to general
 * Java silliness
 * @author Steve Ash
 */
public class MockMethod {

    static interface MockInterface {

        boolean mockMethod();
    }

    public static Method getMockMethod() {
        try {
            return MockInterface.class.getDeclaredMethod("mockMethod");
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e); // cant happen
        }
    }
}
