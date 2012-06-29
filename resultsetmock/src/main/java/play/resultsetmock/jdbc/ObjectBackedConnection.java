package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.springframework.core.annotation.AnnotationUtils;
import play.resultsetmock.annotations.Query;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;

/**
 */
public abstract class ObjectBackedConnection implements Connection {
    
    private Object model;

    protected ObjectBackedConnection(Object model) {
        this.model = model;
    }

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
                Query query = AnnotationUtils.findAnnotation(method, Query.class);
                if (query != null && sql.contains(query.value())) {
                    return method;
                }
                
                
            }
            return null;
        }

    }

    public static <T> Connection createInstance(Object model) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(ObjectBackedConnection.class);
        enhancer.setCallback(new ObjectBackedConnection.Interceptor());
        Object conn = enhancer.create(new Class[]{Object.class}, new Object[]{model});
        return (Connection) conn;
    }
    

}
