package uk.autores.test.testing;

import java.lang.reflect.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public final class Proxies {

    private Proxies() {}

    @SuppressWarnings("unchecked")
    public static <T> T instance(Class<T> proxyType, String name, Class<?>[] ctorTypes, Object[] ctorArgs) {
        assertEquals(ctorTypes.length, ctorArgs.length);
        try {
            ClassLoader cl = Proxies.class.getClassLoader();
            Class<?> c = cl.loadClass(name);
            Constructor<?> ctor = c.getDeclaredConstructor(ctorTypes);
            ctor.setAccessible(true);
            Object inst = ctor.newInstance(ctorArgs);
            Class<?>[] interfaces = {proxyType};
            Object proxy = Proxy.newProxyInstance(cl, interfaces, new IH(c, inst));
            return (T) proxy;
        } catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException e) {
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
}
