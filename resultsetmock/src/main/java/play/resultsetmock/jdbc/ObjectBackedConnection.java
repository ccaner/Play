package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.resultsetmock.annotations.Query;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 */
public abstract class ObjectBackedConnection implements Connection {
    
    private Object model;

    static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == ObjectBackedConnection.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            ObjectBackedConnection conn = (ObjectBackedConnection) obj;
            if (method.getName().equals("prepareStatement") || method.getName().equals("prepareStatement")) {
                Method queryMethod = getQueryMethod(conn.model, (String) args[0]);
                if (queryMethod == null) {
                    throw new IllegalArgumentException("Cannot find loader method for sql :" + args[0]);
                }
                return MethodBackedPreparedStatement.createInstance(conn.model, queryMethod);
            }
            return Defaults.defaultValue(method.getReturnType());
        }

        public static Method getQueryMethod(Object model, String sql) {
            for (Method method : model.getClass().getDeclaredMethods()) {
                Query query = method.getAnnotation(Query.class);
                if (query != null && sql.contains(query.value())) {
                    return method;
                }
            }
            return null;
        }

    }

    public static <T> BeanBackedResultSet createInstance(Object model) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ObjectBackedConnection.class);
        enhancer.setCallback(new ObjectBackedConnection.Interceptor());
        Object rs = enhancer.create(new Class[]{List.class}, new Object[]{model});
        return (BeanBackedResultSet) rs;
    }
    

}
