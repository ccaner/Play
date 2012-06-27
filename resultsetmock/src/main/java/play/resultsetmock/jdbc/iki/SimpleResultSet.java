package play.resultsetmock.jdbc.iki;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * A TYPE_FORWARD_ONLY, CONCUR_READ_ONLY result set implementation.
 */
public abstract class SimpleResultSet implements ResultSet {
    
    private final TabularDataProvider data;
    private int index = -1;

    public SimpleResultSet(TabularDataProvider data) {
        this.data = data;
    }

    private Object getValue(String columnLabel) throws SQLException,
            InvocationTargetException, IllegalAccessException {
        return data.getByLabel(index(), columnLabel);
    }

    private Object getValue(Integer columnIndex) throws SQLException,
            InvocationTargetException, IllegalAccessException {
        return data.getByIndex(index(), columnIndex);
    }

    private int index() {
        if (index >= data.size()) {
            throw new IndexOutOfBoundsException();
        }
        return index;
    }

    @Override
    public boolean next() throws SQLException {
        return ++index < data.size();
    }

    @Override
    public int getRow() throws SQLException {
        return index + 1;
    }

    /* may be better to introduce another layer for these */
/*
    private final Map<Integer, MethodDescriptor> byIndex = new HashMap<Integer, MethodDescriptor>();
    private final Map<String, MethodDescriptor> byLabel = new HashMap<String, MethodDescriptor>();
*/


    /*
       Quite strict for now.
       RsColumn annotation has to be provided. Column label has to be provided (as value in RsColumn).
       Index is not mandatory, but any access to a not indexed column will result in an exception
    */
/*
    private void introspect() {
        if (rows.size() == 0) {
            return;
        }
        try {
            BeanInfo beanInfo = Introspector.getBeanInfo(rows.get(0).getClass());
            MethodDescriptor[] propertyDescriptors = beanInfo.getMethodDescriptors();
            for (MethodDescriptor pd : propertyDescriptors) {
                RsColumn rsColumn = pd.getMethod().getAnnotation(RsColumn.class);
                if (rsColumn != null) {
                    if (pd.getMethod().getParameterTypes().length != 0 ||
                            pd.getMethod().getReturnType() == Void.TYPE) {
                        // not suitable as a column accessor. ignoring...
                    }
                    byLabel.put(rsColumn.value(), pd);
                    if (rsColumn.index() > -1) {
                        byIndex.put(rsColumn.index(), pd);
                    }
                }
            }
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
    }
*/
    public static <T> SimpleResultSet createInstance(List<T> backingList) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(SimpleResultSet.class);
        enhancer.setCallback(new Interceptor());
        Object rs = enhancer.create(new Class[]{List.class}, new Object[]{backingList});
        return (SimpleResultSet) rs;
    }

    /*
        Effectively overrides <code>ResultSet</code> get methods.
     */
    private static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == SimpleResultSet.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            SimpleResultSet mockResultSet = (SimpleResultSet) obj;
            String mName = method.getName();
            Class<?>[] pTypes = method.getParameterTypes();
            boolean isColumnGetter = mName.startsWith("get") && pTypes.length == 1;
            if (isColumnGetter && pTypes[0] == Integer.TYPE) { // retrieve by column idx
                return mockResultSet.getValue((Integer) args[0]);
            }
            if (isColumnGetter && pTypes[0] == String.class) { // retrieve by column label
                return mockResultSet.getValue((String) args[0]);
            }
            return Defaults.defaultValue(method.getReturnType());
        }

    }

    public interface TabularDataProvider {

        Object getByIndex(int row, int index);

        Object getByLabel(int row, String label);

        int size();

    }

}
