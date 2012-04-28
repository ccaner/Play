package play.resultsetmock.jdbc;

import net.sf.cglib.proxy.Enhancer;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public abstract class MockJdbcFactory {
    
    public static DataSource createDataSource(Object model) {
        return (DataSource) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{DataSource.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getConnection")) {
                        return createConnection();
                    }
                    return null;
                }
            });
    }

    public static Connection createConnection() {
        return (Connection) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{Connection.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("getConnection")) {
                            return createConnection();
                        }
                        return null;
                    }
                });

    }
    
    public static <T> ResultSet createResultSet(List<T> backingList) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(BeanBackedResultSet.class);
        enhancer.setCallback(new BeanBackedResultSet.Interceptor());
        Object rs = enhancer.create(new Class[]{List.class}, new Object[]{backingList});
        return (ResultSet) rs;
    }
}
