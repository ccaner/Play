package play.resultsetmock.jdbc;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 4:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class MockJdbcFactory {
    
    
    public static DataSource createDataSource() {
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
}
