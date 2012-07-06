package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotationUtils;
import play.resultsetmock.annotations.Query;

import java.lang.reflect.Method;
import java.sql.*;
import java.util.List;

/**
 * A connection implementation capable of creating Statement, PreparedStatement and CallableStatements.
 */
public abstract class SimpleConnection implements Connection {
    
    private Object model;

    public SimpleConnection(Object model) {
        this.model = model;
    }

    @Override
    public Statement createStatement() throws SQLException {
        return null;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        return SimplePreparedStatement.createInstance(model, getQueryMethod(model, sql));
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        return null;
    }

    public static Method getQueryMethod(Object model, String sql) {
        for (Method method : model.getClass().getDeclaredMethods()) {
            Query query = AnnotationUtils.findAnnotation(method, Query.class);
            if (query != null && sql.contains(query.value())) {
                return method;
            }
        }
        return null;
    }

    static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == SimpleConnection.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            SimpleConnection conn = (SimpleConnection) obj;
            if (method.getName().equals("createStatement")) {
                return conn.createStatement();
            } else if (method.getName().equals("prepareStatement")) {
                return conn.prepareStatement((String) args[0]);
            } else if (method.getName().equals("prepareCall")) {
                return conn.prepareCall((String) args[0]);
            } else {
                return Defaults.defaultValue(method.getReturnType());
            }
        }
    }

    public static <T> SimpleConnection createInstance(Object model) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SimpleConnection.class);
        enhancer.setCallback(new SimpleConnection.Interceptor());
        Object rs = enhancer.create(new Class[]{Object.class}, new Object[]{model});
        return (SimpleConnection) rs;
    }

}
