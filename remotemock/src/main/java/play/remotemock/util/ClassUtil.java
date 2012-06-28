package play.remotemock.util;

import org.mockito.asm.Type;
import org.mockito.cglib.core.Signature;
import org.mockito.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

public class ClassUtil {
    
    public static Class getDeclaringClass(Object object, Method method) {
        try {
            Method overridden = object.getClass().getMethod(method.getName(), method.getParameterTypes());
            return overridden.getDeclaringClass();
        } catch (NoSuchMethodException e) {
            return method.getDeclaringClass();
        }
    }
    
    public static boolean declaresMethod(Class clazz, Method method) {
        try {
            clazz.getMethod(method.getName(), method.getParameterTypes());
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }
    
    public static Object invokeMethodOnProxy(Object proxy, Method method, Object[] args) throws Throwable {
        MethodProxy methodProxy = MethodProxy.find(proxy.getClass(), new Signature(method.getName(),
                Type.getReturnType(method),
                Type.getArgumentTypes(method)));
        return methodProxy.invokeSuper(proxy, args);
    }
    
}
