package play.resultsetmock.jdbc;

import com.google.common.base.Defaults;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.resultsetmock.jdbc.data.ResultSetDataProviderFactory;
import play.resultsetmock.jdbc.data.TabularDataProvider;

import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A TYPE_FORWARD_ONLY, CONCUR_READ_ONLY result set implementation.
 */
public abstract class DataProviderResultSet implements ResultSet {
    
    private static Logger logger = LoggerFactory.getLogger(DataProviderResultSet.class);
    
    private final TabularDataProvider data;
    private int index = -1;

    public DataProviderResultSet(TabularDataProvider data) {
        this.data = data;
    }

    private Object getValue(String columnLabel) throws SQLException {
        return data.getByLabel(index(), columnLabel);
    }

    private Object getValue(Integer columnIndex) throws SQLException {
        return data.getByIndex(index(), columnIndex);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        Object value = getValue(columnIndex);
        try {
            return ResultSetDataProviderFactory.createDataProvider(value);
        } catch (IllegalArgumentException e) {
            logger.debug("Tried to intercept getObject invocation but didn't succeed.", e);
            return value;
        }
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        Object value = getValue(columnLabel);
        try {
            return ResultSetDataProviderFactory.createDataProvider(value);
        } catch (IllegalArgumentException e) {
            logger.debug("Tried to intercept getObject invocation but didn't succeed.", e);
            return value;
        }
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

    public static <T> DataProviderResultSet createInstance(TabularDataProvider dataProvider) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(DataProviderResultSet.class);
        enhancer.setCallback(new Interceptor());
        Object rs = enhancer.create(new Class[]{TabularDataProvider.class}, new Object[]{dataProvider});
        return (DataProviderResultSet) rs;
    }

    /*
        Effectively overrides <code>ResultSet</code> get methods.
     */
    private static class Interceptor implements MethodInterceptor {

        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (method.getDeclaringClass() == DataProviderResultSet.class) {
                return methodProxy.invokeSuper(obj, args);
            }
            DataProviderResultSet mockResultSet = (DataProviderResultSet) obj;
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

}
