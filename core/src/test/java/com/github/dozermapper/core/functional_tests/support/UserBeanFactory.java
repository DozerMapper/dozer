/*
 * Copyright 2005-2019 Dozer Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.dozermapper.core.functional_tests.support;

import com.github.dozermapper.core.BeanFactory;
import com.github.dozermapper.core.MappingException;
import com.github.dozermapper.core.config.BeanContainer;
import com.github.dozermapper.core.vo.interfacerecursion.User;
import com.github.dozermapper.core.vo.interfacerecursion.UserGroup;
import com.github.dozermapper.core.vo.interfacerecursion.UserGroupImpl;
import com.github.dozermapper.core.vo.interfacerecursion.UserGroupPrime;
import com.github.dozermapper.core.vo.interfacerecursion.UserGroupPrimeImpl;
import com.github.dozermapper.core.vo.interfacerecursion.UserImpl;
import com.github.dozermapper.core.vo.interfacerecursion.UserPrime;
import com.github.dozermapper.core.vo.interfacerecursion.UserPrimeImpl;

public class UserBeanFactory implements BeanFactory {

    public Object createBean(Object aSrcObj, Class<?> aSrcObjClass, String aTargetBeanId, BeanContainer beanContainer) {
        // check null arguments
        if (aSrcObj == null || aSrcObjClass == null || aTargetBeanId == null) {
            return null;
        }

        // get class for target bean name
        Class<?> targetClass;
        try {
            targetClass = Class.forName(aTargetBeanId);
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        }

        // handle known interfaces
        if (User.class.isAssignableFrom(targetClass)) {
            return new UserImpl();
        }

        if (UserGroup.class.isAssignableFrom(targetClass)) {
            return new UserGroupImpl();
        }

        if (UserPrime.class.isAssignableFrom(targetClass)) {
            return new UserPrimeImpl();
        }

        if (UserGroupPrime.class.isAssignableFrom(targetClass)) {
            return new UserGroupPrimeImpl();
        }

        // otherwise create instance directly
        try {
            return targetClass.newInstance();
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }
}
