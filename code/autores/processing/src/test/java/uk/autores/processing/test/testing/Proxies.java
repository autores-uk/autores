// Copyright 2023 https://github.com/autores-uk/autores/blob/main/LICENSE.txt
// SPDX-License-Identifier: Apache-2.0
package uk.autores.processing.test.testing;

import java.lang.reflect.*;

public final class Proxies {

    private Proxies() {}

    public static <T> Params<T> instance(Class<T> proxyType, String name) {
        try {
            ClassLoader cl = Proxies.class.getClassLoader();
            Class<?> c = cl.loadClass(name);
            return (Class<?>...params) -> instance(cl, proxyType, c, params);
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static <T> Args<T> instance(ClassLoader cl, Class<T> proxyType, Class<?> c, Class<?>[] ctorTypes) {
        try {
            Constructor<?> ctor = c.getDeclaredConstructor(ctorTypes);
            ctor.setAccessible(true);
            return (Object...args) -> instance(cl, proxyType, c, ctor, args);
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e);
        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T instance(ClassLoader cl, Class<T> proxyType, Class<?> c, Constructor<?> ctor, Object[] ctorArgs) {
        try {
            Object inst = ctor.newInstance(ctorArgs);
            Class<?>[] interfaces = {proxyType};
            Object proxy = Proxy.newProxyInstance(cl, interfaces, new IH(c, inst));
            return (T) proxy;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError(e);
        }

    }

    @SuppressWarnings("unchecked")
    public static <T> T utility(Class<T> proxyType, String name) {
        try {
            ClassLoader cl = Proxies.class.getClassLoader();
            Class<?> c = cl.loadClass(name);
            Class<?>[] interfaces = {proxyType};
            Object proxy = Proxy.newProxyInstance(cl, interfaces, new IH(c, null));
            return (T) proxy;
        } catch (ClassNotFoundException e) {
            throw new AssertionError(e);
        }
    }

    private static final class IH implements InvocationHandler {

        private final Class<?> c;
        private final Object instance;

        private IH(Class<?> c, Object instance) {
            this.c = c;
            this.instance = instance;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String name = method.getName();
            Method proxiedMethod = c.getDeclaredMethod(name, method.getParameterTypes());
            proxiedMethod.setAccessible(true);
            try {
                Object result = proxiedMethod.invoke(instance, args);
                if (result == instance) {
                    return proxy;
                }
                return result;
            } catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }

    public interface Params<T> {
        Args<T> params(Class<?>...types);
    }

    public interface Args<T> {
        T args(Object...args);
    }
}
