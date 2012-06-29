package play.resultsetmock.jdbc.iki.data;

public interface TabularDataProvider {

    Object getByIndex(int row, int index);

    Object getByLabel(int row, String label);

    int size();

    public interface RowAccessor {
        
        Object getRow(int index);
    }

    public interface ColumnAccessor {
        
        Object getColumn(int index);
        
        Object getColumn(String label);
        
    }

}
