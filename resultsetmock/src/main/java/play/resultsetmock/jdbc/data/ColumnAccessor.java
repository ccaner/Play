package play.resultsetmock.jdbc.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.resultsetmock.annotations.RsColumn;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.MethodDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

public interface ColumnAccessor {
    
    Object getColumn(Object row, int index);

    Object getColumn(Object row, String label);

    public static ColumnAccessor EMPTY = new ColumnAccessor() {

        @Override
        public Object getColumn(Object row, int index) {
            throw new IndexOutOfBoundsException("Called get on an empty result set");
        }

        @Override
        public Object getColumn(Object row, String label) {
            throw new IndexOutOfBoundsException("Called get on an empty result set");
        }

    };

    public static ColumnAccessor ANNOTATION = new ColumnAccessor() {

        private Logger logger = LoggerFactory.getLogger(ColumnAccessor.class);
        
        /* may be better to introduce another layer for these */
        private final Map<Class,Map<Integer, MethodDescriptor>> byIndex = new HashMap<Class, Map<Integer, MethodDescriptor>>();
        private final Map<Class, Map<String, MethodDescriptor>> byLabel = new HashMap<Class, Map<String, MethodDescriptor>>();

        @Override
        public Object getColumn(Object row, int index) {
            Map<Integer, MethodDescriptor> cols = byIndex.get(row.getClass());
            if (cols == null) {
                introspect(row.getClass());
                cols = byIndex.get(row.getClass());
            }
            MethodDescriptor md = cols.get(index);
            if (md == null) {
                throw new IllegalArgumentException("Don't know how to get index " + index + " on "
                        + row.getClass() + " using annotations");
            }
            try {
                return md.getMethod().invoke(row);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Object getColumn(Object row, String label) {
            Map<String, MethodDescriptor> cols = byLabel.get(row.getClass());
            if (cols == null) {
                introspect(row.getClass());
                cols = byLabel.get(row.getClass());
            }
            MethodDescriptor md = cols.get(label.toUpperCase());
            if (md == null) {
                throw new IllegalArgumentException("Don't know how to get label " + label + " on "
                        + row.getClass() + " using annotations");
            }
            try {
                return md.getMethod().invoke(row);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

      /*
           Quite strict for now.
           RsColumn annotation has to be provided. Column label has to be provided (as value in RsColumn).
           Index is not mandatory, but any access to a not indexed column will result in an exception
        */
        private void introspect(Class clazz) {
            try {
                Map<Integer, MethodDescriptor> innerByIndex = new HashMap<Integer, MethodDescriptor>();
                Map<String, MethodDescriptor> innerByLabel = new HashMap<String, MethodDescriptor>();
                byIndex.put(clazz, innerByIndex);
                byLabel.put(clazz, innerByLabel);
                BeanInfo beanInfo = Introspector.getBeanInfo(clazz);
                MethodDescriptor[] propertyDescriptors = beanInfo.getMethodDescriptors();
                for (MethodDescriptor pd : propertyDescriptors) {
                    RsColumn rsColumn = pd.getMethod().getAnnotation(RsColumn.class);
                    if (rsColumn != null) {
                        if (pd.getMethod().getParameterTypes().length != 0 ||
                                pd.getMethod().getReturnType() == Void.TYPE) {
                            // not suitable as a column accessor. ignoring...
                        }
                        innerByLabel.put(rsColumn.name().toUpperCase(), pd);
                        if (rsColumn.index() > -1) {
                            innerByIndex.put(rsColumn.index(), pd);
                        }
                    }
                }
            } catch (IntrospectionException e) {
                throw new RuntimeException(e);
            }
        }

    };
}
