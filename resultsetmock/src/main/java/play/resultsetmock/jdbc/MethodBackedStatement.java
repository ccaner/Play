package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.resultsetmock.jdbc.iki.SimpleResultSet;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * An sql statement implementation that performs a method invocation.
 *
 * CallableStatement interface is chosen to make implementation as generic as possible (hopefully)
 */
public abstract class MethodBackedStatement implements CallableStatement {

    private Invocation invocation;

    @Override
    @SuppressWarnings("unchecked")
    public ResultSet executeQuery() throws SQLException {
        try {
            return SimpleResultSet.createInstance((List<Object>) invocation.invoke());
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    protected MethodBackedStatement(Object target, Method method) {
        this.invocation = new Invocation(target, method);
    }

    private void setArgument(int idx, Object val) {
        invocation.setArgument(idx, val);
    }

    private static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == MethodBackedStatement.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            MethodBackedStatement mockPs = (MethodBackedStatement) obj;
            String mName = method.getName();
            Class<?>[] pTypes = method.getParameterTypes();
            boolean isSetter = mName.startsWith("set") && pTypes.length == 2;
            if (isSetter && pTypes[0] == Integer.TYPE) { // set by column idx
                mockPs.setArgument((Integer) args[0], args[1]);
                return null;
            }
            return Defaults.defaultValue(method.getReturnType());
        }

    }

    private static class Invocation {

        final Object obj;
        final Method method;
        final Object[] args;

        Invocation(Object obj, Method method) {
            this.obj = obj;
            this.method = method;
            args = new Object[method.getParameterTypes().length];
        }

        private void setArgument(int index, Object val) {
            args[index - 1] = val;
        }

        private Object invoke() throws InvocationTargetException, IllegalAccessException {
            return method.invoke(obj, args);
        }

    }

    public static PreparedStatement createInstance(Object model, Method method) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MethodBackedStatement.class);
        enhancer.setCallback(new Interceptor());
        Object rs = enhancer.create(new Class[]{Object.class, Method.class}, new Object[]{model, method});
        return (PreparedStatement) rs;
    }

}
