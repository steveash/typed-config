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

import com.github.steveash.typedconfig.resolver.ForwardingValueResolver;
import com.github.steveash.typedconfig.resolver.ValueResolver;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * @author Steve Ash
 */
public class ValidatorResolverDecorator extends ForwardingValueResolver {

    private final Class clazz;
    private final String method;
    private final Validator validator;

    public ValidatorResolverDecorator(ValueResolver delegate, Class clazz, String method, Validator validator) {
        super(delegate);
        this.clazz = clazz;
        this.method = method;
        this.validator = validator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object resolve() {
        Object o = delegate.resolve();
        Set<ConstraintViolation<?>> constraintViolations = validator.validateValue(clazz, method, o);
        if (constraintViolations.size() > 0) {
            throw new ConstraintViolationException("The configuration method '" + method + "' on " + clazz.getName() +
                    " which reads the configuration key [" + delegate.configurationKeyToLookup() + "] returned value [" +
                    (o == null ? "null" : o.toString()) + "] which failed the validation constraints: " +
                    constraintViolations.toString(), constraintViolations);
        }
        return o;
    }
}
