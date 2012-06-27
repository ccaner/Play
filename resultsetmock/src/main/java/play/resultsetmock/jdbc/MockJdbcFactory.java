package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public abstract class MockJdbcFactory {
    
    public static DataSource createDataSource(final Object model) {
        return (DataSource) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{DataSource.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getConnection")) {
                        return ObjectBackedConnection.createInstance(model);
                    }
                    return Defaults.defaultValue(method.getReturnType());
                }
            });
    }

}
