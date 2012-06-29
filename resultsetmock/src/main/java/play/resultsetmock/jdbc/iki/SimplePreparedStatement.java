package play.resultsetmock.jdbc.iki;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.resultsetmock.jdbc.iki.data.TabularDataProvider;
import play.resultsetmock.jdbc.util.Invocation;

import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SimplePreparedStatement extends SimpleStatement implements PreparedStatement {

    private Invocation invocation;

    @Override
    @SuppressWarnings("unchecked")
    public ResultSet executeQuery() throws SQLException {
        try {
            Object result = invocation.invoke();
            TabularDataProvider dataProvider = ResultSetDataProviderFactory.createDataProvider(result);
            return SimpleResultSet.createInstance(dataProvider);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    protected SimplePreparedStatement(Object target, Method method) {
        this.invocation = new Invocation(target, method);
    }

    private void setArgument(int idx, Object val) {
        invocation.setArgument(idx, val);
    }

    private static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == SimplePreparedStatement.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            SimplePreparedStatement mockPs = (SimplePreparedStatement) obj;
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

    public static PreparedStatement createInstance(Object model, Method method) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SimplePreparedStatement.class);
        enhancer.setCallback(new Interceptor());
        Object rs = enhancer.create(new Class[]{Object.class, Method.class}, new Object[]{model, method});
        return (PreparedStatement) rs;
    }


}
