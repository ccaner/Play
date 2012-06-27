package play.resultsetmock.util;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 5/2/12
 * Time: 5:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class AbstractClassInitializer {

    public static <T> T initializeAbstractClass(final Class<T> clazz) {
        return initializeAbstractClass(clazz, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T initializeAbstractClass(final Class<? extends T> clazz, Class[] paramTypes, Object[] args) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                if (method.getDeclaringClass() == clazz) {
                    return methodProxy.invokeSuper(obj, args);
                }
                return Defaults.defaultValue(method.getReturnType());
            }
        });
        Object instance;
        if (paramTypes == null) {
            instance = enhancer.create();
        } else {
            instance = enhancer.create(paramTypes, args);
        }
        return (T) instance;
    }

}
