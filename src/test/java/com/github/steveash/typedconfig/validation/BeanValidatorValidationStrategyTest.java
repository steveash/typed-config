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

package com.github.steveash.typedconfig.validation;

import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.Range;
import org.junit.Before;
import org.junit.Test;
import com.github.steveash.typedconfig.exception.InvalidProxyException;
import com.github.steveash.typedconfig.resolver.ValueResolver;

import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Steve Ash
 */
public class BeanValidatorValidationStrategyTest {

    private BeanValidatorValidationStrategy strategy;
    private ValueResolver resolver;

    @Before
    public void setUp() throws Exception {
        strategy = new BeanValidatorValidationStrategy();
        resolver = mock(ValueResolver.class);
    }

    @Test(expected = InvalidProxyException.class)
    public void shouldThrowExceptionIfAnnotationsOnInvalidMethods() throws Exception {
        strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("eekThisIsNotAJavaBean"));
        fail();
    }

    @Test
    public void shouldValidatePropertyForGoodStrings() throws Exception {
        when(resolver.resolve()).thenReturn("yep");
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getIt1"));

        assertEquals("yep", validating.resolve());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldValidatePropertyForBadStrings() throws Exception {
        when(resolver.resolve()).thenReturn("    "); // thats blank
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getIt1"));

        validating.resolve();
        fail();
    }

    @Test
    public void shouldValidatePropertyForGoodInts() throws Exception {
        when(resolver.resolve()).thenReturn(15);
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getInt"));

        assertEquals(15, validating.resolve());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldValidatePropertyForBadInts() throws Exception {
        when(resolver.resolve()).thenReturn(25); // thats blank
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getInt"));

        validating.resolve();
        fail();
    }


    @Test
    public void shouldValidatePropertyForGoodRegex() throws Exception {
        when(resolver.resolve()).thenReturn("555-55-5555");
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getARegularOne"));

        assertEquals("555-55-5555", validating.resolve());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldValidatePropertyForBadRegex() throws Exception {
        when(resolver.resolve()).thenReturn("123456"); // thats blank
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getARegularOne"));

        validating.resolve();
        fail();
    }

    @Test
    public void shouldValidatePropertyForGoodEmail() throws Exception {
        when(resolver.resolve()).thenReturn("stevemash@gmail.com");
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getEmail"));

        assertEquals("stevemash@gmail.com", validating.resolve());
    }

    @Test(expected = ConstraintViolationException.class)
    public void shouldValidatePropertyForBadEmail() throws Exception {
        when(resolver.resolve()).thenReturn("thisAintNoEmailAddress"); // thats blank
        ValueResolver validating = strategy.decorateForValidation(resolver, MockProxy.class,
                MockProxy.class.getDeclaredMethod("getEmail"));

        validating.resolve();
        fail();
    }

    static interface MockProxy {

        @NotBlank
        String getIt1();

        @NotBlank
        String eekThisIsNotAJavaBean();

        @Range(min = 10, max = 20)
        int getInt();

        @Pattern(regexp = "\\d{3}-\\d{2}-\\d{4}")
        String getARegularOne();

        @Email
        String getEmail();
    }
}
