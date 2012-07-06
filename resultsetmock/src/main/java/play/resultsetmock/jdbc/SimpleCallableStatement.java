package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.resultsetmock.jdbc.data.ResultSetDataProviderFactory;
import play.resultsetmock.jdbc.data.TabularDataProvider;
import play.resultsetmock.util.Invocation;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class SimpleCallableStatement implements CallableStatement {

    private Invocation invocation;

    @Override
    @SuppressWarnings("unchecked")
    public boolean execute() throws SQLException {
        try {
            Object result = invocation.invoke();
            if (result instanceof ResultSet) {
                return (ResultSet) result;
            }
            TabularDataProvider dataProvider = ResultSetDataProviderFactory.createDataProvider(result);
            return DataProviderResultSet.createInstance(dataProvider);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean execute() throws SQLException {
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResultSet getResultSet() throws SQLException {
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean getMoreResults() throws SQLException {
    }

    protected SimpleCallableStatement(Object target, Method method) {
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
