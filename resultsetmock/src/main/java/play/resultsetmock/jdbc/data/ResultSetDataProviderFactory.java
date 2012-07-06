package play.resultsetmock.jdbc.data;

import play.resultsetmock.annotations.RsColumn;

import java.lang.reflect.Method;
import java.util.List;

public class ResultSetDataProviderFactory {

    public static TabularDataProvider createDataProvider(Object result) {
        if (result instanceof TabularDataProvider) {
            return (TabularDataProvider) result;
        }
        RowAccessor rowAccessor;
        ColumnAccessor columnAccessor;

        if (result instanceof List) {
            rowAccessor = RowAccessor.LIST;
        } else {
            throw new UnknownResultSetTypeException("Don't know how to handle " + result.getClass());
        }
        
        if (rowAccessor.size(result) == 0) {
            columnAccessor = ColumnAccessor.EMPTY;
        } else if (annotationPresent(rowAccessor.getRow(result, 0))) {
            columnAccessor = ColumnAccessor.ANNOTATION;
        } else {
            throw new IllegalArgumentException("Don't know how to handle " + rowAccessor.getRow(result, 1).getClass() +
                    " as a row");
        }
        return new DelegatingTabularDataProvider(result, rowAccessor, columnAccessor);
    }

    private static boolean annotationPresent(Object row) {
        for (Method method : row.getClass().getMethods()) {
            if (method.isAnnotationPresent(RsColumn.class)) {
                return true;
            }
        }
        return false;
    }
}
