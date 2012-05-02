package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import javax.sql.DataSource;
import java.io.ObjectStreamConstants;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.List;

public abstract class MockJdbcFactory {
    
    public static DataSource createDataSource(final Object model) {
        return (DataSource) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{DataSource.class},
            new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if (method.getName().equals("getConnection")) {
                        return createConnection(model);
                    }
                    return null;
                }
            });
    }

    public static Connection createConnection(Object model) {
        
        return (Connection) Proxy.newProxyInstance(MockJdbcFactory.class.getClassLoader(), new Class[]{Connection.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        if (method.getName().equals("prepareStatement")) {
                            return createPreparedStatement(args[0]);
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

    public static ResultSet createPreparedStatement(Object model, Method method) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(MethodBackedPreparedStatement.class);
        enhancer.setCallback(new MethodBackedPreparedStatement.Interceptor());
        Object rs = enhancer.create(new Class[]{Object.class, Method.class}, new Object[]{model, method});
        return (ResultSet) rs;
    }

    public static <T> T initializeAbstractClass(final Class<T> clazz) {
        return initializeAbstractClass(clazz, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T initializeAbstractClass(final Class<T> clazz, Class[] paramTypes, Object[] args) {
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
