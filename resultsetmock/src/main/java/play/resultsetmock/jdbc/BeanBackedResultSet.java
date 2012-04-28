package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import play.resultsetmock.annotations.RsColumn;

import java.beans.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public abstract class BeanBackedResultSet<T> implements ResultSet {

    private final List<T> rows;
    private int index = -1;

    /* may be better to introduce another layer for these */
    private final Map<Integer, MethodDescriptor> byIndex = new HashMap<Integer, MethodDescriptor>();
    private final Map<String, MethodDescriptor> byLabel = new HashMap<String, MethodDescriptor>();

    public BeanBackedResultSet(List<T> rows) {
        this.rows = rows == null ? Collections.<T>emptyList() : new ArrayList<T>(rows);
        introspect();
    }

    private Object getValue(String columnLabel) throws SQLException,
            InvocationTargetException, IllegalAccessException {
        T row = currentRow();
        MethodDescriptor pd = byLabel.get(columnLabel);
        if (pd == null) {
            throw new SQLException("Class does not have a column named (" + columnLabel + ") in RsColumn annotation");
        }
        return pd.getMethod().invoke(row);
    }

    private Object getValue(Integer columnIndex) throws SQLException,
            InvocationTargetException, IllegalAccessException {
        T row = currentRow();
        MethodDescriptor pd = byIndex.get(columnIndex);
        if (pd == null) {
            throw new SQLException("Class does not have a column indexed (" + columnIndex + ") in RsColumn annotation");
        }
        return pd.getMethod().invoke(row);
    }

    private T currentRow() {
        if (index >= rows.size()) {
            throw new IndexOutOfBoundsException();
        }
        return rows.get(index);
    }

    @Override
    public boolean next() throws SQLException {
        return ++index < rows.size();
    }

    @Override
    public int getRow() throws SQLException {
        return index + 1;
    }

    /*
       Quite strict for now.
       RsColumn annotation has to be provided. Column label has to be provided (as value in RsColumn).
       Index is not mandatory, but any access to a not indexed column will result in an exception
    */
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
                        // not suitable as a column accessor ignoring
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
    
    static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == BeanBackedResultSet.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            BeanBackedResultSet<?> mockResultSet = (BeanBackedResultSet<?>) obj;
            String mName = method.getName();
            Class<?>[] pTypes = method.getParameterTypes();
            boolean isColumnGetter = mName.startsWith("get") && pTypes.length == 1;
            if (isColumnGetter && pTypes[0] == Integer.TYPE) { // retrieve by column idx
                return mockResultSet.getValue((Integer) args[0]);
            }
            if (isColumnGetter && pTypes[0] == String.class) { // retrieve by column name
                return mockResultSet.getValue((String) args[0]);
            }
            return Defaults.defaultValue(method.getReturnType());
        }

    }

}
