package play.resultsetmock.jdbc.data;

public class DelegatingTabularDataProvider implements TabularDataProvider {
    
    private Object rs;
    
    private final RowAccessor rowAccessor;
    private final ColumnAccessor columnAccessor;

    public DelegatingTabularDataProvider(Object rs,
                                         RowAccessor rowAccessor,
                                         ColumnAccessor columnAccessor) {
        this.rs = rs;
        this.rowAccessor = rowAccessor;
        this.columnAccessor = columnAccessor;
    }

    @Override
    public Object getByIndex(int row, int index) {
        return columnAccessor.getColumn(rowAccessor.getRow(rs, row), index);
    }

    @Override
    public Object getByLabel(int row, String label) {
        return columnAccessor.getColumn(rowAccessor.getRow(rs, row), label);
    }

    @Override
    public int size() {
        return rowAccessor.size(rs);
    }
}
