package play.resultsetmock.model.provider;

import play.resultsetmock.model.query.Query;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: akpinarc
 * Date: 4/27/12
 * Time: 3:42 PM
 * To change this template use File | Settings | File Templates.
 */
public class MapDataProvider implements DataProvider {

    Map db;


    @Override
    public <T> T query(Query<T> query) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
