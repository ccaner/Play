package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.sql.CallableStatement;

public abstract class MethodBackedCallableStatement implements CallableStatement {

    private Invocation invocation;

    protected MethodBackedCallableStatement(Object target, Method method) {
        this.invocation = new Invocation(target, method);
    }

    static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == MethodBackedCallableStatement.class) {
                return methodProxy.invokeSuper(obj, args);
            }

            return Defaults.defaultValue(method.getReturnType());
        }

    }
    
    static class Invocation {
        
        Object obj;
        Method method;
        Object[] args;
        Object result;

        Invocation(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
        }
    }

}
