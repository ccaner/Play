package play.resultsetmock.jdbc.data;

public interface TabularDataProvider {

    Object getByIndex(int row, int index);

    Object getByLabel(int row, String label);

    int size(); // TODO not really needed, remove this 

}
