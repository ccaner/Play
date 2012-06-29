package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import org.mockito.Mock;
import org.mockito.Mockito;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.ResultSet;

import static org.mockito.Mockito.withSettings;

public class MockJdbcFactory {

    /**
     * Creates a DataSource that fetches its data by invoking methods on the given object
     * Searches for <code>@Query</code> on the object
     */
    public static DataSource createDataSource(final Object database) {
        return (DataSource) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{DataSource.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getConnection")) {
                        return ObjectBackedConnection.createInstance(database);
                    }
                    return Defaults.defaultValue(method.getReturnType());
                }
            });
    }

    /**
     * Creates a DataSource that fetches its data by invoking methods on the given object
     * Searches for <code>@Query</code> on the object
     */
    public static <T> DataSource mockDataSource(Class<T> interfaceClass) {
        T mock = Mockito.mock(interfaceClass);
        return createDataSource(mock);
    }


    /**
     * Gives you the sql methods... and it is also a DataSource so you can inject
     */
/*
    public static <T, Z extends T & DataSource> Z mockDataSource(Class<T> interfaceClass) {
        T mock = Mockito.mock(interfaceClass, withSettings().extraInterfaces(javax.sql.DataSource.class));
        return (Z) mock;
    }

    public static <T, Z extends T & ResultSet> Z mockResultSet(Class<T> interfaceClass) {
        T mock = Mockito.mock(interfaceClass, withSettings().extraInterfaces(javax.sql.DataSource.class));
        return (Z) mock;
    }
*/


}
