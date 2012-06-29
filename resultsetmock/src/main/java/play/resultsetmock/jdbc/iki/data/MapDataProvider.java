package play.resultsetmock.jdbc.iki.data;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 6/29/12
 * Time: 5:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapDataProvider implements TabularDataProvider {

    private Map<?, ?> data;

    public MapDataProvider(Map<?, ?> data) {
        this.data = data;
    }

    @Override
    public Object getByIndex(int row, int index) {

    }

    @Override
    public Object getByLabel(int row, String label) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int size() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
