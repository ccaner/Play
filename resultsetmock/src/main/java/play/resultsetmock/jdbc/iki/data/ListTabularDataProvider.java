package play.resultsetmock.jdbc.iki.data;

import java.util.List;

public abstract class ListTabularDataProvider implements TabularDataProvider {

    private List<?> data;

    protected ListTabularDataProvider(List<?> data) {
        this.data = data;
    }

    @Override
    public Object getByIndex(int row, int index) {
        return getColumn(data.get(row));
    }

    protected abstract Object getColumn(Object o);

    @Override
    public Object getByLabel(int row, String label) {
        return getColumn(data.get(row));
    }

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
