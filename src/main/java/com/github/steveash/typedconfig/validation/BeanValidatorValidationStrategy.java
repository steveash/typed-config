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

import com.google.common.collect.Sets;
import com.github.steveash.typedconfig.PropertyUtil;
import com.github.steveash.typedconfig.exception.InvalidProxyException;
import com.github.steveash.typedconfig.resolver.ValueResolver;

import javax.validation.Validation;
import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @author Steve Ash
 */
public class BeanValidatorValidationStrategy implements ValidationStrategy {

    private final Validator validator;

    public BeanValidatorValidationStrategy(Validator validator) {
        this.validator = validator;
    }

    public BeanValidatorValidationStrategy() {
        this(Validation.buildDefaultValidatorFactory().getValidator());
    }

    @Override
    public ValueResolver decorateForValidation(ValueResolver resolver, Class<?> interfaze, Method method) {
        if (PropertyUtil.isProperty(method)) {
            if (hasValidationAnnotation(method)) {
                String propertyName = PropertyUtil.getPropertyName(method);
                resolver = new ValidatorResolverDecorator(resolver, interfaze, propertyName, validator);
            }
        } else {
            throwIfMethodHasValidationAnnotations(method);
        }
        return resolver;
    }

    private void throwIfMethodHasValidationAnnotations(Method method) {
        Annotation ann = getFirstValidationAnnotationOrNull(method);
        if (ann != null) {
            throw new InvalidProxyException("The proxy method " + method.getName() +
                    " has the validation annotation " + ann.toString() + " but is not a javabean property " +
                    "(doesnt start with 'is' or 'get').  Per the validator spec, you can only validate properties");

        }
    }

    private boolean hasValidationAnnotation(Method method) {
        return getFirstValidationAnnotationOrNull(method) != null;
    }

    private Annotation getFirstValidationAnnotationOrNull(Method method) {
        Set<Annotation> checkedAnnotations = Sets.newHashSet();
        for (Annotation a : method.getDeclaredAnnotations()) {
            if (isValidationAnnotation(a, checkedAnnotations)) {
                return a;
            }
        }
        return null;
    }

    private boolean isValidationAnnotation(Annotation a, Set<Annotation> checkedAnnotations) {
        if (checkedAnnotations.contains(a))
            return false;
        checkedAnnotations.add(a); // catch cycles

        if (a.annotationType().getName().startsWith("java.lang"))
            return false;

        if (a.annotationType().getName().startsWith("javax.validation"))
            return true;

        for (Annotation nestedAnnotation : a.annotationType().getDeclaredAnnotations()) {
            if (isValidationAnnotation(nestedAnnotation, checkedAnnotations))
                return true;
        }
        return false;
    }
}
