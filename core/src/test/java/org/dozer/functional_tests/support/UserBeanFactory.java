/*
 * Copyright 2005-2017 Dozer Project
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
package org.dozer.functional_tests.support;

import org.dozer.BeanFactory;
import org.dozer.MappingException;
import org.dozer.config.BeanContainer;
import org.dozer.vo.interfacerecursion.User;
import org.dozer.vo.interfacerecursion.UserGroup;
import org.dozer.vo.interfacerecursion.UserGroupImpl;
import org.dozer.vo.interfacerecursion.UserGroupPrime;
import org.dozer.vo.interfacerecursion.UserGroupPrimeImpl;
import org.dozer.vo.interfacerecursion.UserImpl;
import org.dozer.vo.interfacerecursion.UserPrime;
import org.dozer.vo.interfacerecursion.UserPrimeImpl;

/**
 * @author Christoph Goldner 
 */
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
